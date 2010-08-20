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

package com.google.api.client.sample.latitude.model;

import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.List;

/**
 * @author Yaniv Inbar
 */
public class LocationResource {

  @Key
  public Long timestampMs;

  @Key
  public Float latitude;

  @Key
  public Float longitude;

  @Key
  public Integer accuracy;

  @Key
  public Integer speed;

  @Key
  public Integer heading;

  @Key
  public Integer altitude;

  @Key
  public Integer altitudeAccuracy;

  @Key
  public String placeid;

  public static List<LocationResource> executeList(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = LatitudeUrl.forLocation();
    return request.execute().parseAs(LocationList.class).items;
  }

  public static LocationResource executeGet(
      HttpTransport transport, Long timestampMs) throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = LatitudeUrl.forLocation(timestampMs);
    return request.execute().parseAs(LocationResource.class);
  }

  public LocationResource executeInsert(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildPostRequest();
    request.content = toContent();
    request.url = LatitudeUrl.forLocation();
    return request.execute().parseAs(LocationResource.class);
  }

  public static LocationResource executeGetCurrentLocation(
      HttpTransport transport) throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = LatitudeUrl.forCurrentLocation();
    return request.execute().parseAs(LocationResource.class);
  }

  public LocationResource executeUpdateCurrent(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildPutRequest();
    request.content = toContent();
    request.url = LatitudeUrl.forCurrentLocation();
    return request.execute().parseAs(LocationResource.class);
  }

  public static void executeDelete(HttpTransport transport, Long timestampMs)
      throws IOException {
    HttpRequest request = transport.buildDeleteRequest();
    request.url = LatitudeUrl.forLocation(timestampMs);
    request.execute().ignore();
  }

  public static void executeDeleteCurrent(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildDeleteRequest();
    request.url = LatitudeUrl.forCurrentLocation();
    request.execute().ignore();
  }

  JsonCContent toContent() {
    JsonCContent content = new JsonCContent();
    content.data = this;
    return content;
  }
}
