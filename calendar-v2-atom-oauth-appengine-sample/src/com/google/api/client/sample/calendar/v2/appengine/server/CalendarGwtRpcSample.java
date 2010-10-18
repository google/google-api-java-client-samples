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

package com.google.api.client.sample.calendar.v2.appengine.server;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.calendar.v2.appengine.client.CalendarService;
import com.google.api.client.sample.calendar.v2.appengine.shared.GwtCalendar;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Calendar GWT RPC service implementation.
 *
 * @author Yaniv Inbar
 */
@SuppressWarnings("serial")
public class CalendarGwtRpcSample extends RemoteServiceServlet implements CalendarService {

  @Override
  public List<GwtCalendar> getCalendars() throws IOException {
    try {
      HttpTransport transport = Auth.setUpTransport();
      CalendarUrl url = CalendarUrl.forOwnCalendarsFeed();
      ArrayList<GwtCalendar> result = new ArrayList<GwtCalendar>();
      // iterate through all pages via next links
      CalendarFeed feed = CalendarFeed.executeGet(transport, url);
      feed.appendCalendars(result);
      while (feed.getNextLink() != null) {
        feed = CalendarFeed.executeGet(transport, new CalendarUrl(feed.getNextLink()));
        feed.appendCalendars(result);
      }
      return result;
    } catch (HttpResponseException e) {
      throw Auth.newIOException(e);
    }
  }

  @Override
  public void delete(GwtCalendar calendar) throws IOException {
    try {
      HttpTransport transport = Auth.setUpTransport();
      CalendarEntry entry = new CalendarEntry(calendar);
      entry.executeDelete(transport);
    } catch (HttpResponseException e) {
      throw Auth.newIOException(e);
    }
  }

  @Override
  public GwtCalendar insert(GwtCalendar calendar) throws IOException {
    try {
      HttpTransport transport = Auth.setUpTransport();
      CalendarEntry entry = new CalendarEntry(calendar);
      CalendarUrl url = CalendarUrl.forOwnCalendarsFeed();
      CalendarEntry responseEntry = entry.executeInsert(transport, url);
      return responseEntry.toCalendar();
    } catch (HttpResponseException e) {
      throw Auth.newIOException(e);
    }
  }

  @Override
  public GwtCalendar update(GwtCalendar original, GwtCalendar updated) throws IOException {
    try {
      HttpTransport transport = Auth.setUpTransport();
      CalendarEntry originalEntry = new CalendarEntry(original);
      CalendarEntry updatedEntry = new CalendarEntry(updated);
      CalendarEntry responseEntry =
          updatedEntry.executePatchRelativeToOriginal(transport, originalEntry);
      return responseEntry.toCalendar();
    } catch (HttpResponseException e) {
      throw Auth.newIOException(e);
    }
  }
}
