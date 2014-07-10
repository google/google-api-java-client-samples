/*
 * Copyright (c) 2013 Google Inc.
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

package com.google.api.services.samples.adexchangeseller.cmdline;

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
import com.google.api.services.adexchangeseller.AdExchangeSeller;
import com.google.api.services.adexchangeseller.AdExchangeSellerScopes;
import com.google.api.services.adexchangeseller.model.AdClients;
import com.google.api.services.adexchangeseller.model.AdUnits;
import com.google.api.services.adexchangeseller.model.CustomChannels;
import com.google.api.services.adexchangeseller.model.SavedReports;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * A sample application that runs multiple requests against the Ad Exchange Seller REST API.
 * These include:
 * <ul>
 * <li>Listing all ad clients for the account</li>
 * <li>Listing all ad units for an ad client</li>
 * <li>Listing all custom channels for an ad unit</li>
 * <li>Listing all custom channels for an ad client</li>
 * <li>Listing all ad units for a custom channel</li>
 * <li>Listing all URL channels for an ad client</li>
 * <li>Running a report for an ad client, for the past 7 days</li>
 * <li>Running a paginated report for an ad client, for the past 7 days</li>
 * <li>Listing all saved reports for the account</li>
 * <li>Running a saved report for the account</li>
 * <li>Listing all dimensions for the account</li>
 * <li>Listing all metrics for the account</li>
 * <li>Listing all alerts for the account</li>
 * </ul>
 */
public class AdExchangeSellerSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/adexchangeseller_sample");

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
        new InputStreamReader(
            AdExchangeSellerSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from "
          + "https://code.google.com/apis/console/?api=adexchangeseller into "
          + "adexchangeseller-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(AdExchangeSellerScopes.ADEXCHANGE_SELLER_READONLY))
        .setDataStoreFactory(dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   *
   * @return An initialized AdExchangeSeller service object.
   * @throws Exception
   */
  private static AdExchangeSeller initializeAdExchangeSeller() throws Exception {
    // Authorization.
    Credential credential = authorize();

    // Set up Ad Exchange Seller REST API client.
    AdExchangeSeller adExchangeSeller = new AdExchangeSeller.Builder(
        new NetHttpTransport(), JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
        .build();

    return adExchangeSeller;
  }

  /**
   * Runs all the Ad Exchange Seller REST API samples.
   *
   * @param args command-line arguments.
   */
  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      AdExchangeSeller adExchangeSeller = initializeAdExchangeSeller();

      AdClients adClients = GetAllAdClients.run(adExchangeSeller, MAX_LIST_PAGE_SIZE);
      if ((adClients.getItems() != null) && !adClients.getItems().isEmpty()) {
        // Get an ad client ID, so we can run the rest of the samples.
        String exampleAdClientId = adClients.getItems().get(0).getId();

        AdUnits units = GetAllAdUnits.run(adExchangeSeller, exampleAdClientId, MAX_LIST_PAGE_SIZE);
        if ((units.getItems() != null) && !units.getItems().isEmpty()) {
          // Get an example ad unit ID, so we can run the following sample.
          String exampleAdUnitId = units.getItems().get(0).getId();
          GetAllCustomChannelsForAdUnit.run(
              adExchangeSeller, exampleAdClientId, exampleAdUnitId, MAX_LIST_PAGE_SIZE);
        }

        CustomChannels channels =
            GetAllCustomChannels.run(adExchangeSeller, exampleAdClientId, MAX_LIST_PAGE_SIZE);
        if ((channels.getItems() != null) && !channels.getItems().isEmpty()) {
          // Get an example custom channel ID, so we can run the following sample.
          String exampleCustomChannelId = channels.getItems().get(0).getId();
          GetAllAdUnitsForCustomChannel.run(
              adExchangeSeller, exampleAdClientId, exampleCustomChannelId, MAX_LIST_PAGE_SIZE);
        }

        GetAllUrlChannels.run(adExchangeSeller, exampleAdClientId, MAX_LIST_PAGE_SIZE);
        GenerateReport.run(adExchangeSeller, exampleAdClientId);
        GenerateReportWithPaging.run(adExchangeSeller, exampleAdClientId, MAX_REPORT_PAGE_SIZE);
      } else {
        System.out.println("No ad clients found, unable to run remaining methods.");
      }

      SavedReports savedReports = GetAllSavedReports.run(adExchangeSeller, MAX_REPORT_PAGE_SIZE);
      if ((savedReports.getItems() != null) && !savedReports.getItems().isEmpty()) {
        // Get a saved report ID, so we can generate its report.
        String exampleSavedReportId = savedReports.getItems().get(0).getId();
        GenerateSavedReport.run(adExchangeSeller, exampleSavedReportId);
      } else {
        System.out.println("No saved report found.");
      }

      GetAllDimensions.run(adExchangeSeller);
      GetAllMetrics.run(adExchangeSeller);

      GetAllAlerts.run(adExchangeSeller);

      GetAllPreferredDeals.run(adExchangeSeller);

    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
