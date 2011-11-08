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

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarClient;
import com.google.api.services.calendar.CalendarUrl;
import com.google.api.services.calendar.model.BatchOperation;
import com.google.api.services.calendar.model.BatchStatus;
import com.google.api.services.calendar.model.CalendarEntry;
import com.google.api.services.calendar.model.CalendarFeed;
import com.google.api.services.calendar.model.EventEntry;
import com.google.api.services.calendar.model.EventFeed;
import com.google.api.services.calendar.model.When;
import com.google.api.services.samples.shared.cmdline.CmdlineUtils;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;

import java.io.IOException;
import java.util.Date;

/**
 * @author Yaniv Inbar
 */
public class CalendarSample {

  public static void main(String[] args) {
    try {
      GoogleAccessProtectedResource accessProtectedResource =
          OAuth2Native.authorize(new LocalServerReceiver(), null, "google-chrome",
              CalendarUrl.ROOT_URL);
      CalendarClient client =
          new CalendarClient(
              new CalendarCmdlineRequestInitializer(accessProtectedResource).createRequestFactory());
      client.setPrettyPrint(true);
      client.setApplicationName("Google-CalendarSample/1.0");
      try {
        run(client);
      } catch (HttpResponseException e) {
        System.err.println(e.getResponse().parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      try {
        CmdlineUtils.getHttpTransport().shutdown();
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.exit(1);
    }
  }

  public static void run(CalendarClient client) throws IOException {
    showCalendars(client);
    CalendarEntry calendar = addCalendar(client);
    calendar = updateCalendar(client, calendar);
    addEvent(client, calendar);
    batchAddEvents(client, calendar);
    showEvents(client, calendar);
    deleteCalendar(client, calendar);
  }

  private static void showCalendars(CalendarClient client) throws IOException {
    View.header("Show Calendars");
    CalendarUrl url = forAllCalendarsFeed();
    CalendarFeed feed = client.calendarFeed().list().execute(url);
    View.display(feed);
  }

  private static CalendarEntry addCalendar(CalendarClient client) throws IOException {
    View.header("Add Calendar");
    CalendarUrl url = forOwnCalendarsFeed();
    CalendarEntry entry = new CalendarEntry();
    entry.title = "Calendar for Testing";
    CalendarEntry result = client.calendarFeed().insert().execute(url, entry);
    View.display(result);
    return result;
  }

  public static CalendarEntry updateCalendar(CalendarClient client, CalendarEntry calendar)
      throws IOException {
    View.header("Update Calendar");
    CalendarEntry original = calendar.clone();
    calendar.title = "Updated Calendar for Testing";
    CalendarEntry result = client.calendarFeed().patch().execute(calendar, original);
    View.display(result);
    return result;
  }

  private static void addEvent(CalendarClient client, CalendarEntry calendar) throws IOException {
    View.header("Add Event");
    CalendarUrl url = new CalendarUrl(calendar.getEventFeedLink());
    EventEntry event = newEvent();
    EventEntry result = client.eventFeed().insert().execute(url, event);
    View.display(result);
  }

  private static EventEntry newEvent() {
    EventEntry event = new EventEntry();
    event.title = "New Event";
    When when = new When();
    when.startTime = new DateTime(new Date());
    event.when = when;
    return event;
  }

  private static void batchAddEvents(CalendarClient client, CalendarEntry calendar)
      throws IOException {
    View.header("Batch Add Events");
    EventFeed feed = new EventFeed();
    for (int i = 0; i < 3; i++) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
      }
      EventEntry event = newEvent();
      event.batchId = Integer.toString(i);
      event.batchOperation = BatchOperation.INSERT;
      feed.events.add(event);
    }
    // batch link
    CalendarUrl eventFeedUrl = new CalendarUrl(calendar.getEventFeedLink());
    eventFeedUrl.maxResults = 0;
    CalendarUrl batchUrl =
        new CalendarUrl(client.eventFeed().list().execute(eventFeedUrl).getBatchLink());
    EventFeed result = client.eventFeed().batch().execute(feed, batchUrl);
    for (EventEntry event : result.events) {
      BatchStatus batchStatus = event.batchStatus;
      if (batchStatus != null && !HttpStatusCodes.isSuccess(batchStatus.code)) {
        System.err.println("Error posting event: " + batchStatus.reason);
      }
    }
    View.display(result);
  }

  private static void showEvents(CalendarClient client, CalendarEntry calendar) throws IOException {
    View.header("Show Events");
    CalendarUrl url = new CalendarUrl(calendar.getEventFeedLink());
    EventFeed feed = client.eventFeed().list().execute(url);
    View.display(feed);
  }

  public static void deleteCalendar(CalendarClient client, CalendarEntry calendar)
      throws IOException {
    View.header("Delete Calendar");
    client.calendarFeed().delete().execute(calendar);
  }

  private static CalendarUrl forRoot() {
    return new CalendarUrl(CalendarUrl.ROOT_URL);
  }

  private static CalendarUrl forCalendarMetafeed() {
    CalendarUrl result = forRoot();
    result.getPathParts().add("default");
    return result;
  }

  private static CalendarUrl forAllCalendarsFeed() {
    CalendarUrl result = forCalendarMetafeed();
    result.getPathParts().add("allcalendars");
    result.getPathParts().add("full");
    return result;
  }

  private static CalendarUrl forOwnCalendarsFeed() {
    CalendarUrl result = forCalendarMetafeed();
    result.getPathParts().add("owncalendars");
    result.getPathParts().add("full");
    return result;
  }
}
