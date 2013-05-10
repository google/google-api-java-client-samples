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

package com.google.api.services.samples.adexchangebuyer.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.adexchangebuyer.Adexchangebuyer;
import com.google.api.services.adexchangebuyer.AdexchangebuyerScopes;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A sample application that runs multiple requests against the Ad Exchange Buyer API. These
 * include:
 * <ul>
 * <li>Get All Accounts</li>
 * <li>Update Account</li>
 * <li>Get Creative</li>
 * <li>Submit Creative</li>
 * <li>Get All Direct Deals</li>
 * </ul>
 */
public class AdExchangeBuyerSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";
  
  /** Global instance of the HTTP transport. */
  private static HttpTransport HTTP_TRANSPORT;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  private static ArrayList<BaseSample> samples;

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, new InputStreamReader(
            AdExchangeBuyerSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from "
          + "https://code.google.com/apis/console/?api=adexchangebuyer into "
          + "adexchangebuyer-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"), ".credentials/adexchangebuyer.json"),
        JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
        Collections.singleton(AdexchangebuyerScopes.ADEXCHANGE_BUYER)).setCredentialStore(
        credentialStore).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   * 
   * @return An initialized AdSense service object.
   * @throws Exception
   */
  private static Adexchangebuyer initClient() throws Exception {
    // Authorization.
    Credential credential = authorize();

    // Set up API client.
    Adexchangebuyer client = new Adexchangebuyer.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME).build();

    return client;
  }

  /**
   * Initializes the list of available code samples.
   */
  private static void initSamples() {
    samples = new ArrayList<BaseSample>();
    samples.add(new GetAllAccounts());
    samples.add(new UpdateAccount());
    samples.add(new GetCreative());
    samples.add(new SubmitCreative());
    samples.add(new GetAllDirectDeals());
  }

  /**
   * Runs all the Ad Exchange Buyer API samples.
   * 
   * @param args command-line arguments.
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    initSamples();
    Adexchangebuyer client = initClient();
    BaseSample sample = null;

    while ((sample = selectSample()) != null) {
      try {
        System.out.printf("\nExecuting sample: %s\n\n", sample.getName());
        sample.execute(client);
        System.out.println();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Prints the list of available code samples and prompts the user to select one.
   * 
   * @return The selected sample or null if the user selected to exit
   * @throws IOException
   */
  private static BaseSample selectSample() throws IOException {
    System.out.printf("Samples:\n");
    int counter = 1;
    for (BaseSample sample : samples) {
      System.out.printf("%d) %s - %s\n", counter++, sample.getName(), sample.getDescription());
    }
    System.out.printf("%d) Exit the program\n", counter++);
    Integer sampleNumber = null;
    while (sampleNumber == null) {
      try {
        System.out.println("Select a sample number and press enter:");
        sampleNumber = Integer.parseInt(Utils.readInputLine());
        if (sampleNumber < 1 || sampleNumber > samples.size()) {
          if (sampleNumber == samples.size() + 1) {
            return null;
          }
          System.out.printf("Invalid number provided, try again\n");
          sampleNumber = null;
        }
      } catch (NumberFormatException e) {
        System.out.printf("Invalid number provided, try again\n");
      }
    }
    return samples.get(sampleNumber - 1);
  }
}
