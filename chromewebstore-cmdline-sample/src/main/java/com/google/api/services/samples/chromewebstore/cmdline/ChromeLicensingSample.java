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

package com.google.api.services.samples.chromewebstore.cmdline;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.chromewebstore.Chromewebstore;
import com.google.api.services.chromewebstore.model.License;

import java.util.Scanner;

/**
 * @author Yaniv Inbar
 */
public class ChromeLicensingSample {

  private static void run(JsonFactory jsonFactory) throws Exception {
    HttpTransport transport = new NetHttpTransport();
    // Set up Chromewebstore.
    Chromewebstore chromeWebStore = new Chromewebstore(transport, getOAuthParams(),
        jsonFactory);
    chromeWebStore.setApplicationName("Google-ChromeLicensingSample/1.0");
    chromeWebStore.setPrettyPrint(true);
    // Get the License.
    License license = chromeWebStore.licenses.get(
        ClientCredentials.APP_ID, inputUsername()).execute();
    System.out.print("YES".equals(license.getResult())
        ? "FULL".equals(license.getAccessLevel()) ? "Full" : "Free" : "No");
    System.out.println(" License.");
  }

  private static OAuthParameters getOAuthParams() {
    OAuthParameters authorizer = new OAuthParameters();
    authorizer.consumerKey = "anonymous";
    authorizer.token = ClientCredentials.OAUTH_TOKEN;
    OAuthHmacSigner signer = new OAuthHmacSigner();
    signer.clientSharedSecret = "anonymous";
    signer.tokenSharedSecret = ClientCredentials.OAUTH_TOKEN_SECRET;
    authorizer.signer = signer;
    return authorizer;
  }

  public static void main(String[] args) {
    JsonFactory jsonFactory = new JacksonFactory();
    try {
      try {
        if (ClientCredentials.APP_ID == null || ClientCredentials.OAUTH_TOKEN == null ||
            ClientCredentials.OAUTH_TOKEN_SECRET == null) {
          System.err.println(
              "Please enter your client ID and secret in " + ClientCredentials.class);
          System.exit(1);
        } else {
          run(jsonFactory);
        }
        // success!
        return;
      } catch (HttpResponseException e) {
        if (!Json.CONTENT_TYPE.equals(e.getResponse().getContentType())) {
          System.err.println(e.getResponse().parseAsString());
        } else {
          GoogleJsonError errorResponse = GoogleJsonError.parse(jsonFactory, e.getResponse());
          System.err.println(errorResponse.code + " Error: " + errorResponse.message);
          for (ErrorInfo error : errorResponse.errors) {
            System.err.println(jsonFactory.toString(error));
          }
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static String inputUsername() {
    System.out.print("Username to check license for: ");
    Scanner scanner = new Scanner(System.in);
    return scanner.next();
  }
}
