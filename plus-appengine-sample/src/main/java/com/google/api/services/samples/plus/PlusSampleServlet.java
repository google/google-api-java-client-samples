/*
 * Copyright (c) 2013 Google Inc.
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

package com.google.api.services.samples.plus;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Entry sevlet for the Plus App Engine Sample. Demonstrates how to make an authenticated API call
 * using OAuth2 helper classes.
 *
 * @author Nick Miceli
 */
public class PlusSampleServlet extends AbstractAppEngineAuthorizationCodeServlet {

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    // Get the stored credentials using the Authorization Flow
    AuthorizationCodeFlow authFlow = initializeFlow();
    Credential credential = authFlow.loadCredential(getUserId(req));
    // Build the Plus object using the credentials
    Plus plus = new Plus.Builder(
        Utils.HTTP_TRANSPORT, Utils.JSON_FACTORY, credential).setApplicationName("").build();
    // Make the API call
    Person profile = plus.people().get("me").execute();
    // Send the results as the response
    PrintWriter respWriter = resp.getWriter();
    resp.setStatus(200);
    resp.setContentType("text/html");
    respWriter.println("<img src='" + profile.getImage().getUrl() + "'>");
    respWriter.println("<a href='" + profile.getUrl() + "'>" + profile.getDisplayName() + "</a>");
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
    return Utils.initializeFlow();
  }

  @Override
  protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
    return Utils.getRedirectUri(req);
  }
}
