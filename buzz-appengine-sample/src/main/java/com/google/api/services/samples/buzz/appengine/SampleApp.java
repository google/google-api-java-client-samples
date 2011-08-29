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

package com.google.api.services.samples.buzz.appengine;

import com.google.api.client.extensions.appengine.auth.AbstractAppEngineFlowServlet;
import com.google.api.client.extensions.auth.helpers.Credential;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.extensions.servlet.auth.AbstractFlowUserServlet;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth2.draft10.GoogleOAuth2ThreeLeggedFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.services.buzz.Buzz;
import com.google.api.services.buzz.model.Activity;
import com.google.api.services.buzz.model.ActivityFeed;
import com.google.api.services.samples.shared.oauth2.OAuth2ClientCredentials;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the main entry point for the example. It uses the {@link AbstractFlowUserServlet} to
 * require an authorized session for the current user prior to invoking {@link #doGet}.
 *
 * @author moshenko@google.com (Jacob Moshenko)
 */
public class SampleApp extends AbstractAppEngineFlowServlet {
  private static final long serialVersionUID = 1L;

  /** OAuth 2 scope. */
  static final String SCOPE = "https://www.googleapis.com/auth/buzz.readonly";

  private GenericUrl callbackUrl;

  /**
   * Returns the full URL of the request including the query parameters.
   */
  static String getFullURL(HttpServletRequest request) {
    StringBuffer buf = request.getRequestURL();
    if (request.getQueryString() != null) {
      buf.append('?').append(request.getQueryString());
    }
    return buf.toString();
  }

  @Override
  protected ThreeLeggedFlow newFlow(String userId) {
    return new GoogleOAuth2ThreeLeggedFlow(userId, OAuth2ClientCredentials.CLIENT_ID,
        OAuth2ClientCredentials.CLIENT_SECRET, SCOPE, callbackUrl.build());
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    callbackUrl = new GenericUrl(getFullURL(req));
    callbackUrl.setRawPath("/oauth2callback");
    super.service(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Credential credential = getCredential(req);
    // List activities using the generated client
    Buzz buzz = new Buzz(getHttpTransport(), credential, getJsonFactory());
    ActivityFeed activities = buzz.activities.list("me", "@self").execute();
    resp.setContentType("text/plain");
    PrintWriter writer = resp.getWriter();
    writer.println("Activities:");
    for (Activity oneAct : activities.getItems()) {
      writer.println(oneAct.getTitle());
    }
  }

  @Override
  protected PersistenceManagerFactory getPersistenceManagerFactory() {
    return PMF.get();
  }
}
