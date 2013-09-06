/*
 * Copyright (c) 2010 Google Inc.
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

package com.google.api.services.samples.verification.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.siteVerification.SiteVerification;
import com.google.api.services.siteVerification.SiteVerificationRequest;
import com.google.api.services.siteVerification.SiteVerificationRequestInitializer;
import com.google.api.services.siteVerification.SiteVerificationScopes;
import com.google.api.services.siteVerification.model.SiteVerificationWebResourceGettokenRequest;
import com.google.api.services.siteVerification.model.SiteVerificationWebResourceGettokenRequest.Site;
import com.google.api.services.siteVerification.model.SiteVerificationWebResourceGettokenResponse;
import com.google.api.services.siteVerification.model.SiteVerificationWebResourceListResponse;
import com.google.api.services.siteVerification.model.SiteVerificationWebResourceResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

/**
 * @author Kevin Marshall
 */
public class VerificationSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  private static final String META_VERIFICATION_METHOD = "meta";
  private static final String SITE_TYPE = "SITE";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/verification_sample");
  
  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, new InputStreamReader(
            VerificationSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from "
          + "https://code.google.com/apis/console/?api=siteVerification into "
          + "siteVerification-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(SiteVerificationScopes.SITEVERIFICATION)).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  private static void run() throws Exception {
    httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    // authorization
    System.out.println(
        "Getting an OAuth access token. " + "Please follow the prompts on the browser window.");
    Credential credential = authorize();

    System.out.println("This is an sample Java-based client for the Google Site "
        + "Verification API.\n" + "Your data may be modified as a result of running "
        + "this demonstration.\n" + "We recommend that you run this sample with a test account\n"
        + "to avoid any accidental losses of data. Use at your own risk." + "\n\n");

    System.out.println("Enter the URL of a site to be verified:");
    String siteUrl = in.readLine();

    // set up SiteVerification
    SiteVerification siteVerification = new SiteVerification.Builder(
        httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
        .setGoogleClientRequestInitializer(new SiteVerificationRequestInitializer() {
            @Override
          public void initializeSiteVerificationRequest(SiteVerificationRequest<?> request) {
            request.setPrettyPrint(true);
          }
        }).build();

    String token = getToken(siteUrl, siteVerification);
    System.out.println("Place this META tag on your site:\n\t" + token
        + "\nWhen you are finished, press ENTER to proceed with " + "verification.");
    in.readLine();
    SiteVerificationWebResourceResource verifiedSite = verifySite(siteUrl, siteVerification);
    System.out.println("Verification successful.");

    System.out.println("Congratulations, you're now a verified owner of this site!\n"
        + "Do you also want to delegate ownership to another individual? (y/n)");
    String delegatedOwner = null;
    if (in.readLine().startsWith("y")) {
      System.out.println("Enter the email address of a new co-owner: ");
      delegatedOwner = in.readLine();
      addDelegatedOwner(delegatedOwner, siteUrl, siteVerification, verifiedSite);
      System.out.println("Delegation successful.");
    }

    System.out.println("\n\nHere are all of the sites you own:");
    List<SiteVerificationWebResourceResource> resources = listOwnedSites(siteVerification);
    if (!resources.isEmpty()) {
      for (SiteVerificationWebResourceResource nextResource : resources) {
        System.out.println(nextResource);
      }
    } else {
      System.out.println("You do not have any verified sites yet!");
    }

    System.out.println("\n\nLet's clean up. Do you want to unverify the site that "
        + "you have just verified? (y/n)\n"
        + "Remember that you will need to remove your token prior to unverification.");

    if (in.readLine().startsWith("y")) {
      try {
        if (delegatedOwner != null) {
          System.out.print("Undelegating co-owner prior to unverifying yourself... ");
          removeDelegatedOwner(delegatedOwner, siteUrl, siteVerification, verifiedSite);
          System.out.println("done.");
        }

        System.out.print("Unverifying your site... ");
        unVerifySite(siteUrl, siteVerification);
        System.out.println("done.");
      } catch (HttpResponseException hre) {
        if (hre.getStatusCode() == 400) {
          System.err.println("Unverification failed, because "
              + "you have not yet removed your verification tokens from the site.");
        }
      }
    }
  }

  public static void main(String[] args) {
    try {
      run();
      // success!
      return;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  /**
   * This method demonstrates an example of a <a href=
   * 'https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_getToken'>getToken</a>
   * call.
   */
  private static String getToken(String siteUrl, SiteVerification siteVerification)
      throws IOException {
    SiteVerificationWebResourceGettokenRequest request =
        new SiteVerificationWebResourceGettokenRequest();
    request.setVerificationMethod(META_VERIFICATION_METHOD);
    request.setSite(new Site().setIdentifier(siteUrl).setType(SITE_TYPE));
    SiteVerificationWebResourceGettokenResponse response =
        siteVerification.webResource().getToken(request).execute();
    return response.getToken();
  }

  /**
   * This method demonstrates an example of an <a href=
   * 'https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_insert'>Insert</a>
   * call.
   */
  private static SiteVerificationWebResourceResource verifySite(
      String siteUrl, SiteVerification siteVerification) throws IOException {
    SiteVerificationWebResourceResource resource = new SiteVerificationWebResourceResource();
    SiteVerificationWebResourceResource.Site resourceSite =
        new SiteVerificationWebResourceResource.Site();
    resourceSite.setIdentifier(siteUrl);
    resourceSite.setType(SITE_TYPE);
    resource.setSite(resourceSite);
    SiteVerification.WebResource.Insert request =
        siteVerification.webResource().insert(META_VERIFICATION_METHOD, resource);
    return request.execute();
  }

  /**
   * This method demonstrates an example of a <a href=
   * 'https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_delete'>Delete</a>
   * call.
   */
  private static void unVerifySite(String siteUrl, SiteVerification siteVerification)
      throws IOException {
    SiteVerification.WebResource.Delete deleteRequest =
        siteVerification.webResource().delete(siteUrl);
    deleteRequest.execute();
  }

  /**
   * This method demonstrates an example of an <a href=
   * 'https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_update'>Update</a>
   * call.
   */
  private static void addDelegatedOwner(String delegatedOwner, String siteUrl,
      SiteVerification siteVerification, SiteVerificationWebResourceResource verifiedSite)
      throws IOException {
    verifiedSite.getOwners().add(delegatedOwner);
    SiteVerification.WebResource.Update updateRequest =
        siteVerification.webResource().update(siteUrl, verifiedSite);
    updateRequest.execute();
  }

  /**
   * This method demonstrates an example of an <a href=
   * 'https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_update'>Update</a>
   * call.
   */
  private static void removeDelegatedOwner(String delegatedOwner, String siteUrl,
      SiteVerification siteVerification, SiteVerificationWebResourceResource verifiedSite)
      throws IOException {
    verifiedSite.getOwners().remove(delegatedOwner);
    SiteVerification.WebResource.Update updateRequest =
        siteVerification.webResource().update(siteUrl, verifiedSite);
    updateRequest.execute();
  }

  /**
   * This method demonstrates an example of a <a href=
   * 'https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_list'>List</a>
   * call.
   */
  private static List<SiteVerificationWebResourceResource> listOwnedSites(
      SiteVerification siteVerification) throws IOException {
    SiteVerification.WebResource.List listRequest = siteVerification.webResource().list();
    SiteVerificationWebResourceListResponse listResponse = listRequest.execute();
    return listResponse.getItems();
  }
}
