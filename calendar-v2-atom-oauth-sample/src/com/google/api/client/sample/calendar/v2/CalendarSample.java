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

package com.google.api.client.sample.calendar.v2;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.sample.calendar.v2.model.BatchOperation;
import com.google.api.client.sample.calendar.v2.model.BatchStatus;
import com.google.api.client.sample.calendar.v2.model.CalendarClient;
import com.google.api.client.sample.calendar.v2.model.CalendarEntry;
import com.google.api.client.sample.calendar.v2.model.CalendarFeed;
import com.google.api.client.sample.calendar.v2.model.CalendarUrl;
import com.google.api.client.sample.calendar.v2.model.EventEntry;
import com.google.api.client.sample.calendar.v2.model.EventFeed;
import com.google.api.client.sample.calendar.v2.model.Util;
import com.google.api.client.sample.calendar.v2.model.When;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.util.Date;

/**
 * @author Yaniv Inbar
 */
public class CalendarSample {

  public static void main(String[] args) {
    Util.enableLogging();
    CalendarClient client = new CalendarClient(
        ClientCredentials.ENTER_OAUTH_CONSUMER_KEY, ClientCredentials.ENTER_OAUTH_CONSUMER_SECRET);
    try {
      try {
        client.authorize();
        showCalendars(client);
        CalendarEntry calendar = addCalendar(client);
        calendar = updateCalendar(client, calendar);
        addEvent(client, calendar);
        batchAddEvents(client, calendar);
        showEvents(client, calendar);
        deleteCalendar(client, calendar);
        shutdown(client);
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      shutdown(client);
      System.exit(1);
    }
  }

  private static void shutdown(CalendarClient client) {
    try {
      client.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void showCalendars(CalendarClient client) throws IOException {
    View.header("Show Calendars");
    CalendarUrl url = CalendarUrl.forAllCalendarsFeed();
    CalendarFeed feed = client.executeGetCalendarFeed(url);
    View.display(feed);
  }

  private static CalendarEntry addCalendar(CalendarClient client) throws IOException {
    View.header("Add Calendar");
    CalendarUrl url = CalendarUrl.forOwnCalendarsFeed();
    CalendarEntry entry = new CalendarEntry();
    entry.title = "Calendar for Testing";
    CalendarEntry result = client.executeInsertCalendar(entry, url);
    View.display(result);
    return result;
  }

  public static CalendarEntry updateCalendar(CalendarClient client, CalendarEntry calendar)
      throws IOException {
    View.header("Update Calendar");
    CalendarEntry original = calendar.clone();
    calendar.title = "Updated Calendar for Testing";
    CalendarEntry result = client.executePatchCalendarRelativeToOriginal(calendar, original);
    View.display(result);
    return result;
  }

  private static void addEvent(CalendarClient client, CalendarEntry calendar) throws IOException {
    View.header("Add Event");
    CalendarUrl url = new CalendarUrl(calendar.getEventFeedLink());
    EventEntry event = newEvent();
    EventEntry result = client.executeInsertEvent(event, url);
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
    EventFeed result = client.executeBatchEventFeed(feed, calendar);
    for (EventEntry event : result.events) {
      BatchStatus batchStatus = event.batchStatus;
      if (batchStatus != null && !HttpResponse.isSuccessStatusCode(batchStatus.code)) {
        System.err.println("Error posting event: " + batchStatus.reason);
      }
    }
    View.display(result);
  }

  private static void showEvents(CalendarClient client, CalendarEntry calendar) throws IOException {
    View.header("Show Events");
    CalendarUrl url = new CalendarUrl(calendar.getEventFeedLink());
    EventFeed feed = client.executeGetEventFeed(url);
    View.display(feed);
  }

  public static void deleteCalendar(CalendarClient client, CalendarEntry calendar)
      throws IOException {
    View.header("Delete Calendar");
    client.executeDelete(calendar);
  }
}
