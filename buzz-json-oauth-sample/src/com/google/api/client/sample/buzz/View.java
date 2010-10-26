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

import com.google.api.client.sample.buzz.model.BuzzActivity;
import com.google.api.client.sample.buzz.model.BuzzActivityFeed;

/**
 * @author Yaniv Inbar
 */
public class View {

  static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }

  static void display(BuzzActivityFeed feed) {
    for (BuzzActivity activity : feed.items) {
      System.out.println();
      System.out.println("-----------------------------------------------");
      display(activity);
    }
  }

  static void display(BuzzActivity activity) {
    System.out.println("Content: " + activity.object.content);
    System.out.println("Updated: " + activity.updated);
  }
}
