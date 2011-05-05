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

package com.google.api.client.sample.buzz.v1.cmdline;

import com.google.api.client.auth.oauth2.draft10.AccessTokenErrorResponse;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.auth.oauth2.draft10.InstalledApp;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

/**
 * Implements OAuth authentication "native" flow recommended for installed clients in which the end
 * user must grant access in a web browser and then copy a code into the application.
 *
 * @author Yaniv Inbar
 */
class OAuth2Native {

  /*
   * NOTE: ideally one should leverage the OS functionalities to store the access token and refresh
   * token in encrypted form across invocation of this application. References: <a
   * href="http://msdn.microsoft.com/en-us/library/ms717803(VS.85).aspx">Windows Data Protection
   * Techniques</a> and <a href=
   * "http://developer.apple.com/library/mac/#documentation/Security/Conceptual/keychainServConcepts/01introduction/introduction.html"
   * >Keychains on Mac</a>.
   */

  static GoogleAccessProtectedResource authorize(HttpTransport transport, JsonFactory jsonFactory,
      String clientId, String clientSecret, String scope) throws IOException {
    launchInBrowser(clientId, scope);
    return exchangeCodeForAccessToken(transport, jsonFactory, clientId, clientSecret);
  }

  private static void launchInBrowser(String clientId, String scope) throws IOException {
    String authorizationUrl =
        new GoogleAuthorizationRequestUrl(clientId, InstalledApp.OOB_REDIRECT_URI, scope).build();
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Action.BROWSE)) {
        desktop.browse(URI.create(authorizationUrl));
        return;
      }
    }
    System.out.println("Open the following address in your favorite browser:");
    System.out.println("  " + authorizationUrl);
  }

  private static GoogleAccessProtectedResource exchangeCodeForAccessToken(
      HttpTransport transport, JsonFactory jsonFactory, String clientId, String clientSecret)
      throws IOException {
    while (true) {
      try {
        // request code from user
        String code = "";
        while (code.isEmpty()) {
          System.out.print("Please enter code: ");
          code = new Scanner(System.in).nextLine();
        }
        // exchange code for an access token
        final AccessTokenResponse response =
            new GoogleAuthorizationCodeGrant(new NetHttpTransport(),
                jsonFactory,
                clientId,
                clientSecret,
                code,
                InstalledApp.OOB_REDIRECT_URI).execute();
        // see comment above about storing access and refresh tokens
        return new GoogleAccessProtectedResource(response.accessToken,
            transport,
            jsonFactory,
            clientId,
            clientSecret,
            response.refreshToken) {

          @Override
          protected void onAccessToken(String accessToken) {
            // see comment above about storing access and refresh tokens
          }
        };
      } catch (HttpResponseException e) {
        AccessTokenErrorResponse response = e.response.parseAs(AccessTokenErrorResponse.class);
        System.out.println();
        System.err.println("Error: " + response.error);
        System.out.println();
      }
    }
  }

  private OAuth2Native() {
  }
}
