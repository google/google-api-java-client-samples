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

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2ClientCredentials;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;
import com.google.api.services.siteVerification.SiteVerification;
import com.google.api.services.siteVerification.SiteVerificationRequest;
import com.google.api.services.siteVerification.model.SiteverificationWebResourceGettokenResponse;
import com.google.api.services.siteVerification.model.SiteverificationWebResourceListResponse;
import com.google.api.services.siteVerification.model.SiteverificationWebResourceResource;
import com.google.api.services.siteVerification.model.SiteverificationWebResourceResourceSite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Kevin Marshall
 */
public class VerificationSample {

  private static final String META_VERIFICATION_METHOD = "meta";
  private static final String SITE_TYPE = "site";

  /** OAuth2 scope. */
  private static final String SCOPE = "https://www.googleapis.com/auth/siteverification";

  private static void run(JsonFactory jsonFactory) throws Exception {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    // authorization
    System.out.println("Getting an OAuth access token. "
        + "Please follow the prompts on the browser window.");
    HttpTransport transport = new NetHttpTransport();
    GoogleAccessProtectedResource accessProtectedResource =
        OAuth2Native.authorize(transport, jsonFactory, new LocalServerReceiver(), null,
            "google-chrome", OAuth2ClientCredentials.CLIENT_ID,
            OAuth2ClientCredentials.CLIENT_SECRET, SCOPE);

    System.out.println("This is an sample Java-based client for the Google Site "
        + "Verification API.\n" + "Your data may be modified as a result of running "
        + "this demonstration.\n" + "We recommend that you run this sample with a test account\n"
        + "to avoid any accidental losses of data. Use at your own risk." + "\n\n");

    System.out.println("Enter the URL of a site to be verified:");
    String siteUrl = in.readLine();

    // set up SiteVerification
    SiteVerification siteVerification =
        SiteVerification.builder(new NetHttpTransport(), jsonFactory)
            .setApplicationName("Google-SiteVerificationSample/1.0")
            .setHttpRequestInitializer(accessProtectedResource)
            .setJsonHttpRequestInitializer(new JsonHttpRequestInitializer() {
              @Override
              public void initialize(JsonHttpRequest request) {
                SiteVerificationRequest verificationRequest = (SiteVerificationRequest) request;
                verificationRequest.setPrettyPrint(true);
              }
            }).build();

    String token = getToken(siteUrl, siteVerification);
    System.out.println("Place this META tag on your site:\n\t" + token
        + "\nWhen you are finished, press ENTER to proceed with " + "verification.");
    in.readLine();
    SiteverificationWebResourceResource verifiedSite = verifySite(siteUrl, siteVerification);
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
    List<SiteverificationWebResourceResource> resources = listOwnedSites(siteVerification);
    if (!resources.isEmpty()) {
      for (SiteverificationWebResourceResource nextResource : resources) {
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
        if (hre.getResponse().getStatusCode() == 400) {
          System.err.println("Unverification failed, because "
              + "you have not yet removed your verification tokens from the site.");
        }
      }
    }
  }

  public static void main(String[] args) {
    JsonFactory jsonFactory = new JacksonFactory();
    try {
      try {
        OAuth2ClientCredentials.errorIfNotSpecified();
        run(jsonFactory);
        // success!
        return;
      } catch (GoogleJsonResponseException e) {
        // message already includes parsed response
        System.err.println(e.getMessage());
      } catch (HttpResponseException e) {
        // message doesn't include parsed response
        System.err.println(e.getMessage());
        System.err.println(e.getResponse().parseAsString());
      }
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
    SiteVerification.WebResource.GetToken request = siteVerification.webResource().getToken();
    request.setVerificationMethod(META_VERIFICATION_METHOD);
    request.setIdentifier(siteUrl);
    request.setType(SITE_TYPE);
    SiteverificationWebResourceGettokenResponse response = request.execute();
    return response.getToken();
  }

  /**
   * This method demonstrates an example of an <a href=
   * 'https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_insert'>Insert</a>
   * call.
   */
  private static SiteverificationWebResourceResource verifySite(String siteUrl,
      SiteVerification siteVerification) throws IOException {
    SiteverificationWebResourceResource resource = new SiteverificationWebResourceResource();
    SiteverificationWebResourceResourceSite resourceSite =
        new SiteverificationWebResourceResourceSite();
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
      SiteVerification siteVerification, SiteverificationWebResourceResource verifiedSite)
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
      SiteVerification siteVerification, SiteverificationWebResourceResource verifiedSite)
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
  private static List<SiteverificationWebResourceResource> listOwnedSites(
      SiteVerification siteVerification) throws IOException {
    SiteVerification.WebResource.List listRequest = siteVerification.webResource().list();
    SiteverificationWebResourceListResponse listResponse = listRequest.execute();
    return listResponse.getItems();
  }
}
