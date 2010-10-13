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

import com.google.api.client.http.HttpTransport;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.client.sample.calendar.v2.appengine.shared.GwtCalendar;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Calendar entry.
 *
 * @author Yaniv Inbar
 */
public class CalendarEntry extends Entry {

  public CalendarEntry() {
  }

  CalendarEntry(GwtCalendar calendar) {
    Preconditions.checkNotNull(calendar);
    links = new ArrayList<Link>();
    links.add(Link.editLink(calendar.editLink));
    title = calendar.title;
    updated = calendar.updated;
  }

  GwtCalendar toCalendar() {
    GwtCalendar result = new GwtCalendar();
    result.editLink = getEditLink();
    result.title = title;
    result.updated = updated;
    return result;
  }

  String getEventFeedLink() {
    return Link.find(links, "http://schemas.google.com/gCal/2005#eventFeed");
  }

  @Override
  protected CalendarEntry clone() {
    return (CalendarEntry) super.clone();
  }

  @Override
  CalendarEntry executeInsert(HttpTransport transport, CalendarUrl url) throws IOException {
    return (CalendarEntry) super.executeInsert(transport, url);
  }

  CalendarEntry executePatchRelativeToOriginal(HttpTransport transport, CalendarEntry original)
      throws IOException {
    return (CalendarEntry) super.executePatchRelativeToOriginal(transport, original);
  }
}
