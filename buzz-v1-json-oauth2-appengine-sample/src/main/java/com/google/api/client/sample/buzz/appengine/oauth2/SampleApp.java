// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.buzz.appengine.oauth2;

import com.google.api.client.extensions.appengine.auth.AbstractAppEngineFlowServlet;
import com.google.api.client.extensions.auth.helpers.Credential;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.extensions.servlet.auth.AbstractFlowUserServlet;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth2.draft10.GoogleOAuth2ThreeLeggedFlow;
import com.google.api.services.buzz.v1.Buzz;
import com.google.api.services.buzz.v1.model.Activity;
import com.google.api.services.buzz.v1.model.ActivityFeed;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the main entry point for the example. It uses the
 * {@link AbstractFlowUserServlet} to require an authorized session for the
 * current user prior to invoking the {@link
 * #doGetWithCredentials(HttpServletRequest, HttpServletResponse, Credential)}
 * method.
 *
 * @author moshenko@google.com (Jacob Moshenko)
 *
 */
public class SampleApp extends AbstractAppEngineFlowServlet {
  @Override
  protected ThreeLeggedFlow newFlow(String userId) {
    return new GoogleOAuth2ThreeLeggedFlow(userId,
        OAuth2ClientCredentials.CLIENT_ID,
        OAuth2ClientCredentials.CLIENT_SECRET,
        OAuth2ClientCredentials.SCOPE,
        "http://localhost:8888/oauth2callback");
  }

  @Override
  protected void doGetWithCredentials(
      HttpServletRequest req, HttpServletResponse resp, Credential credential) throws IOException {

    // List activities using the generated client
    Buzz buzz = new Buzz(getHttpTransport(), credential, getJsonFactory());
    ActivityFeed activities = buzz.activities.list("me", "@self").execute();

    resp.setContentType("text/plain");
    PrintWriter writer = resp.getWriter();
    writer.println("Activities:");
    for (Activity oneAct : activities.items) {
      writer.println(oneAct.title);
    }
  }

  @Override
  protected PersistenceManagerFactory getPersistenceManagerFactory() {
    return PMF.get();
  }
}
