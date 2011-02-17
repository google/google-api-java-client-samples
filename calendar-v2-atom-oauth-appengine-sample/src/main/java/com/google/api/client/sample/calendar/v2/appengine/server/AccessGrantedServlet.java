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

package com.google.api.client.sample.calendar.v2.appengine.server;

import com.google.api.client.auth.oauth.OAuthCallbackUrl;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetAccessToken;
import com.google.api.client.http.HttpResponseException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HTTP servlet to process access granted from user.
 *
 * @author Yaniv Inbar
 */
@SuppressWarnings("serial")
public class AccessGrantedServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      OAuthCallbackUrl url = new OAuthCallbackUrl(Auth.getFullURL(request));
      UserCredentials temporaryCredentials = UserCredentials.forCurrentUser();
      if (!url.token.equals(temporaryCredentials.token)) {
        // OAuth token belongs to a different user from the logged-in user
        String fullUrl = Auth.getFullURL(request);
        UserService userService = UserServiceFactory.getUserService();
        response.sendRedirect(userService.createLogoutURL(fullUrl));
        return;
      }
      // gets an access OAuth token upgraded from a temporary token
      GoogleOAuthGetAccessToken accessToken = new GoogleOAuthGetAccessToken();
      accessToken.signer = Auth.createSigner(temporaryCredentials);
      accessToken.consumerKey = Constants.ENTER_DOMAIN;
      accessToken.temporaryToken = temporaryCredentials.token;
      accessToken.verifier = url.verifier;
      Auth.executeGetToken(accessToken, temporaryCredentials);
      // redirect back to application, but clear token and verifier query parameters
      url.setRawPath("/");
      url.verifier = null;
      url.token = null;
      response.sendRedirect(url.build());
    } catch (HttpResponseException e) {
      Auth.newIOException(e);
    }
  }
}
