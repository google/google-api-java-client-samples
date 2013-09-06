/*
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

package com.google.api.services.samples.adsensehost.cmdline;

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
import com.google.api.services.adsensehost.AdSenseHost;
import com.google.api.services.adsensehost.AdSenseHostScopes;
import com.google.api.services.adsensehost.model.AdClients;
import com.google.api.services.adsensehost.model.AdUnit;
import com.google.api.services.adsensehost.model.CustomChannel;
import com.google.api.services.adsensehost.model.UrlChannel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * A sample application that runs multiple requests against the AdSense Host API. These include:
 * <ul>
 * <li>Getting a list of all host ad clients</li>
 * <li>Getting a list of all host custom channels</li>
 * <li>Adding a new host custom channel</li>
 * <li>Updating an existing host custom channel</li>
 * <li>Deleting a host custom channel</li>
 * <li>Getting a list of all host URL channels</li>
 * <li>Adding a new host URL channel</li>
 * <li>Deleting an existing host URL channel</li>
 * <li>Running a report for a host ad client, for the past 7 days</li>
 * </ul>
 *
 * If you give PUB_ACCOUNT_ID a real account ID, the following requests will also run:
 * <ul>
 * <li>Getting a list of all publisher ad clients</li>
 * <li>Getting a list of all publisher ad units</li>
 * <li>Adding a new ad unit</li>
 * <li>Updating an existing ad unit</li>
 * <li>Deleting an ad unit</li>
 * <li>Running a report for a publisher ad client, for the past 7 days</li>
 * </ul>
 *
 * Other samples are included for illustration purposes, but won't be run:
 * <ul>
 * <li>Getting the account data for an existing publisher, given their ad client ID</li>
 * <li>Starting an association session</li>
 * <li>Verifying an association session</li>
 * </ul>
 */
public class AdSenseHostSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/adsense_host_sample");

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory DATA_STORE_FACTORY;

  // Global instance of the HTTP transport.
  private static HttpTransport httpTransport;

  // Global instance of the JSON factory.
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  // Maximum page size for list calls.
  private static final long MAX_LIST_PAGE_SIZE = 50;
  // Change this constant to an example publisher account ID if you want the
  // publisher samples to run.
  private static final String PUB_ACCOUNT_ID = "INSERT_CLIENT_PUB_ID_HERE";

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(AdSenseHostSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from "
          + "https://code.google.com/apis/console/?api=adsensehost into "
          + "adsensehost-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(AdSenseHostScopes.ADSENSEHOST)).setDataStoreFactory(
        DATA_STORE_FACTORY).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   *
   * @return An initialized AdSenseHost service object.
   * @throws Exception
   */
  private static AdSenseHost initializeAdsensehost() throws Exception {
    // Authorization.
    Credential credential = authorize();

    // Set up AdSense Host API client.
    AdSenseHost adsensehost = new AdSenseHost.Builder(
        new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
        APPLICATION_NAME).build();

    return adsensehost;
  }

  /**
   * Returns a unique value based on the system time.
   */
  public static String getUniqueName() {
    return String.valueOf(System.currentTimeMillis());
  }

  /**
   * Runs all the AdSense Host API samples.
   *
   * @param args command-line arguments.
   */
  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
      AdSenseHost service = initializeAdsensehost();

      AdClients adClients = GetAllAdClientsForHost.run(service, MAX_LIST_PAGE_SIZE);
      if ((adClients.getItems() != null) && !adClients.getItems().isEmpty()) {
        // Get a host ad client ID, so we can run the rest of the samples.
        String exampleHostAdClientId = adClients.getItems().get(0).getId();

        GetAllCustomChannelsForHost.run(service, exampleHostAdClientId, MAX_LIST_PAGE_SIZE);

        CustomChannel customChannel = AddCustomChannelToHost.run(service, exampleHostAdClientId);

        customChannel =
            UpdateCustomChannelOnHost.run(service, exampleHostAdClientId, customChannel.getId());

        DeleteCustomChannelOnHost.run(service, exampleHostAdClientId, customChannel.getId());

        GetAllUrlChannelsForHost.run(service, exampleHostAdClientId, MAX_LIST_PAGE_SIZE);

        UrlChannel urlChannel = AddUrlChannelToHost.run(service, exampleHostAdClientId);

        DeleteUrlChannelOnHost.run(service, exampleHostAdClientId, urlChannel.getId());

        GenerateReportForHost.run(service, exampleHostAdClientId);
      } else {
        System.out.println("No host ad clients found, unable to run remaining host samples.");
      }

      if (!PUB_ACCOUNT_ID.equals("INSERT_CLIENT_PUB_ID_HERE")) {
        AdClients pubAdClients =
            GetAllAdClientsForPublisher.run(service, PUB_ACCOUNT_ID, MAX_LIST_PAGE_SIZE);
        if ((pubAdClients.getItems() != null) && !pubAdClients.getItems().isEmpty()) {
          // Get a publisher ad client ID, so we can run the rest of the samples.
          String examplePubAdClientId = pubAdClients.getItems().get(0).getId();

          GetAllAdUnitsForPublisher.run(
              service, PUB_ACCOUNT_ID, examplePubAdClientId, MAX_LIST_PAGE_SIZE);

          AdUnit adUnit = AddAdUnitToPublisher.run(service, PUB_ACCOUNT_ID, examplePubAdClientId);

          UpdateAdUnitOnPublisher.run(
              service, PUB_ACCOUNT_ID, examplePubAdClientId, adUnit.getId());

          DeleteAdUnitOnPublisher.run(
              service, PUB_ACCOUNT_ID, examplePubAdClientId, adUnit.getId());

          GenerateReportForPublisher.run(service, PUB_ACCOUNT_ID, examplePubAdClientId);
        } else {
          System.out.println(
              "No publisher ad clients found, unable to run remaining publisher samples.");
        }
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
