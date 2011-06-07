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

package com.google.api.client.sample.buzz.cmdline;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.sample.buzz.cmdline.oauth.LocalServerReceiver;
import com.google.api.client.sample.buzz.cmdline.oauth.OAuth2Native;
import com.google.api.services.buzz.Buzz;
import com.google.api.services.buzz.model.Activity;
import com.google.api.services.buzz.model.Group;

/**
 * @author Yaniv Inbar
 */
public class BuzzSample {

  /**
   * Set to {@code true} to only perform read-only actions or {@code false} to also do
   * insert/update/delete.
   */
  private static final boolean READ_ONLY = false;

  private static void run(JsonFactory jsonFactory) throws Exception {
    // authorization
    HttpTransport transport = new NetHttpTransport();
    GoogleAccessProtectedResource accessProtectedResource = OAuth2Native.authorize(transport,
        jsonFactory,
        new LocalServerReceiver(),
        null,
        "google-chrome",
        OAuth2ClientCredentials.CLIENT_ID,
        OAuth2ClientCredentials.CLIENT_SECRET,
        OAuth2ClientCredentials.SCOPE);
    // set up Buzz
    final Buzz buzz = new Buzz(transport, accessProtectedResource, jsonFactory);
    buzz.setApplicationName("Google-BuzzSample/1.0");
    buzz.prettyPrint = true;
    // groups
    GroupActions.showGroups(buzz);
    Group group = null;
    if (!READ_ONLY) {
      group = GroupActions.insertGroup(buzz);
      group = GroupActions.updateGroup(buzz, group);
    }
    // activities
    ActivityActions.showActivitiesForConsumption(buzz);
    ActivityActions.showPersonalActivities(buzz);
    if (!READ_ONLY) {
      Activity activity = ActivityActions.insertActivity(buzz, group);
      activity = ActivityActions.updateActivity(buzz, activity);
      // clean up
      ActivityActions.deleteActivity(buzz, activity);
      GroupActions.deleteGroup(buzz, group);
    }
  }

  public static void main(String[] args) {
    JsonFactory jsonFactory = new JacksonFactory();
    try {
      try {
        if (OAuth2ClientCredentials.CLIENT_ID == null
            || OAuth2ClientCredentials.CLIENT_SECRET == null) {
          System.err.println(
              "Please enter your client ID and secret in " + OAuth2ClientCredentials.class);
        } else {
          run(jsonFactory);
        }
        // success!
        return;
      } catch (HttpResponseException e) {
        if (!e.response.contentType.equals(Json.CONTENT_TYPE)) {
          System.err.println(e.response.parseAsString());
        } else {
          GoogleJsonError errorResponse = GoogleJsonError.parse(jsonFactory, e.response);
          System.err.println(errorResponse.code + " Error: " + errorResponse.message);
          for (ErrorInfo error : errorResponse.errors) {
            System.err.println(jsonFactory.toString(error));
          }
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }
}
