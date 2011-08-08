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

package com.google.api.services.samples.buzz.cmdline;

import com.google.api.services.buzz.model.Activity;
import com.google.api.services.buzz.model.ActivityFeed;
import com.google.api.services.buzz.model.Group;
import com.google.api.services.buzz.model.GroupFeed;


/**
 * @author Yaniv Inbar
 */
class View {

  static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }

  static void display(ActivityFeed feed) {
    if (feed.getItems() != null) {
      for (Activity activity : feed.getItems()) {
        System.out.println();
        System.out.println("-----------------------------------------------");
        display(activity);
      }
    }
  }

  static void display(GroupFeed feed) {
    if (feed.getItems() != null) {
      for (Group group : feed.getItems()) {
        System.out.println();
        System.out.println("-----------------------------------------------");
        display(group);
      }
    }
  }

  static void display(Activity activity) {
    System.out.println("Content: " + activity.getBuzzObject().getContent());
    System.out.println("Updated: " + activity.getUpdated());
  }

  static void display(Group group) {
    System.out.println("Title : " + group.getTitle());
  }
}
