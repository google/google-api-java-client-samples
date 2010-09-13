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

package com.google.api.client.sample.calendar.android.model;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;

/**
 * @author Yaniv Inbar
 */
public class CalendarUrl extends GoogleUrl {

  public static final String ROOT_URL = "https://www.google.com/calendar/feeds";

  @Key("max-results")
  public Integer maxResults;

  public CalendarUrl(String url) {
    super(url);
    if (Debug.ENABLED) {
      this.prettyprint = true;
    }
  }

  private static CalendarUrl forRoot() {
    return new CalendarUrl(ROOT_URL);
  }

  public static CalendarUrl forCalendarMetafeed() {
    CalendarUrl result = forRoot();
    result.pathParts.add("default");
    return result;
  }

  public static CalendarUrl forAllCalendarsFeed() {
    CalendarUrl result = forCalendarMetafeed();
    result.pathParts.add("allcalendars");
    result.pathParts.add("full");
    return result;
  }

  public static CalendarUrl forOwnCalendarsFeed() {
    CalendarUrl result = forCalendarMetafeed();
    result.pathParts.add("owncalendars");
    result.pathParts.add("full");
    return result;
  }

  public static CalendarUrl forEventFeed(
      String userId, String visibility, String projection) {
    CalendarUrl result = forRoot();
    result.pathParts.add(userId);
    result.pathParts.add(visibility);
    result.pathParts.add(projection);
    return result;
  }

  public static CalendarUrl forDefaultPrivateFullEventFeed() {
    return forEventFeed("default", "private", "full");
  }
}
