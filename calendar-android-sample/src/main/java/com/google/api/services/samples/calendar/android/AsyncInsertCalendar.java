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

import com.google.api.services.calendar.Calendar.Calendars.Insert;
import com.google.api.services.calendar.model.Calendar;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.IOException;

/**
 * Asynchronously insert a new calendar with a progress dialog.
 *
 * @author Ravi Mistry
 */
class AsyncInsertCalendar extends AsyncTask<Void, Void, Void> {

  private final CalendarSample calendarSample;
  private final ProgressDialog dialog;
  private final Calendar entry;
  private com.google.api.services.calendar.Calendar client;

  AsyncInsertCalendar(CalendarSample calendarSample, Calendar entry) {
    this.calendarSample = calendarSample;
    client = calendarSample.client;
    this.entry = entry;
    dialog = new ProgressDialog(calendarSample);
  }

  @Override
  protected void onPreExecute() {
    dialog.setMessage("Inserting calendar...");
    dialog.show();
  }

  @Override
  protected Void doInBackground(Void... arg0) {
    try {
      Insert insert = client.calendars().insert(entry);
      insert.setFields("id,summary");
      Calendar calendar = insert.execute();
      CalendarInfo info = new CalendarInfo(calendar.getId(), calendar.getSummary());
      calendarSample.calendars.add(info);
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
