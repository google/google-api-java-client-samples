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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;
import com.google.api.services.analytics.model.GaData.ProfileInfo;
import com.google.api.services.analytics.model.GaData.Query;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;

/**
 * This application demonstrates how to use the Google Analytics Java client library to access
 * all the pieces of data returned by the Google Analytics Core Reporting API v3.
 *
 * To run this, you must supply your Google Analytics TABLE ID. Read the Core Reporting API
 * developer guide to learn how to get this value.
 *
 * Note: This demo does not store OAuth 2.0 refresh Tokens. Each time the sample is run,
 * the user must explicitly grant access to their Analytics data.
 *
 * @author api.nickm@gmail.com
 */
public class CoreReportingApiReferenceSample {

  /**
   * Used to identify from which reporting profile to retrieve data.
   * Format is ga:xxx where xxx is your profile ID.
   */
  private static final String TABLE_ID = "INSERT_YOUR_TABLE_ID";

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /**
   * Main demo. This first initializes an Analytics service object. It then queries for the top
   * 25 organic search keywords and traffic sources by visits. Finally each important part of
   * the response is printed to the screen.
   *
   * @param args command line args.
   */
  public static void main(String[] args) {
    try {

      Analytics analytics = initializeAnalytics();
      GaData gaData = executeDataQuery(analytics, TABLE_ID);

      printReportInfo(gaData);
      printProfileInfo(gaData);
      printQueryInfo(gaData);
      printPaginationInfo(gaData);
      printTotalsForAllResults(gaData);
      printColumnHeaders(gaData);
      printDataTable(gaData);

    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() +
            " : " + e.getDetails().getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   * @return an initialized Analytics service object.
   *
   * @throws Exception if an issue occurs with OAuth2Native authorize.
   */
  private static Analytics initializeAnalytics() throws Exception  {
    // Authorization.
    Credential credential = OAuth2Native.authorize(
        HTTP_TRANSPORT, JSON_FACTORY, new LocalServerReceiver(),
        Arrays.asList(AnalyticsScopes.ANALYTICS_READONLY));

    // Set up and return Google Analytics API client.
    return new Analytics.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName("Google-Analytics-Core-Reporting-API-Sample")
        .build();
  }

  /**
   * Returns the top 25 organic search keywords and traffic sources by visits. The
   * Core Reporting API is used to retrieve this data.
   *
   * @param analytics the Analytics service object used to access the API.
   * @param tableId the table ID from which to retrieve data.
   * @return the response from the API.
   * @throws IOException if an API error occured.
   */
  private static GaData executeDataQuery(Analytics analytics, String tableId) throws IOException {
    return analytics.data().ga()
        .get(tableId,                  // Table Id.
            "2012-01-01",              // Start date.
            "2012-01-14",              // End date.
            "ga:visits")               // Metrics.
        .setDimensions("ga:source,ga:keyword")
        .setSort("-ga:visits,ga:source")
        .setFilters("ga:medium==organic")
        .setMaxResults(25)
        .execute();
  }

  /**
   * Prints general information about this report.
   *
   * @param gaData the data returned from the API.
   */
  private static void printReportInfo(GaData gaData) {
    System.out.println();
    System.out.println("Response:");
    System.out.println("ID:" + gaData.getId());
    System.out.println("Self link: " + gaData.getSelfLink());
    System.out.println("Kind: " + gaData.getKind());
    System.out.println("Contains Sampled Data: " + gaData.getContainsSampledData());
  }

  /**
   * Prints general information about the profile from which this report was accessed.
   *
   * @param gaData the data returned from the API.
   */
  private static void printProfileInfo(GaData gaData) {
    ProfileInfo profileInfo = gaData.getProfileInfo();

    System.out.println("Profile Info");
    System.out.println("Account ID: " + profileInfo.getAccountId());
    System.out.println("Web Property ID: " + profileInfo.getWebPropertyId());
    System.out.println("Internal Web Property ID: " + profileInfo.getInternalWebPropertyId());
    System.out.println("Profile ID: " + profileInfo.getProfileId());
    System.out.println("Profile Name: " + profileInfo.getProfileName());
    System.out.println("Table ID: " + profileInfo.getTableId());
   }

  /**
   * Prints the values of all the parameters that were used to query the API.
   *
   * @param gaData the data returned from the API.
   */
  private static void printQueryInfo(GaData gaData) {
    Query query = gaData.getQuery();

    System.out.println("Query Info:");
    System.out.println("Ids: " + query.getIds());
    System.out.println("Start Date: " + query.getStartDate());
    System.out.println("End Date: " + query.getEndDate());
    System.out.println("Metrics: " + query.getMetrics()); // List
    System.out.println("Dimensions: " + query.getDimensions()); // List
    System.out.println("Sort: " + query.getSort()); // List
    System.out.println("Segment: " + query.getSegment());
    System.out.println("Filters: " + query.getFilters());
    System.out.println("Start Index: " + query.getStartIndex());
    System.out.println("Max Results: " + query.getMaxResults());
  }

  /**
   * Prints common pagination information.
   *
   * @param gaData the data returned from the API.
   */
  private static void printPaginationInfo(GaData gaData) {
    System.out.println("Pagination Info:");
    System.out.println("Previous Link: " + gaData.getPreviousLink());
    System.out.println("Next Link: " + gaData.getNextLink());
    System.out.println("Items Per Page: " + gaData.getItemsPerPage());
    System.out.println("Total Results: " + gaData.getTotalResults());
  }

  /**
   * Prints the total metric value for all rows the query matched.
   *
   * @param gaData the data returned from the API.
   */
  private static void printTotalsForAllResults(GaData gaData) {
    System.out.println("Metric totals over all results:");
    Map<String, String> totalsMap = gaData.getTotalsForAllResults();
    for (Map.Entry<String, String> entry : totalsMap.entrySet()) {
      System.out.println(entry.getKey() + " : " + entry.getValue());
    }
  }

  /**
   * Prints the information for each column.
   * The reporting data from the API is returned as rows of data. The column
   * headers describe the names and types of each column in rows.
   *
   * @param gaData the data returned from the API.
   */
  private static void printColumnHeaders(GaData gaData) {
    System.out.println("Column Headers:");

    for (ColumnHeaders header : gaData.getColumnHeaders()) {
      System.out.println("Column Name: " + header.getName());
      System.out.println("Column Type: " + header.getColumnType());
      System.out.println("Column Data Type: " + header.getDataType());
    }
  }

  /**
   * Prints all the rows of data returned by the API.
   *
   * @param gaData the data returned from the API.
   */
  private static void printDataTable(GaData gaData) {
    if (gaData.getTotalResults() > 0) {
      System.out.println("Data Table:");

      // Print the column names.
      for (ColumnHeaders header : gaData.getColumnHeaders()) {
        System.out.format("%-32s", header.getName());
      }
      System.out.println();

      // Print the rows of data.
      for (List<String> rowValues : gaData.getRows()) {
        for (String value : rowValues) {
          System.out.format("%-32s", value);
        }
        System.out.println();
      }
    } else {
      System.out.println("No data");
    }
  }
}
