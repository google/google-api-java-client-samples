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

package com.google.api.client.sample.calendar.v2.model;

import com.google.api.client.googleapis.GoogleUrl;

/**
 * @author Yaniv Inbar
 */
public class CalendarUrl extends GoogleUrl {

  public static final String ROOT_URL = "https://www.google.com/calendar/feeds";

  public CalendarUrl(String url) {
    super(url);
    if (Debug.ENABLED) {
      this.prettyprint = true;
    }
  }

  private static CalendarUrl forRoot() {
    return new CalendarUrl(ROOT_URL);
  }

  public static CalendarUrl forDefault() {
    CalendarUrl result = forRoot();
    result.pathParts.add("default");
    return result;
  }

  public static CalendarUrl forDefaultAllCalendarsFull() {
    CalendarUrl result = forRoot();
    result.pathParts.add("default");
    result.pathParts.add("allcalendars");
    result.pathParts.add("full");
    return result;
  }
}
