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

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.samples.calendar.appengine.client.CalendarService;
import com.google.api.services.samples.calendar.appengine.shared.GwtCalendar;
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
      com.google.api.services.calendar.Calendar client = Utils.loadCalendarClient();
      com.google.api.services.calendar.Calendar.CalendarList.List listRequest =
          client.calendarList().list();
      listRequest.setFields("items(id,summary)");
      CalendarList feed = listRequest.execute();
      ArrayList<GwtCalendar> result = new ArrayList<GwtCalendar>();
      if (feed.getItems() != null) {
        for (CalendarListEntry entry : feed.getItems()) {
          result.add(new GwtCalendar(entry.getId(), entry.getSummary()));
        }
      }
      return result;
    } catch (IOException e) {
      throw Utils.wrappedIOException(e);
    }
  }

  @Override
  public void delete(GwtCalendar calendar) throws IOException {
    try {
      com.google.api.services.calendar.Calendar client = Utils.loadCalendarClient();
      client.calendars().delete(calendar.id).execute();
    } catch (IOException e) {
      throw Utils.wrappedIOException(e);
    }
  }

  @Override
  public GwtCalendar insert(GwtCalendar calendar) throws IOException {
    try {
      Calendar newCalendar = new Calendar().setSummary(calendar.title);
      com.google.api.services.calendar.Calendar client = Utils.loadCalendarClient();
      Calendar responseEntry = client.calendars().insert(newCalendar).execute();
      GwtCalendar result = new GwtCalendar();
      result.title = responseEntry.getSummary();
      result.id = responseEntry.getId();
      return result;
    } catch (IOException e) {
      throw Utils.wrappedIOException(e);
    }
  }

  @Override
  public GwtCalendar update(GwtCalendar updated) throws IOException {
    try {
      com.google.api.services.calendar.Calendar client = Utils.loadCalendarClient();
      Calendar entry = new Calendar();
      entry.setSummary(updated.title);
      String id = updated.id;
      Calendar responseEntry = client.calendars().patch(id, entry).execute();
      return new GwtCalendar(id, responseEntry.getSummary());
    } catch (IOException e) {
      throw Utils.wrappedIOException(e);
    }
  }
}
