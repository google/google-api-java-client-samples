/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.calendar.v2;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.calendar.v2.model.BatchOperation;
import com.google.api.client.sample.calendar.v2.model.BatchStatus;
import com.google.api.client.sample.calendar.v2.model.CalendarEntry;
import com.google.api.client.sample.calendar.v2.model.CalendarFeed;
import com.google.api.client.sample.calendar.v2.model.CalendarUrl;
import com.google.api.client.sample.calendar.v2.model.Debug;
import com.google.api.client.sample.calendar.v2.model.EventEntry;
import com.google.api.client.sample.calendar.v2.model.EventFeed;
import com.google.api.client.sample.calendar.v2.model.Namespace;
import com.google.api.client.sample.calendar.v2.model.When;
import com.google.api.client.util.DateTime;
import com.google.api.client.xml.atom.AtomParser;

import java.io.IOException;
import java.util.Date;

/**
 * @author Yaniv Inbar
 */
public class CalendarSample {

  public static void main(String[] args) {
    Debug.enableLogging();
    try {
      try {
        HttpTransport transport = setUpTransport();
        Auth.authorize(transport);
        showCalendars(transport);
        CalendarEntry calendar = addCalendar(transport);
        calendar = updateCalendar(transport, calendar);
        addEvent(transport, calendar);
        batchAddEvents(transport, calendar);
        showEvents(transport, calendar);
        deleteCalendar(transport, calendar);
        Auth.revoke();
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }
  }

  private static HttpTransport setUpTransport() {
    HttpTransport transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("google-calendarsample-1.0");
    headers.gdataVersion = "2";
    AtomParser parser = new AtomParser();
    parser.namespaceDictionary = Namespace.DICTIONARY;
    transport.addParser(parser);
    return transport;
  }

  private static void showCalendars(HttpTransport transport)
      throws IOException {
    View.header("Show Calendars");
    CalendarUrl url = CalendarUrl.forAllCalendarsFeed();
    CalendarFeed feed = CalendarFeed.executeGet(transport, url);
    View.display(feed);
  }

  private static CalendarEntry addCalendar(HttpTransport transport)
      throws IOException {
    View.header("Add Calendar");
    CalendarUrl url = CalendarUrl.forOwnCalendarsFeed();
    CalendarEntry entry = new CalendarEntry();
    entry.title = "Calendar for Testing";
    CalendarEntry result = entry.executeInsert(transport, url);
    View.display(result);
    return result;
  }

  public static CalendarEntry updateCalendar(
      HttpTransport transport, CalendarEntry calendar) throws IOException {
    View.header("Update Calendar");
    CalendarEntry original = calendar.clone();
    calendar.title = "Updated Calendar for Testing";
    CalendarEntry result =
        calendar.executePatchRelativeToOriginal(transport, original);
    View.display(result);
    return result;
  }

  private static void addEvent(HttpTransport transport, CalendarEntry calendar)
      throws IOException {
    View.header("Add Event");
    CalendarUrl url = new CalendarUrl(calendar.getEventFeedLink());
    EventEntry event = newEvent();
    EventEntry result = event.executeInsert(transport, url);
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

  private static void batchAddEvents(
      HttpTransport transport, CalendarEntry calendar) throws IOException {
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
    EventFeed result = feed.executeBatch(transport, calendar);
    for (EventEntry event : result.events) {
      BatchStatus batchStatus = event.batchStatus;
      if (batchStatus != null
          && !HttpResponse.isSuccessStatusCode(batchStatus.code)) {
        System.err.println("Error posting event: " + batchStatus.reason);
      }
    }
    View.display(result);
  }

  private static void showEvents(
      HttpTransport transport, CalendarEntry calendar) throws IOException {
    View.header("Show Events");
    CalendarUrl url = new CalendarUrl(calendar.getEventFeedLink());
    EventFeed feed = EventFeed.executeGet(transport, url);
    View.display(feed);
  }

  public static void deleteCalendar(
      HttpTransport transport, CalendarEntry calendar) throws IOException {
    View.header("Delete Calendar");
    calendar.executeDelete(transport);
  }
}
