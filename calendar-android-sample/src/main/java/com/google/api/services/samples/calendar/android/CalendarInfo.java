/*
 * Copyright (c) 2012 Google Inc.
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

package com.google.api.services.samples.calendar.android;

import com.google.api.client.util.Objects;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;

/**
 * Class that holds information about a calendar.
 * 
 * @author Yaniv Inbar
 */
class CalendarInfo implements Comparable<CalendarInfo>, Cloneable {

  static final String FIELDS = "id,summary";
  static final String FEED_FIELDS = "items(" + FIELDS + ")";

  String id;
  String summary;

  CalendarInfo(String id, String summary) {
    this.id = id;
    this.summary = summary;
  }

  CalendarInfo(Calendar calendar) {
    update(calendar);
  }

  CalendarInfo(CalendarListEntry calendar) {
    update(calendar);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(CalendarInfo.class).add("id", id).add("summary", summary)
        .toString();
  }

  public int compareTo(CalendarInfo other) {
    return summary.compareTo(other.summary);
  }

  @Override
  public CalendarInfo clone() {
    try {
      return (CalendarInfo) super.clone();
    } catch (CloneNotSupportedException exception) {
      // should not happen
      throw new RuntimeException(exception);
    }
  }

  void update(Calendar calendar) {
    id = calendar.getId();
    summary = calendar.getSummary();
  }

  void update(CalendarListEntry calendar) {
    id = calendar.getId();
    summary = calendar.getSummary();
  }
}
