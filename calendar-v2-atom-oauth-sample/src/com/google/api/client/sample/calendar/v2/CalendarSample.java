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
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.calendar.v2.model.CalendarEntry;
import com.google.api.client.sample.calendar.v2.model.CalendarFeed;
import com.google.api.client.sample.calendar.v2.model.CalendarUrl;
import com.google.api.client.sample.calendar.v2.model.Debug;
import com.google.api.client.sample.calendar.v2.model.Namespace;
import com.google.api.client.xml.atom.AtomParser;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class CalendarSample {

  private final HttpTransport transport;

  public CalendarSample(HttpTransport transport) {
    super();
    this.transport = transport;
  }

  public static void main(String[] args) {
    Debug.enableLogging();
    try {
      try {
        HttpTransport transport = setUpTransport();
        CalendarSample sample = new CalendarSample(transport);
        sample.authorize();
        sample.showCalendars();
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

  private void showCalendars() throws IOException {
    header("Show Calendars");
    CalendarUrl url = CalendarUrl.forDefaultAllCalendarsFull();
    CalendarFeed feed = CalendarFeed.executeGet(transport, url);
    display(feed);
  }

  private void authorize() throws Exception {
    Auth.authorize(transport);
  }

  private static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }

  private static void display(CalendarFeed feed) {
    for (CalendarEntry cal : feed.calendars) {
      display(cal);
    }
  }

  private static void display(CalendarEntry entry) {
    System.out.println();
    System.out.println("-----------------------------------------------");
    System.out.println("Title: " + entry.title);
    System.out.println("Updated: " + entry.updated);
    if (entry.summary != null) {
      System.out.println("Summary: " + entry.summary);
    }
  }
}
