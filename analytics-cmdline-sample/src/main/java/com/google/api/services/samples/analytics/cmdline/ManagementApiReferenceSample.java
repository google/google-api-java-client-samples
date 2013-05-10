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
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Goal;
import com.google.api.services.analytics.model.Goal.EventDetails;
import com.google.api.services.analytics.model.Goal.EventDetails.EventConditions;
import com.google.api.services.analytics.model.Goal.UrlDestinationDetails;
import com.google.api.services.analytics.model.Goal.UrlDestinationDetails.Steps;
import com.google.api.services.analytics.model.Goal.VisitNumPagesDetails;
import com.google.api.services.analytics.model.Goal.VisitTimeOnSiteDetails;
import com.google.api.services.analytics.model.Goals;
import com.google.api.services.analytics.model.Profile;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Segment;
import com.google.api.services.analytics.model.Segments;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.analytics.model.Webproperty;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * This sample application demonstrates how to traverse the Management API. At each level, all the
 * important information about each entity is printed to the screen.
 *
 * Note: This demo does not store OAuth 2.0 refresh tokens. Each time the sample is run,
 * the user must explicitly grant access to their Analytics data.
 * @author api.nickm@gmail.com
 */
public class ManagementApiReferenceSample {

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
   * Main demo. An Analytics service object is instantiated and then it is used to traverse
   * and print all the Management API entities. If any exceptions occur, they are caught
   * and printed.
   *
   * @param args command line args.
   */
  public static void main(String args[]) {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      Analytics analytics = initializeAnalytics();
      printManagementEntities(analytics);

    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() +
          " : " + e.getDetails().getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
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
   * @return An initialized Analytics service object.
   *
   * @throws Exception if an issue occurs with OAuth2Native authorize.
   */
  private static Analytics initializeAnalytics() throws Exception  {
    // Authorization.
    Credential credential = authorize();

    // Set up and return Google Analytics API client.
    return new Analytics.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .setHttpRequestInitializer(credential)
        .build();
  }

  /**
   * Traverses through the Management API hiearchy and prints each entity. This retrieves and
   * prints the authorized user's accounts. It then retrieves and prints all the web
   * properties for the first account, retrieves and prints all the profiles for the
   * first web property, and retrieves and prints all the goals for the first profile. Finally
   * all the user's segments are prtined.
   *
   * @param analytics an initialized Analytics service object.
   * @throws IOException if any network errors occured.
   */
  private static void printManagementEntities(Analytics analytics) throws IOException {

    // Query accounts collection.
    Accounts accounts = analytics.management().accounts().list().execute();

    if (accounts.getItems().isEmpty()) {
      System.err.println("No accounts found");
    } else {
      printAccounts(accounts);
      String firstAccountId = accounts.getItems().get(0).getId();

      // Query webproperties collection.
      Webproperties webproperties = analytics.management().webproperties()
          .list(firstAccountId).execute();

      if (webproperties.getItems().isEmpty()) {
        System.err.println("No webproperties found");
      } else {
        printWebProperties(webproperties);
        String firstWebpropertyId = webproperties.getItems().get(0).getId();

        // Query profiles collection.
        Profiles profiles = analytics.management().profiles()
            .list(firstAccountId, firstWebpropertyId).execute();

        if (profiles.getItems().isEmpty()) {
          System.err.println("No profiles found");
        } else {
          printProfiles(profiles);
          String firstProfileId = profiles.getItems().get(0).getId();

          // Query goals collection.
          Goals goals = analytics.management().goals()
              .list(firstAccountId, firstWebpropertyId, firstProfileId).execute();

          if (goals.getItems() == null || goals.getItems().isEmpty()) {
            System.err.println("No goals found");
          } else {
            printGoals(goals);
          }
        }
      }
    }
    Segments segments = analytics.management().segments().list().execute();
    printSegments(segments);
  }

  /**
   * Prints all the account information from the accounts collection.
   *
   * @param accounts the results from the accounts collection.
   */
  private static void printAccounts(Accounts accounts) {
    System.out.println("\n----- Accounts Collection -----\n");

    for (Account account : accounts.getItems()) {
      System.out.println("Account ID: " + account.getId());
      System.out.println("Account Name: " + account.getName());
      System.out.println("Account Created: " + account.getCreated());
      System.out.println("Account Updated: " + account.getUpdated());
    }
  }

  /**
   * Prints all the webproperty information from the webproperties collection.
   *
   * @param webproperties the results from the webproperties collection.
   */
  private static void printWebProperties(Webproperties webproperties) {
    System.out.println("\n----- Webproperties Collection -----\n");

    for (Webproperty webproperty : webproperties.getItems()) {
      System.out.println("Account ID: " + webproperty.getAccountId());
      System.out.println("Web Property ID: " + webproperty.getId());
      System.out.println("Web Property Name: " + webproperty.getName());
      System.out.println("Web Property Internal Id: " + webproperty.getInternalWebPropertyId());
      System.out.println("Web Property Website URL: " + webproperty.getWebsiteUrl());
      System.out.println("Web Property Created: " + webproperty.getCreated());
      System.out.println("Web Property Updated: " + webproperty.getUpdated());
    }
  }

  /**
   * Prints all the profile information from the profiles collection.
   *
   * @param profiles the results from the profiles collection.
   */
  private static void printProfiles(Profiles profiles) {
    System.out.println("\n----- Profiles Collection -----\n");

    for (Profile profile : profiles.getItems()) {
      System.out.println("Account ID: " + profile.getAccountId());
      System.out.println("Web Property ID: " + profile.getWebPropertyId());
      System.out.println("Web Property Internal ID: " + profile.getInternalWebPropertyId());
      System.out.println("Profile ID: " + profile.getId());
      System.out.println("Profile Name: " + profile.getName());

      System.out.println("Profile defaultPage: " + profile.getDefaultPage());
      System.out.println("Profile Exclude Query Parameters: "
          + profile.getExcludeQueryParameters());
      System.out.println("Profile Site Search Query Parameters: "
          + profile.getSiteSearchQueryParameters());
      System.out.println("Profile Site Search Category Parameters: "
          + profile.getSiteSearchCategoryParameters());

      System.out.println("Profile Currency: " + profile.getCurrency());
      System.out.println("Profile Timezone: " + profile.getTimezone());
      System.out.println("Profile Created: " + profile.getCreated());
      System.out.println("Profile Updated: " + profile.getUpdated());
    }
  }

  /**
   * Prints all the goal information from the goals collection. A goal can be one of 4 types.
   * Depending on the goal type, the appropriate goal data is printed.
   *
   * @param goals the results from the goals collection.
   */
  private static void printGoals(Goals goals) {
    System.out.println("\n----- Goals Collection -----\n");

    for (Goal goal : goals.getItems()) {
      System.out.println("Account ID: " + goal.getAccountId());
      System.out.println("Web Property ID: " + goal.getWebPropertyId());
      System.out.println("Web Property Internal Id: " + goal.getInternalWebPropertyId());
      System.out.println("Profile ID: " + goal.getId());

      System.out.println("Goal Number: " + goal.getId());
      System.out.println("Goal Name: " + goal.getName());
      System.out.println("Is Goal Active: " + goal.getActive());
      System.out.println("Goal Value: " + goal.getValue());
      System.out.println("Goal Type: " + goal.getType());
      System.out.println("Goal Created: " + goal.getCreated());
      System.out.println("Goal Updated: " + goal.getUpdated());

      if (goal.getType().equals("URL_DESTINATION")) {
        printUrlDestinationDetails(goal.getUrlDestinationDetails());

      } else if (goal.getType().equals("VISIT_TIME_ON_SITE")) {
        printVisitTimeOnSiteDetails(goal.getVisitTimeOnSiteDetails());

      } else if (goal.getType().equals("VISIT_NUM_PAGES")) {
        printVisitNumPagesDetails(goal.getVisitNumPagesDetails());

      } else if (goal.getType().equals("EVENT")) {
        printGoalEventDetails(goal.getEventDetails());
      }
    }
  }

  /**
   * Prints details for URL_DESTINATION type goals. Each of these goals might have one or more
   * goal steps configured. If any are present, they are printed.
   *
   * @param destinationDetails the details of a DESTINATION type goal.
   */
  private static void printUrlDestinationDetails(UrlDestinationDetails destinationDetails) {
    System.out.println("Goal Url: " + destinationDetails.getUrl());
    System.out.println("Case Sensitive: " +  destinationDetails.getCaseSensitive());
    System.out.println("Match Type: " +  destinationDetails.getMatchType());
    System.out.println("First Step Required: " +  destinationDetails.getFirstStepRequired());

    if (destinationDetails.getSteps() != null) {
      System.out.println("Goal Steps: ");
      for (Steps step : destinationDetails.getSteps()) {
        System.out.println("Step Number: " + step.getNumber());
        System.out.println("Name: " + step.getName());
        System.out.println("URL: " + step.getUrl());
      }
    } else {
      System.out.println("No Steps Configured");
    }
  }

  /**
   * Prints details for VISIT_TIME_ON_SITE type goals.
   *
   * @param visitTimeOnSiteDetails the details of a VISIT_TIME_ON_SITE goal.
   */
  private static void printVisitTimeOnSiteDetails(
      VisitTimeOnSiteDetails visitTimeOnSiteDetails) {

    System.out.println("Goal Type:  VISIT_TIME_ON_SITE");
    System.out.println("VISIT_TIME_ON_SITE - Comparison Type: "
        + visitTimeOnSiteDetails.getComparisonType());
    System.out.println("VISIT_TIME_ON_SITE - Comparison Value: "
        + visitTimeOnSiteDetails.getComparisonValue());
  }

  /**
   * Prints details for VISIT_NUM_PAGES type goals.
   *
   * @param visitNumPagesDetails the details of a VISIT_NUM_PAGES goal.
   */
  private static void printVisitNumPagesDetails(VisitNumPagesDetails visitNumPagesDetails) {
    System.out.println("Goal Type:  VISIT_NUM_PAGES");
    System.out.println("VISIT_NUM_PAGES - Comparison Type: "
        +  visitNumPagesDetails.getComparisonType());
    System.out.println("VISIT_NUM_PAGES - Comparison Value: "
        +  visitNumPagesDetails.getComparisonValue());
  }

  /**
   * Prints details for EVENT type goals.
   *
   * @param eventDetails the details of an EVENT type goal.
   */
  private static void printGoalEventDetails(EventDetails eventDetails) {
    System.out.println("Goal Type:  EVENT");
    System.out.println("EVENT - Use Event Value: " +  eventDetails.getUseEventValue());

    if (eventDetails.getEventConditions() != null) {
      System.out.println("Goal Conditions: ");
      for (EventConditions conditions : eventDetails.getEventConditions()) {
        System.out.println("Type: " + conditions.getType());

        if (conditions.getType().equals("VALUE")) {
          System.out.println("Comparison Type: " + conditions.getComparisonType());
          System.out.println("Comparison Value: " + conditions.getComparisonValue());
        } else {
          System.out.println("matchType: " + conditions.getMatchType());
          System.out.println("expression: " + conditions.getExpression());
        }
      }
    }
  }

  /**
   * Prints all the segment infromation from the segements collection.
   *
   * @param segments the results from the segments collection.
   */
  private static void printSegments(Segments segments) {
    System.out.println("\n----- Segments Collection -----\n");

    for (Segment segment : segments.getItems()) {
      System.out.println("Advanced Segment ID: " + segment.getId());
      System.out.println("Advanced Segment Name: " + segment.getName());
      System.out.println("Advanced Segment Definition: " + segment.getDefinition());

      // These fields are only set for custom segments and not default segments.
      if (segment.getCreated() != null) {
        System.out.println("Advanced Segment Created: " + segment.getCreated());
        System.out.println("Advanced Segment Updated: " + segment.getUpdated());
      }
    }
  }
}
