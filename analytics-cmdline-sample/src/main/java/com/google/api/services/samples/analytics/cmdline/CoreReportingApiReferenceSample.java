/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.api.services.samples.analytics.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.analytics.v4.Analytics;
import com.google.api.services.analytics.v4.AnalyticsScopes;
import com.google.api.services.analytics.v4.model.Cohort;
import com.google.api.services.analytics.v4.model.CohortGroup;
import com.google.api.services.analytics.v4.model.ColumnHeader;
import com.google.api.services.analytics.v4.model.DateRange;
import com.google.api.services.analytics.v4.model.DateRangeValues;
import com.google.api.services.analytics.v4.model.Dimension;
import com.google.api.services.analytics.v4.model.DimensionFilter;
import com.google.api.services.analytics.v4.model.DimensionFilterClause;
import com.google.api.services.analytics.v4.model.DynamicSegment;
import com.google.api.services.analytics.v4.model.GetReportsRequest;
import com.google.api.services.analytics.v4.model.GetReportsResponse;
import com.google.api.services.analytics.v4.model.Metric;
import com.google.api.services.analytics.v4.model.MetricFilter;
import com.google.api.services.analytics.v4.model.MetricFilterClause;
import com.google.api.services.analytics.v4.model.MetricHeader;
import com.google.api.services.analytics.v4.model.MetricHeaderEntry;
import com.google.api.services.analytics.v4.model.OrFiltersForSegment;
import com.google.api.services.analytics.v4.model.OrderBy;
import com.google.api.services.analytics.v4.model.Pivot;
import com.google.api.services.analytics.v4.model.PivotHeader;
import com.google.api.services.analytics.v4.model.PivotHeaderEntry;
import com.google.api.services.analytics.v4.model.PivotValue;
import com.google.api.services.analytics.v4.model.Report;
import com.google.api.services.analytics.v4.model.ReportData;
import com.google.api.services.analytics.v4.model.ReportRequest;
import com.google.api.services.analytics.v4.model.ReportRow;
import com.google.api.services.analytics.v4.model.Segment;
import com.google.api.services.analytics.v4.model.SegmentDefinition;
import com.google.api.services.analytics.v4.model.SegmentDimensionFilter;
import com.google.api.services.analytics.v4.model.SegmentFilter;
import com.google.api.services.analytics.v4.model.SegmentFilterClause;
import com.google.api.services.analytics.v4.model.SimpleSegment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This application demonstrates how to use the Google Analytics Java client library to access all
 * the pieces of data returned by the Google Analytics Core Reporting API v4.
 *
 * <p>
 * To run this, you must supply your Google Analytics TABLE ID and USER ID. Read the Core Reporting
 * API
 * developer guide to learn how to get these values.
 * </p>
 *
 * @author ikuleshov@google.com
 */
public class CoreReportingApiReferenceSample {
  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "Core Reporting API V4 Demo (Java)";

  /**
   * Replace this constant with your account user id
   */
  private static final String USER_ID = "<INSERT YOUR USER ID>";

  /**
   * Used to identify from which reporting profile to retrieve data. Format is ga:xxx where xxx is
   * your profile ID.
   */
  private static final String TABLE_ID = "<INSERT YOUR TABLE ID>";

  /**
   *  A port of the local HTTP server that will be used for OAuth2 redirect.
   */
  private static final int REDIRECT_HTTP_PORT = 8090;

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/analytics_sample");

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  /** Global instance of the HTTP transport. */
  private static HttpTransport HTTP_TRANSPORT;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /**
   * The global dimension definitions below will be used to construct queries to the Reporting API
   */
  private static final Dimension SOURCE_DIMENSION = new Dimension().setName("ga:source");
  private static final Dimension KEYWORD_DIMENSION = new Dimension().setName("ga:keyword");

  /**
   * This dimension definition groups the values of ga:sessionCount into four distinct
   * buckets: <10, [10-100), [100, 200), >= 200 sec
   */
  private static final Dimension BUCKETED_SESSION_COUNT_DIMENSION =
      new Dimension()
          .setName("ga:sessionCount")
          .setHistogramBuckets(Arrays.asList(10L, 100L, 200L));

  /**
   * ga:segment is a special dimension that holds the information about the row's segment.
   * Should only be present for queries using segments.
   * */
  private static final Dimension SEGMENT_DIMENSION = new Dimension().setName("ga:segment");

  /**
   * Main demo. Returns the top 25 organic
   * search keywords and traffic sources by visits. The Core Reporting
   * API is used to retrieve this data.
   *
   * @param args command line args.
   */
  public static void main(String[] args) {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
      Analytics analytics = initializeAnalytics();

      // Build and execute the queries

      System.out.println("Executing a simple request using a single date range:");
      runDemo(analytics, buildSimpleRequest(TABLE_ID));

      System.out.println("Executing a request using metrics/dimension filters:");
      runDemo(analytics, buildRequestWithFilters(TABLE_ID));

      System.out.println("Executing a request using both original and comparison date ranges:");
      runDemo(analytics, buildRequestWithComparisonDateRange(TABLE_ID));

      System.out.println("Executing a request using pivots feature:");
      runDemo(analytics, buildRequestWithPivots(TABLE_ID));

      System.out.println("Executing a request using segments feature:");
      runDemo(analytics, buildRequestWithSegments(TABLE_ID));

      System.out.println("Executing a request using cohorts feature:");
      runDemo(analytics, buildRequestWithCohorts(TABLE_ID));
      
    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
          + e.getDetails().getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * @param analytics
   * @param request
   * @throws IOException
   */
  private static void runDemo(Analytics analytics, ReportRequest request) throws IOException {
    GetReportsResponse response = executeDataQuery(analytics, request);
    printResponse(response);
  }

  /**
   * Process the response and output to the console.
   *
   * @param response
   */
  private static void printResponse(GetReportsResponse response) {
    System.out.println("*** RESPONSE ***");
    System.out.println("Query cost = " + response.getQueryCost());

    // Since there can be multiple reports corresponding to each portion of
    // the batched query, we are processing all reports from a response object.
    for (Report report : response.getReports()) {
      printReportInfo(report);
      printColumnHeaders(report);
      printTotalsForAllResults(report);
      printRows(report);
    }

    System.out.println("*** END OF RESPONSE ***");
    System.out.println();
  }

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY,
        new InputStreamReader(
            HelloAnalyticsApiSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics "
          + "into analytics-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }

    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow
            .Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
                Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY))
            .setDataStoreFactory(DATA_STORE_FACTORY)
            .build();

    // authorize
    return new AuthorizationCodeInstalledApp(
               flow, new LocalServerReceiver.Builder().setPort(REDIRECT_HTTP_PORT).build())
        .authorize(USER_ID);
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   *
   * @return an initialized Analytics service object.
   *
   * @throws Exception if an issue occurs with OAuth2Native authorize.
   */
  private static Analytics initializeAnalytics() throws Exception {
    // Authorization.
    Credential credential = authorize();

    // Set up and return Google Analytics API client.
    return new Analytics.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build();
  }

  private static ReportRequest buildReportRequest(String viewId) {
    return new ReportRequest()
        // The table ID of the profile you wish to access.
        .setViewId(viewId)
        // Optional limit on the maximum page size.
        .setPageSize(25)
        // Optional indication of the desired sampling level
        .setSamplingLevel("LARGE");
  }

  private static ReportRequest buildSimpleRequest(String viewId) {
    ReportRequest request = buildReportRequest(viewId);

    applyOrderBy(request);
    applySingleDateRange(request);
    applyMetrics(request);
    applyDimensions(request);

    return request;
  }

  private static ReportRequest buildRequestWithFilters(String viewId) {
    ReportRequest request = buildReportRequest(viewId);

    applyOrderBy(request);
    applyMetrics(request);
    applyDimensions(request);

    applySingleDateRange(request);
    applyDimensionFilter(request);
    applyMetricFilter(request);

    return request;
  }

  private static ReportRequest buildRequestWithComparisonDateRange(String viewId) {
    ReportRequest request = buildReportRequest(viewId);

    applyOrderBy(request);
    applyMetrics(request);
    applyDimensions(request);

    applyTwoDateRanges(request);

    return request;
  }

  private static ReportRequest buildRequestWithSegments(String viewId) {
    ReportRequest request = buildReportRequest(viewId);

    applyOrderBy(request);
    applySingleDateRange(request);
    applyMetrics(request);
    applyDimensionsWithSegment(request);

    applySegments(request);

    return request;
  }

  private static ReportRequest buildRequestWithPivots(String viewId) {
    ReportRequest request = buildReportRequest(viewId);

    applyOrderBy(request);
    applySingleDateRange(request);
    applyMetrics(request);
    applyDimensions(request);

    applyPivots(request);

    return request;
  }

  private static ReportRequest buildRequestWithCohorts(String viewId) {
    ReportRequest request = buildReportRequest(viewId);

    request.setDimensions(Arrays.asList(
        new Dimension().setName("ga:cohort"), new Dimension().setName("ga:cohortNthWeek")));
    request.setMetrics(Arrays.asList(
        new Metric().setExpression("ga:cohortTotalUsersWithLifetimeCriteria"),
        new Metric().setExpression("ga:cohortRevenuePerUser")));

    Cohort cohort1 =
        new Cohort()
            .setName("cohort_1")
            .setType("FIRST_VISIT_DATE")
            .setDateRange(new DateRange().setStartDate("2015-08-01").setEndDate("2015-09-01"));
    Cohort cohort2 =
        new Cohort()
            .setName("cohort21")
            .setType("FIRST_VISIT_DATE")
            .setDateRange(new DateRange().setStartDate("2015-07-01").setEndDate("2015-08-01"));
    CohortGroup cohortGroup = new CohortGroup();
    cohortGroup.setCohorts(Arrays.asList(cohort1, cohort2));
    cohortGroup.setLifetimeValue(true);
    request.setCohortGroup(cohortGroup);
    return request;
  }

  /**
   *
   *
   * @param analytics the Analytics service object used to access the API.
   * @param request Report request object that describes the query to Google Analytics Reporting API
   * @return the response from the API.
   * @throws IOException if an API error occurred.
   */
  private static GetReportsResponse executeDataQuery(Analytics analytics, ReportRequest request)
      throws IOException {
    // Building the batch report request
    GetReportsRequest batchRequest =
        new GetReportsRequest().setReportRequests(Arrays.asList(request));

    // Executing the batch request
    return analytics.reports().batchGet(batchRequest).execute();
  }


  /**
   * @param request
   */
  private static void applyDimensions(ReportRequest request) {
    // Dimensions definition
    List<Dimension> dimensions =
        Arrays.asList(SOURCE_DIMENSION, KEYWORD_DIMENSION, BUCKETED_SESSION_COUNT_DIMENSION);

    request.setDimensions(dimensions);
  }

  /**
   * @param request
   */
  private static void applyDimensionsWithSegment(ReportRequest request) {
    // Dimensions definition including Segment dimension
    List<Dimension> dimensions = Arrays.asList(
        SOURCE_DIMENSION, KEYWORD_DIMENSION, BUCKETED_SESSION_COUNT_DIMENSION, SEGMENT_DIMENSION);

    request.setDimensions(dimensions);
  }

  /**
   *  Metrics definition
   *
   * @param request
   */
  private static void applyMetrics(ReportRequest request) {
    List<Metric> metrics = Arrays.asList(
        new Metric().setExpression("ga:sessions"),
        new Metric().setExpression("ga:sessionDuration"),

        // Here we are using a calculated metric that is using a custom expression
        new Metric()
            .setExpression("ga:goal1Completions/ga:goal1Starts")
            .setFormattingType("FLOAT")
            .setAlias("Custom metric"));

    request.setMetrics(metrics);
  }

  /**
   * Creating a segment for users who are NOT from New York by applying a dimension
   * filter on ga:city and inverting the match using 'matchComplement'
   * property.
   *
   * @param request
   */
  private static void applySegments(ReportRequest request) {
    SegmentDimensionFilter segmentDimensionFilter =
        new SegmentDimensionFilter().setDimensionName("ga:city").setExpressions(
            Arrays.asList("New York"));

    SegmentFilterClause segmentFilterClause =
        new SegmentFilterClause().setDimensionFilter(segmentDimensionFilter);

    OrFiltersForSegment orFiltersForSegment =
        new OrFiltersForSegment().setSegmentFilterClauses(Arrays.asList(segmentFilterClause));

    SimpleSegment simpleSegment =
        new SimpleSegment().setOrFiltersForSegment(Arrays.asList(orFiltersForSegment));

    SegmentFilter segmentFilter =
        new SegmentFilter().setMatchComplement(true).setSimpleSegment(simpleSegment);

    SegmentDefinition segmentDefinition =
        new SegmentDefinition().setSegmentFilters(Arrays.asList(segmentFilter));

    DynamicSegment notFromNewYorkSegment =
        new DynamicSegment()
            .setName("Users NOT from New York")
            .setSessionSegment(segmentDefinition);

    Segment segment = new Segment().setDynamicSegment(notFromNewYorkSegment);

    request.setSegments(Arrays.asList(segment));
  }

  /**
   * Pivot definition.
   *
   * @param request
   */
  private static void applyPivots(ReportRequest request) {
    Metric sessionsMetric = new Metric().setExpression("ga:sessions");
    Dimension browserDimension = new Dimension().setName("ga:browser");

    Pivot pivot =
        new Pivot()
            .setMetrics(
                Arrays.asList(sessionsMetric, new Metric().setExpression("ga:sessionDuration")))
            .setDimensions(Arrays.asList(browserDimension));

    // Pivot dimension filter definition. Narrow down the number of browsers we are interested in.
    DimensionFilterClause pivotDimensionFilterClause =
        new DimensionFilterClause().setFilters(Arrays.asList(
            new DimensionFilter()
                .setDimensionName("ga:browser")
                .setOperator("IN_LIST")
                .setExpressions(Arrays.asList("Chrome", "Safari", "Firefox", "IE"))));
    pivot.setDimensionFilterClauses(Arrays.asList(pivotDimensionFilterClause));

    request.setPivots(Arrays.asList(pivot));
  }

  /**
   * Date range definition for a simple request.
   *
   * @param request
   */
  private static void applySingleDateRange(ReportRequest request) {
    DateRange originalDateRange = new DateRange();
    originalDateRange.setStartDate("2015-01-01");
    originalDateRange.setEndDate("2015-12-31");

    request.setDateRanges(Arrays.asList(originalDateRange));
  }

  /**
   * Date ranges definition. When two date ranges are specified, the second range will be used to
   * compare data against the first range.
   *
   * @param request
   */
  private static void applyTwoDateRanges(ReportRequest request) {
    DateRange originalDateRange = new DateRange();
    originalDateRange.setStartDate("2015-01-01");
    originalDateRange.setEndDate("2015-12-31");

    DateRange comparisonDateRange = new DateRange();
    comparisonDateRange.setStartDate("2014-01-01");
    comparisonDateRange.setEndDate("2014-12-31");

    request.setDateRanges(Arrays.asList(originalDateRange, comparisonDateRange));
  }

  /**
   * Dimension filters definition. Include only data from organic search results.
   *
   * @param request
   */
  private static void applyDimensionFilter(ReportRequest request) {
    DimensionFilter dimensionFilter =
        new DimensionFilter()
            .setDimensionName("ga:medium")
            .setOperator("EXACT")
            .setExpressions(Arrays.asList("organic"));

    DimensionFilterClause dimensionFilterClause =
        new DimensionFilterClause().setFilters(Arrays.asList(dimensionFilter));

    request.setDimensionFilterClauses(Arrays.asList(dimensionFilterClause));
  }

  /**
   * Metric filters definition.
   *
   * @param request
   */
  private static void applyMetricFilter(ReportRequest request) {
    MetricFilter sessionsMetricFilter =
        new MetricFilter()
            .setMetricName("ga:sessions")
            .setComparisonValue("0")
            .setOperator("GREATER_THAN");

    MetricFilter sessionDurationMetricFilter =
        new MetricFilter()
            .setMetricName("ga:sessionDuration")
            .setComparisonValue("0")
            .setOperator("GREATER_THAN");

    MetricFilterClause metricFilterClause =
        new MetricFilterClause()
            .setFilters(Arrays.asList(sessionsMetricFilter, sessionDurationMetricFilter))
            .setOperator("AND");

    request.setMetricFilterClauses(Arrays.asList(metricFilterClause));
  }

  /**
   * Order report by sessions count, descending.
   *
   * @param request
   */
  private static void applyOrderBy(ReportRequest request) {
    OrderBy orderBy = new OrderBy().setFieldName("ga:sessions desc").setOrderType("VALUE");
    request.setOrderBys(Arrays.asList(orderBy));
  }



  /**
   * Print the information for each column. The reporting data from the API is returned as rows of
   * data. The column headers describe the names and types of each column in rows.
   *
   * @param report the data returned from the API.
   */
  private static void printColumnHeaders(Report report) {
    System.out.println("Column Headers:");

    ColumnHeader headers = report.getColumnHeader();

    for (String dimensionName : headers.getDimensions()) {
      System.out.println("\tDimension name = " + dimensionName);
    }

    System.out.println();

    MetricHeader metricHeader = headers.getMetricHeader();
    printMetricHeader(metricHeader);
  }

  /**
   * Print metric name.
   *
   * @param metricHeader
   */
  private static void printMetricHeader(MetricHeader metricHeader) {
    for (MetricHeaderEntry metricHeaderEntry : metricHeader.getMetricHeaderEntries()) {
      System.out.println("\tMetric name = " + metricHeaderEntry.getName());
      System.out.println("\tMetric type = " + metricHeaderEntry.getType());
      System.out.println();
    }

    printPivotHeaders(metricHeader);
  }

  /**
   * @param metricHeader
   */
  private static void printPivotHeaders(MetricHeader metricHeader) {
    if (metricHeader.getPivotHeaders() != null) {
      for (PivotHeader pivotHeader : metricHeader.getPivotHeaders()) {
        System.out.println("\tPivot header:");
        System.out.println("\t\tPivot groups count: " + pivotHeader.getTotalPivotGroupsCount());
        if (pivotHeader.getPivotHeaderEntries() != null) {
          for (PivotHeaderEntry pivotHeaderEntry : pivotHeader.getPivotHeaderEntries()) {
            System.out.format(
                "\t\tPivot metric name = %s\n", pivotHeaderEntry.getMetric().getName());
            System.out.format(
                "\t\tPivot metric type = %s\n", pivotHeaderEntry.getMetric().getType());
            System.out.println();

            printPivotDimensions(pivotHeaderEntry);

            System.out.println();
          }
        }
      }
    }
  }

  /**
   * @param pivotHeaderEntry
   */
  private static void printPivotDimensions(PivotHeaderEntry pivotHeaderEntry) {
    for (int dimensionIndex = 0; dimensionIndex < pivotHeaderEntry.getDimensionNames().size();
        dimensionIndex++) {
      String dimensionName = pivotHeaderEntry.getDimensionNames().get(dimensionIndex);
      System.out.println("\t\t\tPivot dimension name = " + dimensionName);

      String dimensionValue = pivotHeaderEntry.getDimensionValues().get(dimensionIndex);
      System.out.println("\t\t\tPivot dimension value = " + dimensionValue);
    }
  }


  private static void printTotalsForAllResults(Report report) {
    ReportData data = report.getData();
    System.out.println("Total Metrics For All Results:");
    System.out.println("\tisDataGolden = " + data.getIsDataGolden());

    List<ReportRow> rows = data.getRows();
    System.out.format("\tThis query returned %s rows.\n", rows != null ? rows.size() : 0);
    System.out.format("\tBut the query matched %s total results.\n", data.getRowCount());
    System.out.println();

    System.out.println("Here are the metric TOTALS for the matched total results.");
    printDateRanges(data.getTotals(), report.getColumnHeader());

    System.out.println();

    System.out.println("Here are the metric MINIMUMS for the matched total results.");
    printDateRanges(data.getMinimums(), report.getColumnHeader());

    System.out.println();

    System.out.println("Here are the metric MAXIMUMS for the matched total results.");
    printDateRanges(data.getMaximums(), report.getColumnHeader());

    System.out.println();
  }

  private static void printDateRanges(List<DateRangeValues> dateRanges, ColumnHeader columnHeader) {
    for (int i = 0; i < dateRanges.size(); i++) {
      System.out.println("\tDate range #" + i);

      DateRangeValues dateRangeValues = dateRanges.get(i);

      printMetrics(dateRangeValues, columnHeader);
      printPivotValues(dateRangeValues, columnHeader);
    }
  }

  /**
   * @param columnHeader
   * @param dateRangeValues
   */
  private static void printMetrics(DateRangeValues dateRangeValues, ColumnHeader columnHeader) {
    List<String> values = dateRangeValues.getValues();

    for (int metricIndex = 0; metricIndex < values.size(); metricIndex++) {
      // Obtain a human readable metric name from the report column header
      String metricName =
          columnHeader.getMetricHeader().getMetricHeaderEntries().get(metricIndex).getName();
      String metricValue = values.get(metricIndex);

      System.out.println("\t\tMetric  = " + metricName);
      System.out.println("\t\tValue = " + metricValue);
      System.out.println();
    }
  }

  /**
   * Print pivot values, along with the corresponding metric name
   *
   * @param columnHeader
   * @param dateRangeValues
   */
  private static void printPivotValues(DateRangeValues dateRangeValues, ColumnHeader columnHeader) {
    List<PivotValue> pivotValues = dateRangeValues.getPivotValues();
    if (pivotValues != null) {
      // Iterate through every pivot region
      for (int pivotRegionIndex = 0; pivotRegionIndex < pivotValues.size(); pivotRegionIndex++) {
        System.out.println("\t\tPivot region #" + pivotRegionIndex);

        PivotValue pivotRegion = pivotValues.get(pivotRegionIndex);
        if (pivotRegion.getValues() != null) {
          // Iterate through every metric column within the current pivot region
          for (int pivotMetricIndex = 0; pivotMetricIndex < pivotRegion.getValues().size();
              pivotMetricIndex++) {
            // Obtain the pivot header entry object from the report header that will be used to
            // display the pivot header for the current column
            PivotHeaderEntry pivotHeaderEntry =
                columnHeader.getMetricHeader()
                    .getPivotHeaders()
                    .get(pivotRegionIndex)
                    .getPivotHeaderEntries()
                    .get(pivotMetricIndex);

            // Print the dimension section of the pivot header
            printPivotDimensions(pivotHeaderEntry);

            // Print the pivot metric name
            String pivotName = pivotHeaderEntry.getMetric().getName();
            System.out.println("\t\t\tPivot metric name = " + pivotName);

            // Print the pivot metric value
            String pivotValue = pivotRegion.getValues().get(pivotMetricIndex);
            System.out.println("\t\t\tPivot metric value = " + pivotValue);

            System.out.println();
          }
        }
        System.out.println();
      }
    }
  }

  private static void printReportInfo(Report report) {
    System.out.println("Report Infos:");
    System.out.println("Next page token = " + report.getNextPageToken());
    System.out.println();
  }

  private static void printDimensions(List<String> dimensionValues, ColumnHeader columnHeader) {
    for (int i = 0; i < dimensionValues.size(); i++) {
      // Obtain a human readable dimension name from the report's column header
      String dimensionName = columnHeader.getDimensions().get(i);
      String dimensionValue = dimensionValues.get(i);

      System.out.println("\tDimension name = " + dimensionName);
      System.out.println("\tDimension value = " + dimensionValue);

      System.out.println();
    }
  }

  /**
   * Print all the rows of data returned by the API.
   *
   * @param report the data returned from the API.
   */
  private static void printRows(Report report) {
    System.out.println("Report rows:");

    ReportData data = report.getData();
    List<ReportRow> rows = data.getRows();

    if (rows == null || rows.size() == 0) {
      System.out.println("No Rows Found");
      return;
    }

    for (int i = 0; i < rows.size(); i++) {
      ReportRow row = rows.get(i);
      System.out.println("Row " + i);

      printDimensions(row.getDimensions(), report.getColumnHeader());
      printDateRanges(row.getMetrics(), report.getColumnHeader());

      System.out.println();
    }
  }
}
