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

package com.google.api.client.sample.calendar.v2.model;

import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetAccessToken;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetTemporaryToken;
import com.google.api.client.http.HttpTransport;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.net.URI;

/**
 * Implements OAuth authentication.
 *
 * @author Yaniv Inbar
 */
public class Auth {

  private static final String APP_NAME = "Google Calendar Data API Java Client Sample";

  private static OAuthHmacSigner signer;

  private static OAuthCredentialsResponse credentials;

  static OAuthParameters authorize(
      HttpTransport transport, String consumerKey, String consumerSecret) throws Exception {
    // callback server
    LoginCallbackServer callbackServer = null;
    String verifier = null;
    String tempToken = null;
    try {
      callbackServer = new LoginCallbackServer();
      callbackServer.start();
      // temporary token
      GoogleOAuthGetTemporaryToken temporaryToken = new GoogleOAuthGetTemporaryToken();
      temporaryToken.transport = transport;
      signer = new OAuthHmacSigner();
      signer.clientSharedSecret = consumerSecret;
      temporaryToken.signer = signer;
      temporaryToken.consumerKey = consumerKey;
      temporaryToken.scope = CalendarUrl.ROOT_URL;
      temporaryToken.displayName = APP_NAME;
      temporaryToken.callback = callbackServer.getCallbackUrl();
      OAuthCredentialsResponse tempCredentials = temporaryToken.execute();
      signer.tokenSharedSecret = tempCredentials.tokenSecret;
      // authorization URL
      GoogleOAuthAuthorizeTemporaryTokenUrl authorizeUrl =
          new GoogleOAuthAuthorizeTemporaryTokenUrl();
      authorizeUrl.temporaryToken = tempToken = tempCredentials.token;
      String authorizationUrl = authorizeUrl.build();
      // launch in browser
      boolean browsed = false;
      if (Desktop.isDesktopSupported()) {
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Action.BROWSE)) {
          desktop.browse(URI.create(authorizationUrl));
          browsed = true;
        }
      }
      if (!browsed) {
        String browser = "google-chrome";
        Runtime.getRuntime().exec(new String[] {browser, authorizationUrl});
      }
      verifier = callbackServer.waitForVerifier(tempToken);
    } finally {
      if (callbackServer != null) {
        callbackServer.stop();
      }
    }
    GoogleOAuthGetAccessToken accessToken = new GoogleOAuthGetAccessToken();
    accessToken.transport = transport;
    accessToken.temporaryToken = tempToken;
    accessToken.signer = signer;
    accessToken.consumerKey = consumerKey;
    accessToken.verifier = verifier;
    credentials = accessToken.execute();
    signer.tokenSharedSecret = credentials.tokenSecret;
    return createOAuthParameters(consumerKey);
  }

  static void revoke(HttpTransport transport, String consumerKey) {
    if (credentials != null) {
      try {
        GoogleOAuthGetAccessToken.revokeAccessToken(transport, createOAuthParameters(consumerKey));
      } catch (Exception e) {
        e.printStackTrace(System.err);
      }
    }
  }

  private static OAuthParameters createOAuthParameters(String consumerKey) {
    OAuthParameters authorizer = new OAuthParameters();
    authorizer.consumerKey = consumerKey;
    authorizer.signer = signer;
    authorizer.token = credentials.token;
    return authorizer;
  }
}
