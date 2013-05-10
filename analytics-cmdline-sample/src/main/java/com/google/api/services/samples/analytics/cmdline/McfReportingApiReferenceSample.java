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
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.McfData;
import com.google.api.services.analytics.model.McfData.ColumnHeaders;
import com.google.api.services.analytics.model.McfData.ProfileInfo;
import com.google.api.services.analytics.model.McfData.Query;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This application demonstrates how to use the Google Analytics Java client library to access all
 * the pieces of data returned by the Google Analytics Multi-Channel Funnels API v3.
 * 
 *  To run this, you must supply your profile ID. Read the Core Reporting API developer guide to
 * learn how to get this value.
 * 
 *  Note: This demo does not store OAuth 2.0 refresh Tokens. Each time the sample is run, the user
 * must explicitly grant access to their Analytics data.
 * 
 * @author nafi@google.com
 * @author api.nickm@gmail.com
 */
public class McfReportingApiReferenceSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";
  
  /** Global instance of the HTTP transport. */
  private static HttpTransport HTTP_TRANSPORT;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /**
   * TABLE ID of the format 'ga:xxx' where 'xxx' is your profile ID.
   */
  private static final String TABLE_ID = "_your_table_id_";

  private static final String MCF_SEQUENCE_TYPE = "MCF_SEQUENCE";
  private static final String INTEGER_TYPE = "INTEGER";

  /**
   * Main demo. This first initializes an analytics service object. It then uses the MCF API to
   * retrieve the top 25 source paths with most total conversions. It will also retrieve the top 25
   * organic sources with most total conversions. Finally the results are printed to the screen. If
   * an API error occurs, it is printed here.
   * 
   * @param args command line args.
   */
  public static void main(String[] args) {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      Analytics analytics = initializeAnalytics();

      McfData mcfPathData = executePathQuery(analytics, TABLE_ID);
      printAllInfo(mcfPathData);

      McfData mcfInteractionData = executeInteractionQuery(analytics, TABLE_ID);
      printAllInfo(mcfInteractionData);

    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
          + e.getDetails().getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * Print all data returned from the API.
   * 
   * @param mcfData the data returned from the API.
   */
  public static void printAllInfo(McfData mcfData) {
    printReportInfo(mcfData);
    printProfileInfo(mcfData);
    printQueryInfo(mcfData);
    printPaginationInfo(mcfData);
    printTotalsForAllResults(mcfData);
    printColumnHeaders(mcfData);
    printDataTable(mcfData);
    System.out.println();
    System.out.println();
  }

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, new InputStreamReader(
            HelloAnalyticsApiSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics "
          + "into analytics-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"), ".credentials/analytics.json"), JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
        Collections.singleton(AnalyticsScopes.ANALYTICS_READONLY)).setCredentialStore(
        credentialStore).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
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
        .setApplicationName(APPLICATION_NAME).build();
  }

  /**
   * Returns the top 25 source paths with most total conversions. The MCF API is used to retrieve
   * this data.
   * 
   * @param analytics The analytics service object used to access the API.
   * @param tableId The table ID from which to retrieve data.
   * @return The response from the API.
   * @throws IOException If an API error occurred.
   */
  private static McfData executePathQuery(Analytics analytics, String tableId) throws IOException {
    return analytics.data().mcf().get(tableId, "2012-01-01", // Start date.
        "2012-03-31", // End date.
        "mcf:totalConversions") // Metrics.
        .setDimensions("mcf:sourcePath")
        .setSort("-mcf:totalConversions")
        .setMaxResults(25)
        .execute();
  }

  /**
   * Returns the top 25 organic sources with most total conversions. The MCF API is used to retrieve
   * this data.
   * 
   * @param analytics The analytics service object used to access the API.
   * @param tableId The table ID from which to retrieve data.
   * @return The response from the API.
   * @throws IOException If an API error occurred.
   */
  private static McfData executeInteractionQuery(Analytics analytics, String tableId)
      throws IOException {
    return analytics.data().mcf().get(tableId, "2012-01-01", // Start date.
        "2012-03-31", // End date.
        "mcf:totalConversions") // Metrics.
        .setDimensions("mcf:source")
        .setSort("-mcf:totalConversions")
        .setFilters("mcf:medium==organic")
        .setMaxResults(25)
        .execute();
  }

  /**
   * Prints general information about this report.
   * 
   * @param mcfData the data returned from the API.
   */
  private static void printReportInfo(McfData mcfData) {
    System.out.println();
    System.out.println("Report Info:");
    System.out.println("ID:" + mcfData.getId());
    System.out.println("Self link: " + mcfData.getSelfLink());
    System.out.println("Kind: " + mcfData.getKind());
    System.out.println("Contains Sampled Data: " + mcfData.getContainsSampledData());
    System.out.println();
  }

  /**
   * Prints general information about the profile from which this report was accessed.
   * 
   * @param mcfData the data returned from the API.
   */
  private static void printProfileInfo(McfData mcfData) {
    ProfileInfo profileInfo = mcfData.getProfileInfo();

    System.out.println("Profile Info:");
    System.out.println("Account ID: " + profileInfo.getAccountId());
    System.out.println("Web Property ID: " + profileInfo.getWebPropertyId());
    System.out.println("Internal Web Property ID: " + profileInfo.getInternalWebPropertyId());
    System.out.println("Profile ID: " + profileInfo.getProfileId());
    System.out.println("Profile Name: " + profileInfo.getProfileName());
    System.out.println("Table ID: " + profileInfo.getTableId());
    System.out.println();
  }

  /**
   * Prints the values of all the parameters that were used to query the API.
   * 
   * @param mcfData the data returned from the API.
   */
  private static void printQueryInfo(McfData mcfData) {
    Query query = mcfData.getQuery();

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
    System.out.println();
  }

  /**
   * Prints common pagination information.
   * 
   * @param mcfData the data returned from the API.
   */
  private static void printPaginationInfo(McfData mcfData) {
    System.out.println("Pagination Info:");
    System.out.println("Previous Link: " + mcfData.getPreviousLink());
    System.out.println("Next Link: " + mcfData.getNextLink());
    System.out.println("Items Per Page: " + mcfData.getItemsPerPage());
    System.out.println("Total Results: " + mcfData.getTotalResults());
    System.out.println();
  }

  /**
   * Prints the total metric value for all rows the query matched.
   * 
   * @param mcfData the data returned from the API.
   */
  private static void printTotalsForAllResults(McfData mcfData) {
    System.out.println("Metric totals over all results:");
    Map<String, String> totalsMap = mcfData.getTotalsForAllResults();
    for (Map.Entry<String, String> entry : totalsMap.entrySet()) {
      System.out.println(entry.getKey() + " : " + entry.getValue());
    }
    System.out.println();
  }

  /**
   * Prints the information for each column. The reporting data from the API is returned as rows of
   * data. The column headers describe the names and types of each column in rows.
   * 
   * @param mcfData the data returned from the API.
   */
  private static void printColumnHeaders(McfData mcfData) {
    System.out.println("Column Headers:");

    for (ColumnHeaders header : mcfData.getColumnHeaders()) {
      System.out.println("Column Name: " + header.getName());
      System.out.println("Column Type: " + header.getColumnType());
      System.out.println("Column Data Type: " + header.getDataType());
    }
    System.out.println();
  }

  /**
   * Prints all the rows of data returned by the API.
   * 
   * @param mcfData the data returned from the API.
   */
  private static void printDataTable(McfData mcfData) {
    System.out.println("Data Table:");
    if (mcfData.getTotalResults() > 0) {
      // Print the column names.
      List<ColumnHeaders> headers = mcfData.getColumnHeaders();
      for (ColumnHeaders header : headers) {
        if (header.getDataType().equals(MCF_SEQUENCE_TYPE)) {
          System.out.printf("%-50s", header.getName());
        } else {
          System.out.printf("%25s", header.getName());
        }
      }
      System.out.println();

      // Print the rows of data.
      for (List<McfData.Rows> row : mcfData.getRows()) {
        for (int columnIndex = 0; columnIndex < row.size(); ++columnIndex) {
          ColumnHeaders header = headers.get(columnIndex);
          McfData.Rows cell = row.get(columnIndex);
          if (header.getDataType().equals(MCF_SEQUENCE_TYPE)) {
            System.out.printf(
                "%-50s", getStringFromMcfSequence(cell.getConversionPathValue(), " > "));
          } else if (header.getDataType().equals(INTEGER_TYPE)) {
            System.out.printf("%25d", Long.parseLong(cell.getPrimitiveValue()));
          } else {
            System.out.printf("%25s", cell.getPrimitiveValue());
          }
        }
        System.out.println();
      }
    } else {
      System.out.println("No rows found");
    }
    System.out.println();
  }

  /**
   * Builds and gets a string to represent the path data contained in a list of
   * McfData.Rows.ConversionPathValue objects.
   * 
   * @param path List of MCF path elements.
   * @param delimiter The string that will be used while joining all path elements.
   */
  private static String getStringFromMcfSequence(
      List<McfData.Rows.ConversionPathValue> path, String delimiter) {
    StringBuilder stringBuilder = new StringBuilder();
    for (McfData.Rows.ConversionPathValue pathElement : path) {
      if (stringBuilder.length() > 0) stringBuilder.append(delimiter);
      stringBuilder.append(pathElement.getNodeValue());
    }
    return stringBuilder.toString();
  }
}
