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

package com.google.api.services.samples.calendar.appengine.server;

import com.google.api.client.extensions.auth.helpers.Credential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.calendar.CalendarRequestInitializer;
import com.google.api.services.samples.shared.appengine.AppEngineUtils;

import java.io.IOException;

/**
 * Request initializer for a command-line client application for the Google Calendar Data API that
 * takes care of the "gsessionid".
 * 
 * @author Yaniv Inbar
 */
class CalendarAppEngineRequestInitializer extends CalendarRequestInitializer {

  private final Credential credential;

  CalendarAppEngineRequestInitializer(Credential credential) {
    super(AppEngineUtils.getHttpTransport());
    this.credential = credential;
  }

  @Override
  public void intercept(HttpRequest request) throws IOException {
    super.intercept(request);
    credential.intercept(request);
  }

  @Override
  public boolean handleResponse(HttpRequest request, HttpResponse response, boolean retrySupported)
      throws IOException {
    return super.handleResponse(request, response, retrySupported)
        || credential.handleResponse(request, response, retrySupported);
  }
}
