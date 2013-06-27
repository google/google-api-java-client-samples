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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.calendar.model.Calendar;

import java.io.IOException;

/**
 * Asynchronously updates a calendar with a progress dialog.
 * 
 * @author Yaniv Inbar
 */
class AsyncUpdateCalendar extends CalendarAsyncTask {

  private final String calendarId;
  private final Calendar entry;

  AsyncUpdateCalendar(CalendarSampleActivity calendarSample, String calendarId, Calendar entry) {
    super(calendarSample);
    this.calendarId = calendarId;
    this.entry = entry;
  }

  @Override
  protected void doInBackground() throws IOException {
    try {
      Calendar updatedCalendar =
          client.calendars().patch(calendarId, entry).setFields(CalendarInfo.FIELDS).execute();
      model.add(updatedCalendar);
    } catch (GoogleJsonResponseException e) {
      // 404 Not Found would happen if user tries to delete an already deleted calendar
      if (e.getStatusCode() != 404) {
        throw e;
      }
      model.remove(calendarId);
    }
  }
}
