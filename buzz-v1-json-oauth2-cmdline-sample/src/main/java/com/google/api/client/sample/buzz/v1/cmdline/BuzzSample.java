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
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

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
    HttpTransport transport = new NetHttpTransport();
    JsonFactory factory = new JacksonFactory();
    try {
      try {
        // set up and authorization
        Buzz buzz = new Buzz("Google-BuzzSample/1.0", transport, factory);
        buzz.prettyPrint = true;
        OAuth2Native.authorize(transport, factory);
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
        GoogleJsonError errorResponse = GoogleJsonError.parse(factory, e.response);
        System.err.println(errorResponse.code + " Error: " + errorResponse.message);
        for (ErrorInfo error : errorResponse.errors) {
          System.err.println(factory.toString(error));
        }
        System.exit(1);
      }
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }
}
