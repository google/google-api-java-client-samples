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

import com.google.api.client.googleapis.xml.atom.GData;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.List;

/**
 * Generic Atom Feed.
 *
 * @author Yaniv Inbar
 */
public class Feed {

  @Key("link")
  List<Link> links;

  String getNextLink() {
    return Link.find(links, "next");
  }

  String getBatchLink() {
    return Link.find(links, "http://schemas.google.com/g/2005#batch");
  }

  static Feed executeGet(HttpTransport transport, CalendarUrl url, Class<? extends Feed> feedClass)
      throws IOException {
    url.fields = GData.getFieldsFor(feedClass);
    HttpRequest request = transport.buildGetRequest();
    request.url = url;
    return Auth.execute(request).parseAs(feedClass);
  }
}
