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

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpUnsuccessfulResponseHandler;

import java.io.IOException;

/**
 * Request initializer for the Google Calendar Data API that takes care of the "gsessionid".
 *
 * @author Yaniv Inbar
 */
public class CalendarRequestInitializer
    implements HttpRequestInitializer, HttpExecuteInterceptor, HttpUnsuccessfulResponseHandler {

  private String gsessionid;
  private final HttpTransport transport;

  public CalendarRequestInitializer(HttpTransport transport) {
    this.transport = transport;
  }

  public final HttpRequestFactory createRequestFactory() {
    return transport.createRequestFactory(this);
  }

  @Override
  public void initialize(HttpRequest request) throws IOException {
    request.setInterceptor(this);
    request.setUnsuccessfulResponseHandler(this);
  }

  @Override
  public void intercept(HttpRequest request) throws IOException {
    request.getUrl().set("gsessionid", gsessionid);
  }

  public final String getGsessionid() {
    return gsessionid;
  }

  public final void setGsessionid(String gsessionid) {
    this.gsessionid = gsessionid;
  }

  @Override
  public boolean handleResponse(HttpRequest request, HttpResponse response, boolean retrySupported)
      throws IOException {
    if (response.getStatusCode() == 302) {
      GoogleUrl url = new GoogleUrl(response.getHeaders().getLocation());
      gsessionid = (String) url.getFirst("gsessionid");
      return true;
    }
    return false;
  }
}
