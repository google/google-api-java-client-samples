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

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.calendar.model.Calendar;

import java.io.IOException;
import java.util.List;

/**
 * Asynchronously insert a new calendar.
 * 
 * @author Yaniv Inbar
 */
class AsyncBatchInsertCalendars extends CalendarAsyncTask {

  private final List<Calendar> calendars;

  AsyncBatchInsertCalendars(CalendarSampleActivity calendarSample, List<Calendar> calendars) {
    super(calendarSample);
    this.calendars = calendars;
  }

  @Override
  protected void doInBackground() throws IOException {
    BatchRequest batch = client.batch();
    for (Calendar calendar : calendars) {
      client.calendars().insert(calendar).setFields(CalendarInfo.FIELDS)
          .queue(batch, new JsonBatchCallback<Calendar>() {

            public void onSuccess(Calendar calendar, HttpHeaders headers) {
              model.add(calendar);
            }

            @Override
            public void onFailure(GoogleJsonError err, HttpHeaders headers) throws IOException {
              Utils.logAndShowError(activity, CalendarSampleActivity.TAG, err.getMessage());
            }
          });
    }
    batch.execute();
  }
}
