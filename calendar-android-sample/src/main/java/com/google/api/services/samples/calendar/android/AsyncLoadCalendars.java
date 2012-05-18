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

import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Asynchronously load the calendars with a progress dialog.
 *
 * @author Ravi Mistry
 */
class AsyncLoadCalendars extends AsyncTask<Void, Void, Void> {

  private final CalendarSample calendarSample;
  private final ProgressDialog dialog;
  private com.google.api.services.calendar.Calendar client;

  AsyncLoadCalendars(CalendarSample calendarSample) {
    this.calendarSample = calendarSample;
    client = calendarSample.client;
    dialog = new ProgressDialog(calendarSample);
  }

  @Override
  protected void onPreExecute() {
    dialog.setMessage("Loading calendars...");
    dialog.show();
  }

  @Override
  protected Void doInBackground(Void... arg0) {
    try {
      calendarSample.calendars.clear();
      com.google.api.services.calendar.Calendar.CalendarList.List list =
          client.calendarList().list();
      list.setFields("items");
      CalendarList feed = list.execute();
      if (feed.getItems() != null) {
        for (CalendarListEntry calendar : feed.getItems()) {
          CalendarInfo info = new CalendarInfo(calendar.getId(), calendar.getSummary());
          calendarSample.calendars.add(info);
        }
      }
    } catch (IOException e) {
      calendarSample.handleGoogleException(e);
    } finally {
      calendarSample.onRequestCompleted();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Void result) {
    dialog.dismiss();
    calendarSample.refresh();
  }
}
