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

package com.google.api.client.sample.calendar.android.model;

import com.google.api.client.googleapis.xml.atom.AtomPatchRelativeToOriginalContent;
import com.google.api.client.googleapis.xml.atom.GoogleAtom;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class CalendarClient {

  /** Whether to enable debugging. */
  public static final boolean DEBUG = true;

  static final XmlNamespaceDictionary DICTIONARY =
      new XmlNamespaceDictionary().set("", "http://www.w3.org/2005/Atom").set(
          "batch", "http://schemas.google.com/gdata/batch").set(
          "gd", "http://schemas.google.com/g/2005");


  private final HttpRequestFactory requestFactory;

  public CalendarClient(HttpRequestFactory requestFactory) {
    this.requestFactory = requestFactory;
  }

  public void initializeParser(HttpRequest request) {
    AtomParser parser = new AtomParser();
    parser.namespaceDictionary = DICTIONARY;
    request.addParser(parser);
  }

  public void executeDelete(Entry entry) throws IOException {
    HttpRequest request = requestFactory.buildDeleteRequest(new GenericUrl(entry.getEditLink()));
    request.execute().ignore();
  }

  Entry executeInsert(Entry entry, CalendarUrl url) throws IOException {
    AtomContent content = new AtomContent();
    content.namespaceDictionary = DICTIONARY;
    content.entry = entry;
    HttpRequest request = requestFactory.buildPostRequest(url, content);
    return request.execute().parseAs(entry.getClass());
  }

  Entry executePatchRelativeToOriginal(Entry updated, Entry original) throws IOException {
    AtomPatchRelativeToOriginalContent content = new AtomPatchRelativeToOriginalContent();
    content.namespaceDictionary = DICTIONARY;
    content.originalEntry = original;
    content.patchedEntry = updated;
    HttpRequest request =
        requestFactory.buildPatchRequest(new GenericUrl(updated.getEditLink()), content);
    return request.execute().parseAs(updated.getClass());
  }

  <F extends Feed> F executeGetFeed(CalendarUrl url, Class<F> feedClass) throws IOException {
    url.fields = GoogleAtom.getFieldsFor(feedClass);
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(feedClass);
  }

  public CalendarEntry executeInsertCalendar(CalendarEntry entry, CalendarUrl url)
      throws IOException {
    return (CalendarEntry) executeInsert(entry, url);
  }

  public CalendarEntry executePatchCalendarRelativeToOriginal(
      CalendarEntry updated, CalendarEntry original) throws IOException {
    return (CalendarEntry) executePatchRelativeToOriginal(updated, original);
  }

  public CalendarFeed executeGetCalendarFeed(CalendarUrl url) throws IOException {
    return executeGetFeed(url, CalendarFeed.class);
  }
}
