/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.urlshortener.appengine.robots;

import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.extensions.auth.helpers.Credential;
import com.google.api.client.extensions.auth.helpers.TwoLeggedFlow;
import com.google.api.client.extensions.servlet.auth.AbstractFlowUserServlet;
import com.google.api.client.extensions.servlet.auth.AbstractTwoLeggedFlowServlet;
import com.google.api.client.googleapis.extensions.auth.helpers.appengine.GoogleAppAssertionFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;
import com.google.api.services.urlshortener.model.UrlHistory;
import com.google.appengine.api.appidentity.AppIdentityService;
import com.google.appengine.api.appidentity.AppIdentityServiceFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the main entry point for the example. It uses the
 * {@link AbstractFlowUserServlet} to require an authorized session for the
 * current user prior to invoking the
 * {@link #doGet(HttpServletRequest, HttpServletResponse)} method.
 *
 * @author moshenko@google.com (Jacob Moshenko)
 *
 */
public class SampleApp extends AbstractTwoLeggedFlowServlet {

  @Override
  protected TwoLeggedFlow newFlow(String userId) {
    return new GoogleAppAssertionFlow(userId, "https://www.googleapis.com/auth/urlshortener",
        getHttpTransport(), getJsonFactory());
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Credential credential = getCredential(req);
    Urlshortener shortener = new Urlshortener(getHttpTransport(), credential, getJsonFactory());
    UrlHistory history = shortener.url.list().execute();

    resp.setContentType("text/html");
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
    writer.append(
        "<html><body><form action=\".\" method=\"post\">Long Url: "
            + "<input name=\"longUrl\" type=\"text\" /><input type=\"submit\" /></form><table><tr>"
            + "<th>Original</th><th>Shortened</th></tr>");

    for (Url oneShortened : history.items) {
      writer.append("<tr><td>");
      writer.append(oneShortened.longUrl).append("</td><td><a href=\"").append(oneShortened.id);
      writer.append("\">").append(oneShortened.id);
      writer.append("</a></td></tr>");
    }

    writer.append("</table></body></html>");
    writer.flush();
    resp.setStatus(200);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Object longUrlObj = req.getParameter("longUrl");
    if (longUrlObj == null) {
      resp.sendError(400, "Must include the longUrl parameter.");
      return;
    }
    String longUrl = String.valueOf(longUrlObj);
    Credential credential = getCredential(req);

    Urlshortener shortener = new Urlshortener(getHttpTransport(), credential, getJsonFactory());
    Url toInsert = new Url();
    toInsert.longUrl = longUrl;
    Url shortened = shortener.url.insert(toInsert).execute();

    resp.sendRedirect("/");
  }

  @Override
  protected PersistenceManagerFactory getPersistenceManagerFactory() {
    return PMF.get();
  }

  @Override
  protected String getUserId() {
    AppIdentityService service = AppIdentityServiceFactory.getAppIdentityService();
    return service.getServiceAccountName();
  }

  @Override
  protected HttpTransport newHttpTransportInstance() {
    return new UrlFetchTransport();
  }

  @Override
  protected JsonFactory newJsonFactoryInstance() {
    return new JacksonFactory();
  }
}
