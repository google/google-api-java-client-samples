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
import com.google.api.buzz.v1.Buzz.Activities.Insert;
import com.google.api.buzz.v1.Buzz.Activities.List;
import com.google.api.buzz.v1.Buzz.Activities.Update;
import com.google.api.buzz.v1.model.Activity;
import com.google.api.buzz.v1.model.ActivityFeed;
import com.google.api.buzz.v1.model.ActivityObject;
import com.google.api.buzz.v1.model.ActivityVisibility;
import com.google.api.buzz.v1.model.ActivityVisibilityEntries;
import com.google.api.buzz.v1.model.Group;
import com.google.common.collect.Lists;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class ActivityActions {

  private static final String FIELDS_ACTIVITY = "object/content,updated,id";
  private static final String FIELDS_ACTIVITY_FEED = "items(" + FIELDS_ACTIVITY + ")";

  static void showActivitiesForConsumption(Buzz buzz) throws IOException {
    View.header("Show Buzz Activities for Consumption");
    List request = buzz.activities.list("@me", "@consumption");
    request.put("fields", FIELDS_ACTIVITY_FEED);
    ActivityFeed feed = request.execute();
    View.display(feed);
  }

  static void showPersonalActivities(Buzz buzz) throws IOException {
    View.header("Show Buzz Personal Activities");
    List request = buzz.activities.list("@me", "@self");
    request.put("fields", FIELDS_ACTIVITY_FEED);
    ActivityFeed feed = request.execute();
    View.display(feed);
  }

  static Activity insertActivity(Buzz buzz, Group group) throws IOException {
    View.header("Insert Buzz Activity");
    Activity activity = new Activity();
    activity.buzzObject = new ActivityObject();
    activity.buzzObject.content = "Posted using Google API Client Library for Java "
        + "(http://code.google.com/p/google-api-java-client/)";
    activity.visibility = new ActivityVisibility();
    activity.visibility.entries = Lists.newArrayList();
    ActivityVisibilityEntries entry = new ActivityVisibilityEntries();
    entry.id = group.id;
    activity.visibility.entries.add(entry);
    Insert request = buzz.activities.insert("@me", activity);
    request.put("fields", FIELDS_ACTIVITY);
    Activity result = request.execute();
    View.display(result);
    return result;
  }

  static Activity updateActivity(Buzz buzz, Activity activity) throws IOException {
    View.header("Update Buzz Activity");
    activity.buzzObject.content += " (updated)";
    Update request = buzz.activities.update("@me", "@self", activity.id, activity);
    request.put("fields", FIELDS_ACTIVITY);
    Activity result = request.execute();
    View.display(result);
    return result;
  }

  static void deleteActivity(Buzz buzz, Activity activity) throws IOException {
    View.header("Delete Buzz Activity");
    buzz.activities.delete("@me", "@self", activity.id).execute();
    System.out.println("Deleted.");
  }

}
