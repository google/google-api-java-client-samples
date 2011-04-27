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

import com.google.api.client.auth.oauth2.draft10.AccessProtectedResource;
import com.google.api.client.auth.oauth2.draft10.AccessTokenErrorResponse;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.auth.oauth2.draft10.InstalledApp;
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

  static void authorize(HttpTransport transport, JsonFactory jsonFactory) throws Exception {
    // TODO(yanivi): store the refresh & access tokens locally (after handling token refresh)
    launchInBrowser();
    exchangeCodeForAccessToken(transport, jsonFactory);
  }

  private static void launchInBrowser() {
    String authorizationUrl =
        new GoogleAuthorizationRequestUrl(OAuth2ClientCredentials.CLIENT_ID,
            InstalledApp.OOB_REDIRECT_URI, OAuth2ClientCredentials.SCOPE).build();
    // launch in browser
    boolean browsed = false;
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Action.BROWSE)) {
        try {
          desktop.browse(URI.create(authorizationUrl));
          browsed = true;
        } catch (Exception e) {
          System.out.println();
          System.err.println("Error: " + e.getMessage());
          System.out.println();
        }
      }
    }
    if (!browsed) {
      System.out.println("Open the following address in your favorite browser:");
      System.out.println("  " + authorizationUrl);
    }
  }

  private static void exchangeCodeForAccessToken(HttpTransport transport, JsonFactory jsonFactory)
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
        GoogleAuthorizationCodeGrant request =
            new GoogleAuthorizationCodeGrant(new NetHttpTransport(),
                jsonFactory,
                OAuth2ClientCredentials.CLIENT_ID,
                OAuth2ClientCredentials.CLIENT_SECRET,
                code,
                InstalledApp.OOB_REDIRECT_URI);
        AccessTokenResponse response = request.execute().parseAs(AccessTokenResponse.class);
        // set the access token into the authorization header
        AccessProtectedResource.usingAuthorizationHeader(transport, response.accessToken);
        return;
      } catch (HttpResponseException e) {
        AccessTokenErrorResponse response = e.response.parseAs(AccessTokenErrorResponse.class);
        System.out.println();
        System.err.println("Error: " + response.error);
        System.out.println();
      }
    }
  }
}
