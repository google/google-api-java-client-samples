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

package com.google.api.client.sample.calendar.v2.model;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.Key;
import com.google.api.client.xml.atom.AtomFeedContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Yaniv Inbar
 */
public class EventFeed extends Feed {

  @Key("entry")
  public List<EventEntry> events = new ArrayList<EventEntry>();

  public static EventFeed executeGet(CalendarUrl url) throws IOException {
    return (EventFeed) Feed.executeGet(url, EventFeed.class);
  }

  public EventFeed executeBatch(CalendarEntry calendar) throws IOException {
    // batch link
    CalendarUrl eventFeedUrl = new CalendarUrl(calendar.getEventFeedLink());
    eventFeedUrl.maxResults = 0;
    EventFeed eventFeed = EventFeed.executeGet(eventFeedUrl);
    CalendarUrl url = new CalendarUrl(eventFeed.getBatchLink());
    // execute request
    HttpRequest request = Util.TRANSPORT.buildPostRequest();
    request.url = url;
    AtomFeedContent content = new AtomFeedContent();
    content.namespaceDictionary = Util.DICTIONARY;
    content.feed = this;
    request.content = content;
    return RedirectHandler.execute(request).parseAs(getClass());
  }

  @Override
  public List<EventEntry> getEntries() {
    return events;
  }
}
