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

import com.google.api.services.buzz.v1.Buzz;
import com.google.api.services.buzz.v1.model.Group;
import com.google.api.services.buzz.v1.model.GroupFeed;

import java.io.IOException;
import java.util.Date;

/**
 * @author Yaniv Inbar
 */
public class GroupActions {
  private static final String FIELDS_GROUP = "title,id";
  private static final String FIELDS_GROUP_FEED = "items(" + FIELDS_GROUP + ")";

  static void showGroups(Buzz buzz) throws IOException {
    View.header("Show Buzz Groups");
    com.google.api.services.buzz.v1.Buzz.Groups.List request = buzz.groups.list("@me");
    request.put("fields", FIELDS_GROUP_FEED);
    GroupFeed feed = request.execute();
    View.display(feed);
  }

  static Group insertGroup(Buzz buzz) throws IOException {
    View.header("Insert Buzz Group");
    Group group = new Group();
    group.title = "Temporary Group (" + new Date() + ")";
    com.google.api.services.buzz.v1.Buzz.Groups.Insert request = buzz.groups.insert("@me", group);
    request.put("fields", FIELDS_GROUP);
    Group result = request.execute();
    View.display(result);
    return result;
  }

  static Group updateGroup(Buzz buzz, Group group) throws IOException {
    View.header("Update Buzz Group");
    group.title += " (updated)";
    com.google.api.services.buzz.v1.Buzz.Groups.Update request =
        buzz.groups.update("@me", group.id, group);
    request.put("fields", FIELDS_GROUP);
    Group result = request.execute();
    View.display(result);
    return result;
  }

  static void deleteGroup(Buzz buzz, Group group) throws IOException {
    View.header("Delete Buzz Group");
    buzz.groups.delete("@me", group.id).execute();
    System.out.println("Deleted.");
  }

}
