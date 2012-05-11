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
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Asynchronously load the calendars with a progress dialog.
 *
 * @author Ravi Mistry
 */
class AsyncLoadCalendars extends AsyncTask<Void, Void, List<String>> {

  private final CalendarSample calendarSample;
  private final ProgressDialog dialog;
  private com.google.api.services.calendar.Calendar service;

  AsyncLoadCalendars(CalendarSample calendarSample) {
    this.calendarSample = calendarSample;
    service = calendarSample.client;
    dialog = new ProgressDialog(calendarSample);
  }

  @Override
  protected void onPreExecute() {
    dialog.setMessage("Loading calendars...");
    dialog.show();
  }

  @Override
  protected List<String> doInBackground(Void... arg0) {
    try {
      List<String> calendarNames = new ArrayList<String>();
      calendarSample.calendars.clear();
      CalendarList feed = service.calendarList().list().execute();
      if (feed.getItems() != null) {
        for (CalendarListEntry calendar : feed.getItems()) {
          calendarSample.calendars.add(calendar);
          calendarNames.add(calendar.getSummary());
        }
      }
      return calendarNames;
    } catch (IOException e) {
      calendarSample.handleGoogleException(e);
      return Collections.singletonList(e.getMessage());
    } finally {
      calendarSample.onRequestCompleted();
    }
  }

  @Override
  protected void onPostExecute(List<String> result) {
    dialog.dismiss();
    calendarSample.setListAdapter(
        new ArrayAdapter<String>(calendarSample, android.R.layout.simple_list_item_1, result));
  }
}
