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

import com.google.api.client.googleapis.auth.oauth.GoogleOAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetTemporaryToken;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponseException;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Google Calendar Data API App Engine sample.
 *
 * @author Yaniv Inbar
 */
@SuppressWarnings("serial")
public class CalendarAppEngineSample extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    try {
      String fullUrl = Auth.getFullURL(request);
      // check if we have user credentials
      UserCredentials userCredentials = UserCredentials.forCurrentUser();
      if (userCredentials == null) {
        // get the temporary OAuth token
        GoogleOAuthGetTemporaryToken requestToken = new GoogleOAuthGetTemporaryToken();
        requestToken.displayName = Constants.APP_NAME;
        requestToken.signer = Auth.createSigner(null);
        requestToken.consumerKey = Constants.ENTER_DOMAIN;
        requestToken.scope = CalendarUrl.ROOT_URL;
        GenericUrl url = new GenericUrl(fullUrl);
        url.setRawPath("/access_granted");
        requestToken.callback = url.build();
        userCredentials = Auth.executeGetToken(requestToken, null);
      }
      // have a temporary token?
      if (userCredentials.temporary) {
        // redirect to grant access UI
        GoogleOAuthAuthorizeTemporaryTokenUrl url = new GoogleOAuthAuthorizeTemporaryTokenUrl();
        url.temporaryToken = userCredentials.token;
        response.sendRedirect(url.build());
        return;
      }
      writeHtmlContent(request, response, fullUrl);
    } catch (HttpResponseException e) {
      throw Auth.newIOException(e);
    }
  }

  /**
   * Write GWT HTML hosted content.
   */
  private static void writeHtmlContent(
      HttpServletRequest request, HttpServletResponse response, String fullUrl) throws IOException {
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    PrintWriter writer = response.getWriter();
    writer.println("<!doctype html><html><head>");
    writer.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
    writer.println("<title>" + Constants.APP_NAME + "</title>");
    writer.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"" + Constants.GWT_MODULE_NAME
        + ".css\">");
    writer.println("<script type=\"text/javascript\" language=\"javascript\" " + "src=\""
        + Constants.GWT_MODULE_NAME + "/" + Constants.GWT_MODULE_NAME + ".nocache.js\"></script>");
    writer.println("</head><body>");
    UserService userService = UserServiceFactory.getUserService();
    writer.println("<div class=\"header\"><b>" + request.getUserPrincipal().getName() + "</b> | "
        + "<a href=\"" + userService.createLogoutURL(fullUrl) + "\">Log out</a> | "
        + "<a href=\"http://code.google.com/p/google-api-java-client/source/browse?repo="
        + "samples#hg/calendar-v2-atom-oauth-appengine-sample\">See source code for "
        + "this sample</a></div>");
    writer.println("<div id=\"main\"/>");
    writer.println("</body></html>");
  }
}
