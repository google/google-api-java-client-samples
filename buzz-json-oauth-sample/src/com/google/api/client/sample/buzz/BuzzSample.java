/*
 * Copyright (c) 2010 Google Inc.
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

package com.google.api.client.sample.buzz;

import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.buzz.model.BuzzActivity;
import com.google.api.client.sample.buzz.model.BuzzActivityFeed;
import com.google.api.client.sample.buzz.model.BuzzObject;
import com.google.api.client.sample.buzz.model.Debug;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class BuzzSample {

  private final HttpTransport transport;

  public BuzzSample(HttpTransport transport) {
    super();
    this.transport = transport;
  }

  public static void main(String[] args) {
    Debug.enableLogging();
    HttpTransport transport = GoogleTransport.create();
    transport.addParser(new JsonCParser());
    BuzzSample sample = new BuzzSample(transport);
    try {
      try {
        sample.authorize();
        sample.showActivities();
        BuzzActivity activity = sample.insertActivity();
        activity = sample.updateActivity(activity);
        sample.deleteActivity(activity);
        Auth.revoke();
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }
  }

  private void authorize() throws Exception {
    Auth.authorize(transport);
  }

  private void showActivities() throws IOException {
    header("Show Buzz Activities");
    BuzzActivityFeed feed = BuzzActivityFeed.list(transport);
    if (feed.activities != null) {
      int size = feed.activities.size();
      for (int i = 0; i < size; i++) {
        BuzzActivity activity = feed.activities.get(i);
        show(activity);
      }
    }
  }

  private BuzzActivity insertActivity() throws IOException {
    header("Insert Buzz Activity");
    BuzzActivity activity = new BuzzActivity();
    activity.object = new BuzzObject();
    activity.object.content = "Posting using " + Auth.APP_NAME;
    BuzzActivity result = activity.post(transport);
    show(result);
    return result;
  }

  private BuzzActivity updateActivity(BuzzActivity activity)
      throws IOException {
    header("Update Buzz Activity");
    activity.object.content += " (http://bit.ly/9WbLmb)";
    BuzzActivity result = activity.update(transport);
    show(result);
    return result;
  }

  private void deleteActivity(BuzzActivity activity) throws IOException {
    header("Delete Buzz Activity");
    activity.delete(transport);
    System.out.println("Deleted.");
  }

  private static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }

  private static void show(BuzzActivity activity) {
    System.out.println(activity.object.content);
  }
}
