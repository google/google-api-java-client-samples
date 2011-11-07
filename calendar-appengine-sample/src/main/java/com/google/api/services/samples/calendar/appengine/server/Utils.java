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

import com.google.api.client.extensions.auth.helpers.Credential;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth2.draft10.GoogleOAuth2ThreeLeggedFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.calendar.CalendarClient;
import com.google.api.services.calendar.CalendarUrl;
import com.google.api.services.samples.shared.appengine.AppEngineUtils;
import com.google.api.services.samples.shared.appengine.OAuth2ClientCredentials;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for JDO persistence, OAuth flow helpers, and others.
 * 
 * @author Yaniv Inbar
 */
class Utils {

  private static Lock lock = new ReentrantLock();
  private static String callbackUrl;

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

  private static ThreeLeggedFlow newFlow(String userId) throws IOException {
    return new GoogleOAuth2ThreeLeggedFlow(userId, OAuth2ClientCredentials.getClientId(),
        OAuth2ClientCredentials.getClientSecret(), CalendarUrl.ROOT_URL, callbackUrl);
  }

  private static Credential loadCredential() throws IOException {
    PersistenceManager pm = Utils.getPersistenceManager();

    ThreeLeggedFlow oauthFlow = Utils.newFlow(Utils.getUserId());
    oauthFlow.setJsonFactory(AppEngineUtils.getJsonFactory());
    oauthFlow.setHttpTransport(AppEngineUtils.getHttpTransport());

    try {
      return oauthFlow.loadCredential(pm);
    } finally {
      pm.close();
    }
  }

  static CalendarClient loadCalendarClient() throws IOException {
    Credential credential = Utils.loadCredential();
    return new CalendarClient(
        new CalendarAppEngineRequestInitializer(credential).createRequestFactory());
  }

  private static PersistenceManager getPersistenceManager() {
    return AppEngineUtils.getPersistenceManagerFactory().getPersistenceManager();
  }

  static IOException newIOException(HttpResponseException e) throws IOException {
    return new IOException(e.getResponse().parseAsString());
  }

  private Utils() {
  }
}
