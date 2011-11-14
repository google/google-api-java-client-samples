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

package com.google.api.services.samples.shared.cmdline.oauth2;

import com.google.api.client.auth.oauth2.draft10.AccessTokenErrorResponse;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.samples.shared.cmdline.CmdlineUtils;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;

/**
 * Implements OAuth authentication "native" flow recommended for installed clients in which the end
 * user must grant access in a web browser and then copy a code into the application.
 * 
 * @author Yaniv Inbar
 */
public class OAuth2Native {

  /**
   * Authorizes the installed application to access user's protected data.
   * 
   * @param receiver verification code receiver
   * @param credentialStore credential store or {@code null} for none
   * @param browser browser to open in case {@link Desktop#isDesktopSupported()} is {@code false}.
   *        If {@code null} it will simply prompt user to open the URL in their favorite browser.
   * @param scope OAuth 2.0 scope
   */
  public static GoogleAccessProtectedResource authorize(VerificationCodeReceiver receiver,
      final CredentialStore credentialStore, String browser, String scope) throws Exception {
    AccessTokenResponse response = null;
    if (credentialStore != null) {
      response = credentialStore.read();
    }
    if (response == null) {
      try {
        String redirectUrl = receiver.getRedirectUrl();
        String authorizationUrl =
            new GoogleAuthorizationRequestUrl(OAuth2ClientCredentials.getClientId(), redirectUrl,
                scope).build();
        browse(authorizationUrl, browser);
        response = exchangeCodeForAccessToken(redirectUrl, receiver);
        if (credentialStore != null) {
          credentialStore.write(response);
        }
      } finally {
        receiver.stop();
      }
    }
    final AccessTokenResponse responseForStorage = response;
    return new GoogleAccessProtectedResource(response.accessToken, CmdlineUtils.getHttpTransport(),
        CmdlineUtils.getJsonFactory(), OAuth2ClientCredentials.getClientId(),
        OAuth2ClientCredentials.getClientSecret(), response.refreshToken) {

      @Override
      protected void onAccessToken(String accessToken) {
        if (credentialStore != null) {
          responseForStorage.accessToken = accessToken;
          credentialStore.write(responseForStorage);
        }
      }
    };

  }

  private static void browse(String url, String browser) {
    // first try the Java Desktop
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Action.BROWSE)) {
        try {
          desktop.browse(URI.create(url));
          return;
        } catch (IOException e) {
          // handled below
        }
      }
    }
    // Next try rundll32 (only works on Windows)
    try {
      Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
      return;
    } catch (IOException e) {
      // handled below
    }
    // Next try the requested browser (e.g. "google-chrome")
    if (browser != null) {
      try {
        Runtime.getRuntime().exec(new String[] {browser, url});
      } catch (IOException e) {
        // handled below
      }
    }
    // Finally just ask user to open in their browser using copy-paste
    System.out.println("Please open the following URL in your browser:");
    System.out.println("  " + url);
  }

  private static AccessTokenResponse exchangeCodeForAccessToken(String redirectUrl,
      VerificationCodeReceiver receiver) throws IOException {
    String code = receiver.waitForCode();
    try {
      // exchange code for an access token
      return new GoogleAuthorizationCodeGrant(new NetHttpTransport(),
          CmdlineUtils.getJsonFactory(), OAuth2ClientCredentials.getClientId(),
          OAuth2ClientCredentials.getClientSecret(), code, redirectUrl).execute();
    } catch (HttpResponseException e) {
      AccessTokenErrorResponse response = e.getResponse().parseAs(AccessTokenErrorResponse.class);
      System.out.println();
      System.err.println("Error: " + response.error);
      System.out.println();
      System.exit(1);
      return null;
    }
  }

  private OAuth2Native() {
  }
}
