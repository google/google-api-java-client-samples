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

package com.google.api.client.sample.moderator.model;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class SeriesResource extends Resource {

  @Key
  public String name;

  @Key
  public String description;

  @Key
  public Integer numTopics;

  @Key
  public Counters counters;

  @Key
  public Boolean videoSubmissionAllowed;

  public static SeriesResource executeGet(
      HttpTransport transport, String seriesId) throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = ModeratorUrl.forSeries(seriesId);
    return request.execute().parseAs(SeriesResource.class);
  }

  public SeriesResource executeInsert(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildPostRequest();
    request.content = toContent();
    request.url = ModeratorUrl.forSeries();
    return request.execute().parseAs(SeriesResource.class);
  }

  public SeriesResource executeUpdate(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildPutRequest();
    request.content = toContent();
    request.url = ModeratorUrl.forSeries(id.seriesId);
    return request.execute().parseAs(SeriesResource.class);
  }
}
