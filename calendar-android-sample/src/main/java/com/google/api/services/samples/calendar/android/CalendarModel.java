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

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarListEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Thread-safe model for the Google calendars.
 * 
 * @author Yaniv Inbar
 */
class CalendarModel {

  private final Map<String, CalendarInfo> calendars = new HashMap<String, CalendarInfo>();

  int size() {
    synchronized (calendars) {
      return calendars.size();
    }
  }

  void remove(String id) {
    synchronized (calendars) {
      calendars.remove(id);
    }
  }

  CalendarInfo get(String id) {
    synchronized (calendars) {
      return calendars.get(id);
    }
  }

  void add(Calendar calendarToAdd) {
    synchronized (calendars) {
      CalendarInfo found = get(calendarToAdd.getId());
      if (found == null) {
        calendars.put(calendarToAdd.getId(), new CalendarInfo(calendarToAdd));
      } else {
        found.update(calendarToAdd);
      }
    }
  }

  void add(CalendarListEntry calendarToAdd) {
    synchronized (calendars) {
      CalendarInfo found = get(calendarToAdd.getId());
      if (found == null) {
        calendars.put(calendarToAdd.getId(), new CalendarInfo(calendarToAdd));
      } else {
        found.update(calendarToAdd);
      }
    }
  }

  void reset(List<CalendarListEntry> calendarsToAdd) {
    synchronized (calendars) {
      calendars.clear();
      for (CalendarListEntry calendarToAdd : calendarsToAdd) {
        add(calendarToAdd);
      }
    }
  }

  public CalendarInfo[] toSortedArray() {
    synchronized (calendars) {
      List<CalendarInfo> result = new ArrayList<CalendarInfo>();
      for (CalendarInfo calendar : calendars.values()) {
        result.add(calendar.clone());
      }
      Collections.sort(result);
      return result.toArray(new CalendarInfo[0]);
    }
  }
}
