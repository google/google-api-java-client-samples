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

package com.google.api.services.samples.calendar.cmdline;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.calendar.CalendarRequestInitializer;

import java.io.IOException;

/**
 * Request initializer for a command-line client application for the Google Calendar Data API that
 * takes care of the "gsessionid".
 *
 * @author Yaniv Inbar
 */
public class CalendarCmdlineRequestInitializer extends CalendarRequestInitializer {

  private final GoogleAccessProtectedResource accessProtectedResource;

  public CalendarCmdlineRequestInitializer(GoogleAccessProtectedResource accessProtectedResource) {
    super(accessProtectedResource.getTransport());
    this.accessProtectedResource = accessProtectedResource;
  }

  @Override
  public void intercept(HttpRequest request) throws IOException {
    super.intercept(request);
    accessProtectedResource.intercept(request);
  }

  @Override
  public boolean handleResponse(HttpRequest request, HttpResponse response, boolean retrySupported)
      throws IOException {
    return super.handleResponse(request, response, retrySupported)
        || accessProtectedResource.handleResponse(request, response, retrySupported);
  }
}
