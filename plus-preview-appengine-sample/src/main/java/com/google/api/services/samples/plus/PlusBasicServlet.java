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

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusRequestInitializer;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Sample Google+ servlet that does a search on public activities.
 *
 * @author Nick Miceli
 */
public class PlusBasicServlet extends HttpServlet {

  /**
   * Enter your API key here from https://code.google.com/apis/console/?api=plus under "API Access".
   */
  private static final String API_KEY = "";

  private static final long serialVersionUID = 1;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HttpTransport httpTransport = new UrlFetchTransport();
    JsonFactory jsonFactory = new JacksonFactory();

    Plus plus = new Plus.Builder(httpTransport, jsonFactory, null).setApplicationName("")
        .setGoogleClientRequestInitializer(new PlusRequestInitializer(API_KEY)).build();

    ActivityFeed myActivityFeed = plus.activities().search("Google").execute();
    List<Activity> myActivities = myActivityFeed.getItems();

    resp.setContentType("text/html");
    resp.setStatus(200);
    Writer writer = resp.getWriter();
    writer.write("<ul>");
    for (Activity a : myActivities) {
      writer.write("<li>" + a.getTitle() + "</li>");
    }
    writer.write("</ul>");
  }

}
