/*
 * Copyright (c) 2010 Google Inc.
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

package com.google.api.services.samples.plus.cmdline;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusRequest;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;
import com.google.api.services.plus.model.Person;
import com.google.api.services.samples.shared.cmdline.CmdlineUtils;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class PlusSample {

  /** OAuth 2 scope. */
  private static final String SCOPE = "https://www.googleapis.com/auth/plus.me";

  private static Plus plus;

  public static void main(String[] args) {
    try {
      try {
        // authorization
        GoogleAccessProtectedResource accessProtectedResource =
            OAuth2Native.authorize(new LocalServerReceiver(), null, "google-chrome", SCOPE);
        // set up global Plus instance
        plus =
            Plus.builder(CmdlineUtils.getHttpTransport(), CmdlineUtils.getJsonFactory())
                .setApplicationName("Google-PlusSample/1.0")
                .setHttpRequestInitializer(accessProtectedResource)
                .setJsonHttpRequestInitializer(new JsonHttpRequestInitializer() {
                  @Override
                  public void initialize(JsonHttpRequest request) {
                    PlusRequest plusRequest = (PlusRequest) request;
                    plusRequest.setPrettyPrint(true);
                  }
                }).build();
        // run commands
        listActivities();
        getActivity();
        getProfile();
        // success!
        return;
      } catch (GoogleJsonResponseException e) {
        // message already includes parsed response
        System.err.println(e.getMessage());
      } catch (HttpResponseException e) {
        // message doesn't include parsed response
        System.err.println(e.getMessage());
        System.err.println(e.getResponse().parseAsString());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  /** List the public activities for the authenticated user. */
  private static void listActivities() throws IOException {
    View.header1("Listing My Activities");
    // Fetch the first page of activities
    Plus.Activities.List listActivities = plus.activities().list("me", "public");
    listActivities.setMaxResults(5L);
    // Pro tip: Use partial responses to improve response time considerably
    listActivities.setFields("nextPageToken,items(id,url,object/content)");
    ActivityFeed feed = listActivities.execute();
    // Keep track of the page number in case we're listing activities
    // for a user with thousands of activities. We'll limit ourselves
    // to 5 pages
    int currentPageNumber = 0;
    while (feed.getItems() != null && !feed.getItems().isEmpty() && ++currentPageNumber <= 5) {
      for (Activity activity : feed.getItems()) {
        View.show(activity);
        View.separator();
      }
      // Fetch the next page
      String nextPageToken = feed.getNextPageToken();
      if (nextPageToken == null) {
        break;
      }
      listActivities.setPageToken(nextPageToken);
      View.header2("New page of activities");
      feed = listActivities.execute();
    }
  }

  /** Get an activity for which we already know the ID. */
  private static void getActivity() throws IOException {
    // A known public activity ID
    String activityId = "z12gtjhq3qn2xxl2o224exwiqruvtda0i";
    // We do not need to be authenticated to fetch this activity
    View.header1("Get an explicit public activity by ID");
    Activity activity = plus.activities().get(activityId).execute();
    View.show(activity);
  }

  /** Get the profile for the authenticated user. */
  private static void getProfile() throws IOException {
    View.header1("Get my Google+ profile");
    Person profile = plus.people().get("me").execute();
    View.show(profile);
  }
}
