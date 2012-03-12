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

package com.google.api.services.samples.calendar.appengine.server;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AppEngineCredentialStore;
import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for JDO persistence, OAuth flow helpers, and others.
 * 
 * @author Yaniv Inbar
 */
class Utils {

  /** Global instance of the HTTP transport. */
  static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();

  /** Global instance of the JSON factory. */
  static final JsonFactory JSON_FACTORY = new JacksonFactory();

  private static final String RESOURCE_LOCATION = "/client_secrets.json";

  private static GoogleClientSecrets clientSecrets = null;

  static GoogleClientSecrets getClientCredential() throws IOException {
    if (clientSecrets == null) {
      InputStream inputStream = Utils.class.getResourceAsStream(RESOURCE_LOCATION);
      Preconditions.checkNotNull(inputStream, "missing resource %s", RESOURCE_LOCATION);
      clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, inputStream);
      Preconditions.checkArgument(!clientSecrets.getDetails().getClientId().startsWith("[[")
          && !clientSecrets.getDetails().getClientSecret().startsWith("[["),
          "Please enter your client ID and secret from the Google APIs Console in %s from the "
          + "root samples directory", RESOURCE_LOCATION);
    }
    return clientSecrets;
  }

  static String getRedirectUri(HttpServletRequest req) {
    GenericUrl url = new GenericUrl(req.getRequestURL().toString());
    url.setRawPath("/oauth2callback");
    return url.build();
  }

  static GoogleAuthorizationCodeFlow newFlow() throws IOException {
    return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
        getClientCredential(), Collections.singleton(CalendarScopes.CALENDAR)).setCredentialStore(
        new AppEngineCredentialStore()).build();
  }

  static Calendar loadCalendarClient() throws IOException {
    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
    Credential credential = newFlow().loadCredential(userId);
    return Calendar.builder(HTTP_TRANSPORT, JSON_FACTORY)
        .setHttpRequestInitializer(credential).build();
  }

  /**
   * Returns an {@link IOException} (but not a subclass) in order to work around restrictive GWT
   * serialization policy.
   */
  static IOException wrappedIOException(IOException e) {
    if (e.getClass() == IOException.class) {
      return e;
    }
    return new IOException(e.getMessage());
  }

  private Utils() {
  }
}
