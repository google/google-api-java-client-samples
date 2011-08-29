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

package com.google.api.services.samples.calendar.cmdline;

import com.google.api.services.calendar.model.Entry;
import com.google.api.services.calendar.model.EventEntry;
import com.google.api.services.calendar.model.Feed;


/**
 * @author Yaniv Inbar
 */
public class View {

  static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }

  static void display(Feed feed) {
    for (Entry entry : feed.getEntries()) {
      System.out.println();
      System.out.println("-----------------------------------------------");
      display(entry);
    }
  }

  static void display(Entry entry) {
    System.out.println("Title: " + entry.title);
    System.out.println("Updated: " + entry.updated);
    if (entry.summary != null) {
      System.out.println("Summary: " + entry.summary);
    }
    if (entry instanceof EventEntry) {
      EventEntry event = (EventEntry) entry;
      if (event.when != null) {
        if (event.when.startTime != null) {
          System.out.println("Start Time: " + event.when.startTime);
        }
        if (event.when.endTime != null) {
          System.out.println("End Time: " + event.when.startTime);
        }
      }
    }
  }

}
