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

package com.google.api.services.samples.calendar.appengine.server;

import com.google.api.client.http.HttpResponseException;
import com.google.api.services.calendar.CalendarClient;
import com.google.api.services.calendar.CalendarUrl;
import com.google.api.services.calendar.model.CalendarEntry;
import com.google.api.services.calendar.model.CalendarFeed;
import com.google.api.services.calendar.model.Link;
import com.google.api.services.samples.calendar.appengine.client.CalendarService;
import com.google.api.services.samples.calendar.appengine.shared.GwtCalendar;
import com.google.common.base.Preconditions;
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

  CalendarClient client;

  @Override
  public List<GwtCalendar> getCalendars() throws IOException {
    try {
      CalendarUrl url = forOwnCalendarsFeed();
      ArrayList<GwtCalendar> result = new ArrayList<GwtCalendar>();
      // iterate through all pages via next links
      CalendarClient client = Utils.loadCalendarClient();
      CalendarFeed feed = client.calendarFeed().get().execute(url);
      appendCalendars(feed, result);
      while (feed.getNextLink() != null) {
        feed = client.calendarFeed().get().execute(new CalendarUrl(feed.getNextLink()));
        appendCalendars(feed, result);
      }
      return result;
    } catch (HttpResponseException e) {
      throw Utils.newIOException(e);
    }
  }

  @Override
  public void delete(GwtCalendar calendar) throws IOException {
    try {
      CalendarEntry entry = toCalendarEntry(calendar);
      CalendarClient client = Utils.loadCalendarClient();
      client.calendarFeed().delete().execute(entry);
    } catch (HttpResponseException e) {
      throw Utils.newIOException(e);
    }
  }

  @Override
  public GwtCalendar insert(GwtCalendar calendar) throws IOException {
    try {
      CalendarEntry entry = toCalendarEntry(calendar);
      CalendarUrl url = forOwnCalendarsFeed();
      CalendarClient client = Utils.loadCalendarClient();
      CalendarEntry responseEntry = client.calendarFeed().insert().execute(url, entry);
      return toCalendar(responseEntry);
    } catch (HttpResponseException e) {
      throw Utils.newIOException(e);
    }
  }

  @Override
  public GwtCalendar update(GwtCalendar original, GwtCalendar updated) throws IOException {
    try {
      CalendarClient client = Utils.loadCalendarClient();
      CalendarEntry originalEntry = toCalendarEntry(original);
      CalendarEntry updatedEntry = toCalendarEntry(updated);
      CalendarEntry responseEntry =
          client.calendarFeed().patch().execute(originalEntry, updatedEntry);
      return toCalendar(responseEntry);
    } catch (HttpResponseException e) {
      throw Utils.newIOException(e);
    }
  }

  private static CalendarUrl forRoot() {
    return new CalendarUrl(CalendarUrl.ROOT_URL);
  }

  private static CalendarUrl forCalendarMetafeed() {
    CalendarUrl result = forRoot();
    result.getPathParts().add("default");
    return result;
  }

  private static CalendarUrl forOwnCalendarsFeed() {
    CalendarUrl result = forCalendarMetafeed();
    result.getPathParts().add("owncalendars");
    result.getPathParts().add("full");
    return result;
  }

  void appendCalendars(CalendarFeed feed, List<GwtCalendar> result) {
    for (CalendarEntry entry : feed.calendars) {
      result.add(toCalendar(entry));
    }
  }

  CalendarEntry toCalendarEntry(GwtCalendar calendar) {
    Preconditions.checkNotNull(calendar);
    CalendarEntry entry = new CalendarEntry();
    entry.links = new ArrayList<Link>();
    entry.links.add(Link.editLink(calendar.editLink));
    entry.title = calendar.title;
    entry.updated = calendar.updated;
    return entry;
  }

  GwtCalendar toCalendar(CalendarEntry entry) {
    GwtCalendar result = new GwtCalendar();
    result.editLink = entry.getEditLink();
    result.title = entry.title;
    result.updated = entry.updated;
    return result;
  }
}
