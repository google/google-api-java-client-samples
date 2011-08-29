/*
 * Copyright (c) 2011 Google Inc.
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

package com.google.api.services.calendar;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.services.calendar.model.CalendarEntry;
import com.google.api.services.calendar.model.CalendarFeed;
import com.google.api.services.calendar.model.Entry;
import com.google.api.services.calendar.model.EventEntry;
import com.google.api.services.calendar.model.EventFeed;
import com.google.api.services.calendar.model.Feed;
import com.google.api.services.samples.shared.gdata.xml.GDataXmlClient;

import java.io.IOException;

/**
 * Client for Google Calendar Data API.
 *
 * @author Yaniv Inbar
 */
public class CalendarClient extends GDataXmlClient {

  static final XmlNamespaceDictionary DICTIONARY =
      new XmlNamespaceDictionary().set("", "http://www.w3.org/2005/Atom").set(
          "batch", "http://schemas.google.com/gdata/batch").set(
          "gd", "http://schemas.google.com/g/2005");

  public CalendarClient(HttpRequestFactory requestFactory) {
    super("2", requestFactory, DICTIONARY);
  }

  public void executeDelete(Entry entry) throws IOException {
    CalendarUrl url = new CalendarUrl(entry.getEditLink());
    super.executeDelete(url, null);
  }

  <T> T executeGet(CalendarUrl url, Class<T> parseAsType) throws IOException {
    return super.executeGet(url, parseAsType);
  }

  public <T extends Entry> T executePatchRelativeToOriginal(T original, T updated)
      throws IOException {
    CalendarUrl url = new CalendarUrl(updated.getEditLink());
    return super.executePatchRelativeToOriginal(url, original, updated, null);
  }

  <T> T executePost(CalendarUrl url, T content) throws IOException {
    return super.executePost(url, content instanceof Feed, content);
  }

  public EventCollection eventFeed() {
    return new EventCollection();
  }

  /** Event collection. */
  public class EventCollection {

    public ListRequest list() {
      return new ListRequest();
    }

    /** List request. */
    public class ListRequest {

      public EventFeed execute(CalendarUrl url) throws IOException {
        return executeGet(url, EventFeed.class);
      }
    }

    public BatchRequest batch() {
      return new BatchRequest();
    }

    /** Batch request. */
    public class BatchRequest {
      public EventFeed execute(EventFeed eventFeed, CalendarUrl batchUrl) throws IOException {
        return executePost(batchUrl, eventFeed);
      }
    }

    public InsertRequest insert() {
      return new InsertRequest();
    }

    /** Insert request. */
    public class InsertRequest {
      public EventEntry execute(CalendarUrl url, EventEntry entry) throws IOException {
        return executePost(url, entry);
      }
    }

    public DeleteRequest delete() {
      return new DeleteRequest();
    }

    /** Delete request. */
    public class DeleteRequest {
      public void execute(EventEntry entry) throws IOException {
        executeDelete(entry);
      }
    }
  }

  public CalendarCollection calendarFeed() {
    return new CalendarCollection();
  }

  /** Calendar collection. */
  public class CalendarCollection {

    public ListRequest list() {
      return new ListRequest();
    }

    /** List request. */
    public class ListRequest {

      public CalendarFeed execute(CalendarUrl url) throws IOException {
        return executeGet(url, CalendarFeed.class);
      }
    }

    public InsertRequest insert() {
      return new InsertRequest();
    }

    /** Insert request. */
    public class InsertRequest {
      public CalendarEntry execute(CalendarUrl url, CalendarEntry entry) throws IOException {
        return executePost(url, entry);
      }
    }

    public PatchRequest patch() {
      return new PatchRequest();
    }

    /** Patch request. */
    public class PatchRequest {
      public CalendarEntry execute(CalendarEntry original, CalendarEntry updated)
          throws IOException {
        return executePatchRelativeToOriginal(original, updated);
      }
    }

    public DeleteRequest delete() {
      return new DeleteRequest();
    }

    /** Delete request. */
    public class DeleteRequest {
      public void execute(CalendarEntry entry) throws IOException {
        executeDelete(entry);
      }
    }
  }
}
