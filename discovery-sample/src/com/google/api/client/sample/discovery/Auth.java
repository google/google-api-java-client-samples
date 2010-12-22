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

package com.google.api.client.sample.discovery;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetAccessToken;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetTemporaryToken;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceMethod;
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

  private static final String APP_NAME = "Google Discovery API Client 1.0.0";

  private static OAuthHmacSigner signer;

  private static OAuthCredentialsResponse credentials;

  static void authorize(HttpTransport transport, String apiName, ServiceMethod method)
      throws Exception {
    // callback server
    LoginCallbackServer callbackServer = null;
    String verifier = null;
    String tempToken = null;
    try {
      callbackServer = new LoginCallbackServer();
      callbackServer.start();
      // temporary token
      GoogleOAuthGetTemporaryToken temporaryToken = new GoogleOAuthGetTemporaryToken();
      signer = new OAuthHmacSigner();
      signer.clientSharedSecret = "anonymous";
      temporaryToken.signer = signer;
      temporaryToken.consumerKey = "anonymous";
      temporaryToken.scope = "https://www.googleapis.com/auth/" + apiName;
      temporaryToken.displayName = APP_NAME;
      temporaryToken.callback = callbackServer.getCallbackUrl();
      OAuthCredentialsResponse tempCredentials = temporaryToken.execute();
      signer.tokenSharedSecret = tempCredentials.tokenSecret;
      // authorization URL
      OAuthAuthorizeTemporaryTokenUrl authorizeUrl;
      if (apiName.equals("buzz")) {
        authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(
            "https://www.google.com/buzz/api/auth/OAuthAuthorizeToken");
        authorizeUrl.set("scope", temporaryToken.scope);
        authorizeUrl.set("domain", "anonymous");
        authorizeUrl.set("xoauth_displayname", APP_NAME);
      } else if (apiName.equals("latitude")) {
        // TODO: test!
        authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(
            "https://www.google.com/latitude/apps/OAuthAuthorizeToken");
      } else {
        authorizeUrl = new GoogleOAuthAuthorizeTemporaryTokenUrl();
      }
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
    accessToken.temporaryToken = tempToken;
    accessToken.signer = signer;
    accessToken.consumerKey = "anonymous";
    accessToken.verifier = verifier;
    credentials = accessToken.execute();
    signer.tokenSharedSecret = credentials.tokenSecret;
    createOAuthParameters().signRequestsUsingAuthorizationHeader(transport);
  }

  static void revoke() {
    if (credentials != null) {
      try {
        GoogleOAuthGetAccessToken.revokeAccessToken(createOAuthParameters());
      } catch (Exception e) {
        e.printStackTrace(System.err);
      }
    }
  }

  private static OAuthParameters createOAuthParameters() {
    OAuthParameters authorizer = new OAuthParameters();
    authorizer.consumerKey = "anonymous";
    authorizer.signer = signer;
    authorizer.token = credentials.token;
    return authorizer;
  }
}
