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

package com.google.api.client.sample.buzz;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetAccessToken;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetTemporaryToken;
import com.google.api.client.sample.buzz.model.Util;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.net.URI;

/**
 * Implements OAuth authentication.
 *
 * @author Yaniv Inbar
 */
public class Auth {

  private static OAuthHmacSigner signer;

  private static OAuthCredentialsResponse credentials;

  static void authorize() throws Exception {
    // callback server
    LoginCallbackServer callbackServer = null;
    String verifier = null;
    String tempToken = null;
    try {
      callbackServer = new LoginCallbackServer();
      callbackServer.start();
      // temporary token
      GoogleOAuthGetTemporaryToken temporaryToken = new GoogleOAuthGetTemporaryToken();
      temporaryToken.transport = Util.AUTH_TRANSPORT;
      signer = new OAuthHmacSigner();
      signer.clientSharedSecret = "anonymous";
      temporaryToken.signer = signer;
      temporaryToken.consumerKey = "anonymous";
      temporaryToken.scope = "https://www.googleapis.com/auth/buzz";
      temporaryToken.displayName = BuzzSample.APP_DESCRIPTION;
      temporaryToken.callback = callbackServer.getCallbackUrl();
      OAuthCredentialsResponse tempCredentials = temporaryToken.execute();
      signer.tokenSharedSecret = tempCredentials.tokenSecret;
      // authorization URL
      OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(
          "https://www.google.com/buzz/api/auth/OAuthAuthorizeToken");
      authorizeUrl.set("scope", temporaryToken.scope);
      authorizeUrl.set("domain", "anonymous");
      authorizeUrl.set("xoauth_displayname", BuzzSample.APP_DESCRIPTION);
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
    // access token
    GoogleOAuthGetAccessToken accessToken = new GoogleOAuthGetAccessToken();
    accessToken.transport = Util.AUTH_TRANSPORT;
    accessToken.temporaryToken = tempToken;
    accessToken.signer = signer;
    accessToken.consumerKey = "anonymous";
    accessToken.verifier = verifier;
    credentials = accessToken.execute();
    signer.tokenSharedSecret = credentials.tokenSecret;
    createOAuthParameters().signRequestsUsingAuthorizationHeader(Util.TRANSPORT);
  }

  static void revoke() {
    if (credentials != null) {
      try {
        GoogleOAuthGetAccessToken.revokeAccessToken(Util.AUTH_TRANSPORT, createOAuthParameters());
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
