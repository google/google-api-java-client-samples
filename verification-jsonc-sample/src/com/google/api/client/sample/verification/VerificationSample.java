/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.verification;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonHttpParser;
import com.google.api.client.sample.verification.model.Debug;
import com.google.api.client.sample.verification.model.WebResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author Kevin Marshall
 */
public class VerificationSample {
  static final String APP_DESCRIPTION =
    "Site Verification API Java Client Sample";
  static final String META_VERIFICATION_METHOD = "meta";
  static final String SITE_TYPE = "site";

  public static void main(String[] args) {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    Debug.enableLogging();
    HttpTransport.setLowLevelHttpTransport(NetHttpTransport.INSTANCE);
    HttpTransport transport = setUpTransport();
    try {
      try {
        System.out.println("Getting an OAuth access token. " +
            "Please follow the prompts on the browser window.");
        Auth.authorize(transport);

        System.out.println(
            "This is an sample Java-based client for the Google Site " +
            "Verification API.\n" +
            "Your data may be modified as a result of running " +
            "this demonstration.\n" +
            "We recommend that you run this sample with a test account\n" +
            "to avoid any accidental losses of data. Use at your own risk." +
            "\n\n");


        System.out.println("Enter the URL of a site to be verified:");
        String siteUrl = in.readLine();

        // Example of a getToken call.
        // https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_getToken
        String token = WebResource.getToken(transport, siteUrl, SITE_TYPE,
                                            META_VERIFICATION_METHOD);
        System.out.println("Place this META tag on your site:\n\t" + token +
            "\nWhen you are finished, press ENTER to proceed with " +
            "verification.");
        in.readLine();

        // Example of an Insert call.
        // https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_insert
        WebResource verifiedSite = new WebResource();
        verifiedSite.site.identifier = siteUrl;
        verifiedSite.site.type = "site";
        verifiedSite = verifiedSite.executeInsert(transport, "meta");
        System.out.println("Verification successful.");

        // Example of an Update call.
        // https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_update
        System.out.println(
            "Congratulations, you're now a verified owner of this site!\n" +
            "Do you also want to delegate ownership to another individual? (y/n)");
        String delegatedOwner = null;
        if (in.readLine().startsWith("y")) {
          System.out.println("Enter the email address of a new co-owner: ");
          delegatedOwner = in.readLine();
          verifiedSite.owners.add(delegatedOwner);
          verifiedSite = verifiedSite.executeUpdate(transport);
          System.out.println("Delegation successful.");
        }

        System.out.println("\n\nHere are all of the sites you own:");
        showVerifiedSites(transport);

        // Example of Delete call.
        // https://code.google.com/apis/siteverification/v1/reference.html#method_siteVerification_webResource_delete
        System.out.println(
            "\n\nLet's clean up. Do you want to unverify the site that " +
            "you have just verified? (y/n)\n" +
            "Remember that you will need to remove your token prior to unverification.");

        if (in.readLine().startsWith("y")) {
          try {
            if (delegatedOwner != null) {
              // Another example of an Update call.
              System.out.print(
                  "Undelegating co-owner prior to unverifying yourself... ");
              verifiedSite.owners.remove(delegatedOwner);
              verifiedSite = verifiedSite.executeUpdate(transport);
              System.out.println("done.");
            }

            System.out.print("Unverifying your site... ");
            verifiedSite.executeDelete(transport);
            System.out.println("done.");
          } catch (HttpResponseException hre) {
            if (hre.response.statusCode == 400) {
              System.err.println("Unverification failed, because " +
                  "you have not yet removed your verification tokens from the site.");
            }
          }
        }

        Auth.revoke();
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }

    System.out.println("All done. Quitting.");
  }

  private static HttpTransport setUpTransport() {
    HttpTransport transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("Google-VerificationSample/1.0");
    transport.addParser(new JsonHttpParser());
    return transport;
  }

  private static void showVerifiedSites(HttpTransport transport)
    throws IOException {
    List<WebResource> resources = WebResource.executeList(transport);

    if (!resources.isEmpty()) {
      for (WebResource nextResource : resources) {
        System.out.println("\t" + Json.toString(nextResource));
      }
    } else {
      System.out.println("You do not have any verified sites yet!");
    }
  }
}
