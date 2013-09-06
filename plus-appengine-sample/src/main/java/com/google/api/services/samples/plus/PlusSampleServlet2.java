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
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthServlet;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.GoogleAppEngineOAuthApplicationContext;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Person;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Entry servlet for the Plus App Engine Sample. Demonstrates how to make an authenticated API call
 * using OAuth2 helper classes.
 *
 * @author Nick Miceli
 */
public class PlusSampleServlet2 extends AbstractAppEngineAuthServlet {

  public PlusSampleServlet2() {
    super(new GoogleAppEngineOAuthApplicationContext("/plussampleservlet", "/client_secrets.json",
        Collections.singleton(PlusScopes.PLUS_ME), ""));
  }

  private static final long serialVersionUID = 1L;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    // TODO(NOW): Update this using the createService method.
    // Currently not doing so for comparison purposes. See PlusSampleServlet.java
    AuthorizationCodeFlow authFlow = getOAuthApplicationContext().getFlow();
    Credential credential = authFlow.loadCredential(getOAuthApplicationContext().getUserId(req));
    Plus plus = new Plus.Builder(
        authFlow.getTransport(), authFlow.getJsonFactory(), credential).setApplicationName("")
        .build();
    Person profile = plus.people().get("me").execute();
    PrintWriter respWriter = resp.getWriter();
    resp.setStatus(200);
    resp.setContentType("text/html");
    respWriter.println("<img src='" + profile.getImage().getUrl() + "'>");
    respWriter.println("<a href='" + profile.getUrl() + "'>" + profile.getDisplayName() + "</a>");
    respWriter.println("<p> Birthday: " + profile.getBirthday() + "</p>");
    respWriter.println("<p> Gender: " + profile.getGender() + "</p>");
    respWriter.println("<p> Current Location: " + profile.getCurrentLocation() + "</p>");
  }

}
