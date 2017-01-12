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
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Account;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.CustomDimensions;
import com.google.api.services.analytics.model.Webproperties;
import com.google.api.services.analytics.model.Webproperty;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;


/**
 * This is a basic hello world sample for the Google Analytics API. It is designed to run from the
 * command line and will prompt a user to grant access to their data. Once complete, the sample will
 * traverse the Management API hierarchy by going through the authorized user's first account, first
 * web property, and finally the first profile and retrieve the first profile id. This ID is then
 * used with the Core Reporting API to retrieve the top 25 organic search terms.
 *
 * @author api.nickm@gmail.com
 */
public class GoogleAnalyticsDimensionsCopyTool {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "Google Analytics Dimension Copy Tool";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/analytics_sample");
  
  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
  
  /** Global instance of the Google Analytics service object */
  private static Analytics analytics;
  /**
   * Main demo. This first initializes an analytics service object. It then uses the Google
   * Analytics Management API to get the first profile ID for the authorized user. It then uses the
   * Core Reporting API to retrieve the top 25 organic search terms. Finally the results are printed
   * to the screen. If an API error occurs, it is printed here.
   *
   * @param args command line args.
   */
  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      analytics = initializeAnalytics();
      
//      Scanner reader = new Scanner(System.in);  // Reading from System.in
//      System.out.println("Enter source GA Account Id: ");
//      String sourceGAAccountId = reader.nextLine(); // Scans the next token of the input as an int.
//      System.out.println("Enter source GA Property Id: ");
//      String sourceGAPropertyId = reader.nextLine();
//      System.out.println("Enter destination GA Account Id: ");
//      String destinationGAAccountId = reader.nextLine(); // Scans the next token of the input as an int.
//      System.out.println("Enter destination GA Property Id: ");
//      String destinationGAPropertyId = reader.nextLine();
      String sourceGAAccountId = "88634604"; //CE Magnolia Sites Account
      String sourceGAPropertyId = "UA-48148083-48"; //Invesco PT Portugal Property
      String destinationGAAccountId = "89271070"; //Google Analytics API Demo Account
      CustomDimensions sourceCustomDimensions = getCustomDimensions(sourceGAAccountId, sourceGAPropertyId);
      copyExistingPropertyIntoNewProperty(getPropertyByPropertyIdAccountId(sourceGAPropertyId, sourceGAAccountId), destinationGAAccountId);
      
      
    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
          + e.getDetails().getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, new InputStreamReader(
            GoogleAnalyticsDimensionsCopyTool.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=analytics "
          + "into analytics-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(AnalyticsScopes.ANALYTICS_EDIT)).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /**
   * Performs all necessary setup steps for running requests against the API.
   *
   * @return An initialized Analytics service object.
   *
   * @throws Exception if an issue occurs with OAuth2Native authorize.
   */
  private static Analytics initializeAnalytics() throws Exception {
    // Authorization.
    Credential credential = authorize();

    // Set up and return Google Analytics API client.
    return new Analytics.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
        APPLICATION_NAME).build();
  }
  
  /**
   * 
   * @param accountId
   * @param propertyId
   * @param analytics
   * @return
   * @throws IOException
   */
  private static CustomDimensions getCustomDimensions(String accountId, String propertyId) throws IOException {
    // Query CustomDimensions collection.
    CustomDimensions customDimensions = analytics.management().customDimensions().list(accountId, propertyId).execute();

    if (customDimensions.getItems().isEmpty()) {
      System.err.println("No Custom Dimensions found for account: " + accountId + " and property: " + propertyId);
    }
    
    return customDimensions;
  }
  
  /**
   * 
   * @param accountId
   * @return
   * @throws IOException
   */
  private static Account getAccountByAccountId(String accountId) throws IOException{
    List<Account> accountList = analytics.management().accounts().list().execute().getItems();
    Account accountGA = null;
    for (Iterator<Account> iterator = accountList.iterator(); iterator.hasNext();) {
      Account account = iterator.next();
      if(account.getId().equals(accountId)){
        accountGA = account;
      }
    }
    
    return accountGA;
  }
  
  /**
   * 
   * @param accountId
   * @return
   * @throws IOException
   */
  private static Webproperty getPropertyByPropertyIdAccountId(String propertyId, String accountId) throws IOException{
    List<Webproperty> propertyList = analytics.management().webproperties().list(accountId).execute().getItems();
    Webproperty webPropertyGA = null;
    
    for (Iterator<Webproperty> iterator = propertyList.iterator(); iterator.hasNext();) {
      Webproperty webproperty = iterator.next();
      if(webproperty.getId().equals(propertyId)){
        webPropertyGA = webproperty;
      }
    }
    
    return webPropertyGA;
  }
  
  private static void copyExistingPropertyIntoNewProperty(Webproperty sourceProperty, String destinationAccountId) throws IOException {
    // Query CustomDimensions collection.
    //CustomDimensions customDimensions = sourceProperty.get
 // Construct the body of the request.
    Webproperty body = new Webproperty();
    body.setWebsiteUrl(sourceProperty.getWebsiteUrl());
    body.setName(sourceProperty.getName());

    try {
      analytics.management().webproperties().insert(destinationAccountId, body).execute();
    } catch (GoogleJsonResponseException e) {
      System.err.println("There was a service error: "
          + e.getDetails().getCode() + " : "
          + e.getDetails().getMessage());
    }
  }
}
