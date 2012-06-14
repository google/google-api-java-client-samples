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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;


/**
 * This is a basic hello world sample for the Google Analytics API. It is desgigned
 * to run from the command line and will prompt a user to grant access to their data.
 * Once complete, the sample will traverse the Management API hiearchy by going through the
 * authorized user's first account, first web property, and finally the first profile and
 * retrieve the first profile id. This ID is then used with the Core Reporting API to
 * retrieve the top 25 organic search terms.
 *
 * Note: This demo does not store OAuth 2.0 refresh tokens. Each time the sample is run,
 * the user must explicitly grant access to their analytics data.
 * 
 * @author api.nickm@gmail.com
 */
public class HelloAnalyticsApiSample {

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /**
   * Main demo. This first initializes an analytics service object. It then uses the
   * Google Analytics Management API to get the first profile ID for the authorized user. It then
   * uses the Core Reporting API to retrieve the top 25 organic search terms. Finally the
   * results are printed to the screen. If an API error occurs, it is printed here.
   *
   * @param args command line args.
   */
  public static void main(String[] args) {
    try {
      Analytics analytics = initializeAnalytics();
      String profileId = getFirstProfileId(analytics);
      if (profileId == null) {
        System.err.println("No profiles found.");
      } else {
        GaData gaData = executeDataQuery(analytics, profileId);
        printGaData(gaData);
      }
    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() +
          " : " + e.getDetails().getMessage());
    }
     catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   * @return An initialized Analytics service object.
   * 
   * @throws Exception if an issue occurs with OAuth2Native authorize. 
   */
  private static Analytics initializeAnalytics() throws Exception {
    // Authorization.
    Credential credential = OAuth2Native.authorize(
        HTTP_TRANSPORT, JSON_FACTORY, new LocalServerReceiver(),
        Arrays.asList(AnalyticsScopes.ANALYTICS_READONLY));

    // Set up and return Google Analytics API client.
    return new Analytics.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName("Google-Analytics-Hello-Analytics-API-Sample")
        .build();
  }

  /**
   * Returns the first profile id by traversing the Google Analytics Management API.
   * This makes 3 queries, first to the accounts collection, then to the web properties
   * collection, and finally to the profiles collection. In each request the first
   * ID of the first entity is retrieved and used in the query for the next collection
   * in the hierarchy.
   *
   * @param analytics the analytics service object used to access the API.
   * @return the profile ID of the user's first account, web property, and profile.
   * @throws IOException if the API encounters an error.
   */
  private static String getFirstProfileId(Analytics analytics) throws IOException {
    String profileId = null;

    // Query accounts collection.
    Accounts accounts = analytics.management().accounts().list().execute();

    if (accounts.getItems().isEmpty()) {
      System.err.println("No accounts found");
    } else {
      String firstAccountId = accounts.getItems().get(0).getId();

      // Query webproperties collection.
      Webproperties webproperties = analytics.management().webproperties()
          .list(firstAccountId).execute();

      if (webproperties.getItems().isEmpty()) {
        System.err.println("No Webproperties found");
      } else {
        String firstWebpropertyId = webproperties.getItems().get(0).getId();

        // Query profiles collection.
        Profiles profiles = analytics.management().profiles()
            .list(firstAccountId, firstWebpropertyId).execute();

        if (profiles.getItems().isEmpty()) {
          System.err.println("No profiles found");
        } else {
          profileId = profiles.getItems().get(0).getId();
        }
      }
    }
    return profileId;
  }

  /**
   * Returns the top 25 organic search keywords and traffic source by visits. The
   * Core Reporting API is used to retrieve this data.
   *
   * @param analytics the analytics service object used to access the API.
   * @param profileId the profile ID from which to retrieve data.
   * @return the response from the API.
   * @throws IOException tf an API error occured.
   */
  private static GaData executeDataQuery(Analytics analytics, String profileId) throws IOException {
    return analytics.data().ga()
        .get("ga:" + profileId,        // Table Id. ga: + profile id.
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
   * Prints the output from the Core Reporting API. The profile name is printed along with
   * each column name and all the data in the rows.
   *
   * @param results data returned from the Core Reporting API.
   */
  private static void printGaData(GaData results) {
    System.out.println("printing results for profile: " +
        results.getProfileInfo().getProfileName());

    if (results.getRows() == null || results.getRows().isEmpty()) {
      System.out.println("No results Found.");
    } else {

      // Print column headers.
      for (ColumnHeaders header : results.getColumnHeaders()) {
        System.out.printf("%30s", header.getName());
      }
      System.out.println();

      // Print actual data.
      for (List<String> row : results.getRows()) {
        for (String column : row) {
          System.out.printf("%30s", column);
        }
        System.out.println();
      }

      System.out.println();
    }
  }
}
