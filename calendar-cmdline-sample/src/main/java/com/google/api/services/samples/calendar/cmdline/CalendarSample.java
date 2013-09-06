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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.OAuthApplicationContext;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.jackson2.auth.oauth2.GoogleOAuthInstalledAppContext;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Yaniv Inbar
 */
public class CalendarSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "calendar_sample";

  private static com.google.api.services.calendar.Calendar client;

  static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();

  static OAuthApplicationContext context = new GoogleOAuthInstalledAppContext(
      "/client_secrets.json", Collections.singleton(CalendarScopes.CALENDAR), APPLICATION_NAME);

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    return new AuthorizationCodeInstalledApp(context.getFlow(), new LocalServerReceiver())
        .authorize("user");
  }

  public static void main(String[] args) {
    try {
      // authorization
      Credential credential = authorize();

      // set up global Calendar instance
      client = new com.google.api.services.calendar.Calendar.Builder(
          context.getTransport(), context.getJsonFactory(), credential).setApplicationName(
          APPLICATION_NAME).build();

      // run commands
      showCalendars();
      addCalendarsUsingBatch();
      Calendar calendar = addCalendar();
      updateCalendar(calendar);
      addEvent(calendar);
      showEvents(calendar);
      deleteCalendarsUsingBatch();
      deleteCalendar(calendar);

    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static void showCalendars() throws IOException {
    View.header("Show Calendars");
    CalendarList feed = client.calendarList().list().execute();
    View.display(feed);
  }

  private static void addCalendarsUsingBatch() throws IOException {
    View.header("Add Calendars using Batch");
    BatchRequest batch = client.batch();

    // Create the callback.
    JsonBatchCallback<Calendar> callback = new JsonBatchCallback<Calendar>() {

        @Override
      public void onSuccess(Calendar calendar, HttpHeaders responseHeaders) {
        View.display(calendar);
        addedCalendarsUsingBatch.add(calendar);
      }

        @Override
      public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
        System.out.println("Error Message: " + e.getMessage());
      }
    };

    // Create 2 Calendar Entries to insert.
    Calendar entry1 = new Calendar().setSummary("Calendar for Testing 1");
    client.calendars().insert(entry1).queue(batch, callback);

    Calendar entry2 = new Calendar().setSummary("Calendar for Testing 2");
    client.calendars().insert(entry2).queue(batch, callback);

    batch.execute();
  }

  private static Calendar addCalendar() throws IOException {
    View.header("Add Calendar");
    Calendar entry = new Calendar();
    entry.setSummary("Calendar for Testing 3");
    Calendar result = client.calendars().insert(entry).execute();
    View.display(result);
    return result;
  }

  private static Calendar updateCalendar(Calendar calendar) throws IOException {
    View.header("Update Calendar");
    Calendar entry = new Calendar();
    entry.setSummary("Updated Calendar for Testing");
    Calendar result = client.calendars().patch(calendar.getId(), entry).execute();
    View.display(result);
    return result;
  }


  private static void addEvent(Calendar calendar) throws IOException {
    View.header("Add Event");
    Event event = newEvent();
    Event result = client.events().insert(calendar.getId(), event).execute();
    View.display(result);
  }

  private static Event newEvent() {
    Event event = new Event();
    event.setSummary("New Event");
    Date startDate = new Date();
    Date endDate = new Date(startDate.getTime() + 3600000);
    DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
    event.setStart(new EventDateTime().setDateTime(start));
    DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
    event.setEnd(new EventDateTime().setDateTime(end));
    return event;
  }

  private static void showEvents(Calendar calendar) throws IOException {
    View.header("Show Events");
    Events feed = client.events().list(calendar.getId()).execute();
    View.display(feed);
  }

  private static void deleteCalendarsUsingBatch() throws IOException {
    View.header("Delete Calendars Using Batch");
    BatchRequest batch = client.batch();
    for (Calendar calendar : addedCalendarsUsingBatch) {
      client.calendars().delete(calendar.getId()).queue(batch, new JsonBatchCallback<Void>() {

          @Override
        public void onSuccess(Void content, HttpHeaders responseHeaders) {
          System.out.println("Delete is successful!");
        }

          @Override
        public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
          System.out.println("Error Message: " + e.getMessage());
        }
      });
    }

    batch.execute();
  }

  private static void deleteCalendar(Calendar calendar) throws IOException {
    View.header("Delete Calendar");
    client.calendars().delete(calendar.getId()).execute();
  }
}
