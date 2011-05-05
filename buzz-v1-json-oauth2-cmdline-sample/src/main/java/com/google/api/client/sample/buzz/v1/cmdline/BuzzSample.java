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

package com.google.api.client.sample.buzz.v1.cmdline;

import com.google.api.buzz.v1.Buzz;
import com.google.api.buzz.v1.model.Activity;
import com.google.api.buzz.v1.model.Group;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.common.base.Preconditions;

/**
 * @author Yaniv Inbar
 */
public class BuzzSample {

  /**
   * Set to {@code true} to only perform read-only actions or {@code false} to also do
   * insert/update/delete.
   */
  private static final boolean READ_ONLY = false;

  public static void main(String[] args) {
    Preconditions.checkArgument(
        OAuth2ClientCredentials.CLIENT_ID != null && OAuth2ClientCredentials.CLIENT_SECRET != null,
        "Please enter your client ID and secret in " + OAuth2ClientCredentials.class);
    HttpTransport transport = new NetHttpTransport();
    JsonFactory jsonFactory = new JacksonFactory();
    try {
      try {
        // authorization
        GoogleAccessProtectedResource accessProtectedResource =
            OAuth2Native.authorize(transport, jsonFactory, OAuth2ClientCredentials.CLIENT_ID,
                OAuth2ClientCredentials.CLIENT_SECRET, OAuth2ClientCredentials.SCOPE);
        // set up Buzz
        final Buzz buzz = new Buzz("Google-BuzzSample/1.0", transport, jsonFactory);
        buzz.prettyPrint = true;
        buzz.setAccessToken(accessProtectedResource.getAccessToken());
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
        System.exit(1);
      }
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }
}
