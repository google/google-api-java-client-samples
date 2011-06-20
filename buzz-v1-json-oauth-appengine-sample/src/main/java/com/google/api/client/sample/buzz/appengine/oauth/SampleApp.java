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

package com.google.api.client.sample.buzz.appengine.oauth;

import com.google.api.client.extensions.appengine.auth.AbstractAppEngineFlowServlet;
import com.google.api.client.extensions.auth.helpers.Credential;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.extensions.servlet.auth.AbstractFlowUserServlet;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth.GoogleOAuthHmacThreeLeggedFlow;
import com.google.api.services.buzz.Buzz;
import com.google.api.services.buzz.model.Activity;
import com.google.api.services.buzz.model.ActivityFeed;
import com.google.api.services.buzz.model.ActivityObject;
import com.google.api.services.buzz.model.ActivityVisibility;
import com.google.api.services.buzz.model.ActivityVisibilityEntries;
import com.google.api.services.buzz.model.Group;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is the main entry point for the example. It uses the
 * {@link AbstractFlowUserServlet} to require an authorized session for the
 * current user prior to invoking the
 * {@link #doGet(HttpServletRequest, HttpServletResponse)} method.
 *
 * <p>
 * This application will post to a private group within your Buzz stream. The
 * output will only be visible to you.
 * </p>
 *
 * @author moshenko@google.com (Jacob Moshenko)
 *
 */
public class SampleApp extends AbstractAppEngineFlowServlet {
  @Override
  protected ThreeLeggedFlow newFlow(String userId) throws IOException {
    return new GoogleOAuthHmacThreeLeggedFlow(userId,
        OAuthClientCredentials.CONSUMER_KEY,
        OAuthClientCredentials.CONSUMER_SECRET,
        OAuthClientCredentials.SCOPE,
        OAuthClientCredentials.X_OAUTH_DISPLAYNAME,
        "http://localhost:8888/oauthcallback",
        getHttpTransport());
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    // List activities using the generated client
    Credential credential = getCredential(req);
    Buzz buzz = new Buzz(getHttpTransport(), credential, getJsonFactory());

    Group g = new Group();
    g.title = "Temporary buzz group: " + new Date();
    Group newGroup = buzz.groups.insert("@me", g).execute();

    Activity act = new Activity();

    // Visibility
    ActivityVisibilityEntries entry = new ActivityVisibilityEntries();
    entry.id = newGroup.id;
    ActivityVisibility visibility = new ActivityVisibility();
    visibility.entries = Lists.newArrayList();
    visibility.entries.add(entry);
    act.visibility = visibility;

    // Content
    act.buzzObject = new ActivityObject();
    act.buzzObject.content = "Posted from a sample app!";
    act.buzzObject.type = "note";
    Activity inserted = buzz.activities.insert("me", act).execute();

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
