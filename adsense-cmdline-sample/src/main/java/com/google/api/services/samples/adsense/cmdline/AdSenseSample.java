/*
 * Copyright (c) 2011 Google Inc.
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

package com.google.api.services.samples.adsense.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSenseScopes;
import com.google.api.services.adsense.model.Accounts;
import com.google.api.services.adsense.model.AdClients;
import com.google.api.services.adsense.model.AdUnits;
import com.google.api.services.adsense.model.CustomChannels;
import com.google.api.services.adsense.model.SavedReports;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * A sample application that runs multiple requests against the AdSense Management API. These
 * include:
 * <ul>
 * <li>Listing all AdSense accounts for a user</li>
 * <li>Listing the sub-account tree for an account</li>
 * <li>Listing all ad clients for an account</li>
 * <li>Listing all ad clients for the default account</li>
 * <li>Listing all ad units for an ad client</li>
 * <li>Listing all custom channels for an ad unit</li>
 * <li>Listing all custom channels for an ad client</li>
 * <li>Listing all ad units for a custom channel</li>
 * <li>Listing all URL channels for an ad client</li>
 * <li>Running a report for an ad client, for the past 7 days</li>
 * <li>Running a paginated report for an ad client, for the past 7 days</li>
 * <li>Listing all saved reports for the default account</li>
 * <li>Running a saved report for the default account</li>
 * <li>Listing all saved ad styles for the default account</li>
 * <li>Listing all dimensions for the default account</li>
 * <li>Listing all metrics for the default account</li>
 * <li>Listing all alerts for the default account</li>
 * </ul>
 */
public class AdSenseSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/adsense_management_sample");

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;
  
  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  // Request parameters.
  private static final int MAX_LIST_PAGE_SIZE = 50;
  private static final int MAX_REPORT_PAGE_SIZE = 50;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(AdSenseSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from "
          + "https://code.google.com/apis/console/?api=adsense into "
          + "adsense-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(AdSenseScopes.ADSENSE_READONLY)).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   *
   * @return An initialized AdSense service object.
   * @throws Exception
   */
  private static AdSense initializeAdsense() throws Exception {
    // Authorization.
    Credential credential = authorize();

    // Set up AdSense Management API client.
    AdSense adsense = new AdSense.Builder(
        new NetHttpTransport(), JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
        .build();

    return adsense;
  }

  /**
   * Runs all the AdSense Management API samples.
   *
   * @param args command-line arguments.
   */
  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      AdSense adsense = initializeAdsense();

      Accounts accounts = GetAllAccounts.run(adsense, MAX_LIST_PAGE_SIZE);
      if ((accounts.getItems() != null) && !accounts.getItems().isEmpty()) {
        // Get an example account ID, so we can run the following sample.
        String exampleAccountId = accounts.getItems().get(0).getId();
        GetAccountTree.run(adsense, exampleAccountId);
        GetAllAdClientsForAccount.run(adsense, exampleAccountId, MAX_LIST_PAGE_SIZE);
      }

      AdClients adClients = GetAllAdClients.run(adsense, MAX_LIST_PAGE_SIZE);
      if ((adClients.getItems() != null) && !adClients.getItems().isEmpty()) {
        // Get an ad client ID, so we can run the rest of the samples.
        String exampleAdClientId = adClients.getItems().get(0).getId();

        AdUnits units = GetAllAdUnits.run(adsense, exampleAdClientId, MAX_LIST_PAGE_SIZE);
        if ((units.getItems() != null) && !units.getItems().isEmpty()) {
          // Get an example ad unit ID, so we can run the following sample.
          String exampleAdUnitId = units.getItems().get(0).getId();
          GetAllCustomChannelsForAdUnit.run(
              adsense, exampleAdClientId, exampleAdUnitId, MAX_LIST_PAGE_SIZE);
        }

        CustomChannels channels =
            GetAllCustomChannels.run(adsense, exampleAdClientId, MAX_LIST_PAGE_SIZE);
        if ((channels.getItems() != null) && !channels.getItems().isEmpty()) {
          // Get an example custom channel ID, so we can run the following sample.
          String exampleCustomChannelId = channels.getItems().get(0).getId();
          GetAllAdUnitsForCustomChannel.run(
              adsense, exampleAdClientId, exampleCustomChannelId, MAX_LIST_PAGE_SIZE);
        }

        GetAllUrlChannels.run(adsense, exampleAdClientId, MAX_LIST_PAGE_SIZE);
        GenerateReport.run(adsense, exampleAdClientId);
        GenerateReportWithPaging.run(adsense, exampleAdClientId, MAX_REPORT_PAGE_SIZE);
      } else {
        System.out.println("No ad clients found, unable to run remaining methods.");
      }

      SavedReports savedReports = GetAllSavedReports.run(adsense, MAX_REPORT_PAGE_SIZE);
      if ((savedReports.getItems() != null) && !savedReports.getItems().isEmpty()) {
        // Get a saved report ID, so we can generate its report.
        String exampleSavedReportId = savedReports.getItems().get(0).getId();
        GenerateSavedReport.run(adsense, exampleSavedReportId);
      } else {
        System.out.println("No saved report found.");
      }

      GetAllSavedAdStyles.run(adsense, MAX_LIST_PAGE_SIZE);

      GetAllDimensions.run(adsense);
      GetAllMetrics.run(adsense);

      GetAllAlerts.run(adsense);

    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
