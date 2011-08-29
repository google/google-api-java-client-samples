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

import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.extensions.auth.helpers.Credential;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth2.draft10.GoogleOAuth2ThreeLeggedFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.CalendarClient;
import com.google.api.services.calendar.CalendarUrl;
import com.google.api.services.samples.shared.appengine.OAuth2ClientCredentials;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for JDO persistence, OAuth flow helpers, and others.
 *
 * @author Yaniv Inbar
 */
class Utils {

  private static Lock lock = new ReentrantLock();
  private static String callbackUrl;

  private static final HttpTransport TRANSPORT = new UrlFetchTransport();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** Persistence manager factory instance. */
  private static final PersistenceManagerFactory PM_FACTORY =
      JDOHelper.getPersistenceManagerFactory("transactions-optional");

  static HttpTransport getTransport() {
    return TRANSPORT;
  }

  static JsonFactory getJsonFactory() {
    return JSON_FACTORY;
  }

  /**
   * Return the user id for the currently logged in user.
   */
  static final String getUserId() {
    UserService userService = UserServiceFactory.getUserService();
    User loggedIn = userService.getCurrentUser();
    return loggedIn.getUserId();
  }

  static String getCallbackUrl() {
    lock.lock();
    try {
      return callbackUrl;
    } finally {
      lock.unlock();
    }
  }

  static void setCallbackUrl(HttpServletRequest req) {
    GenericUrl url = new GenericUrl(getFullURL(req));
    url.setRawPath("/oauth2callback");
    lock.lock();
    try {
      callbackUrl = url.build();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Returns the full URL of the request including the query parameters.
   */
  static String getFullURL(HttpServletRequest request) {
    StringBuffer buf = request.getRequestURL();
    if (request.getQueryString() != null) {
      buf.append('?').append(request.getQueryString());
    }
    return buf.toString();
  }

  private static ThreeLeggedFlow newFlow(String userId) {
    return new GoogleOAuth2ThreeLeggedFlow(userId, OAuth2ClientCredentials.CLIENT_ID,
        OAuth2ClientCredentials.CLIENT_SECRET, CalendarUrl.ROOT_URL, callbackUrl);
  }

  private static Credential loadCredential() {
    PersistenceManager pm = Utils.getPersistenceManager();

    ThreeLeggedFlow oauthFlow = Utils.newFlow(Utils.getUserId());
    oauthFlow.setJsonFactory(Utils.getJsonFactory());
    oauthFlow.setHttpTransport(Utils.getTransport());

    try {
      return oauthFlow.loadCredential(pm);
    } finally {
      pm.close();
    }
  }

  static CalendarClient loadCalendarClient() {
    Credential credential = Utils.loadCredential();
    return new CalendarClient(
        new CalendarAppEngineRequestInitializer(credential).createRequestFactory());
  }

  static PersistenceManagerFactory getPersistenceManagerFactory() {
    return PM_FACTORY;
  }

  private static PersistenceManager getPersistenceManager() {
    return PM_FACTORY.getPersistenceManager();
  }

  static IOException newIOException(HttpResponseException e) throws IOException {
    return new IOException(e.getResponse().parseAsString());
  }

  private Utils() {
  }
}
