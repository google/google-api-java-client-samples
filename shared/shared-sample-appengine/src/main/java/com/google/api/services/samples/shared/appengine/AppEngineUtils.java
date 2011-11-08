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

package com.google.api.services.samples.shared.appengine;

import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * Utilities for Google App Engine applications.
 * 
 * @author Yaniv Inbar
 */
public class AppEngineUtils {

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** Persistence manager factory instance. */
  private static final PersistenceManagerFactory PM_FACTORY = JDOHelper
      .getPersistenceManagerFactory("transactions-optional");

  /** Returns the global instance of the HTTP transport. */
  public static HttpTransport getHttpTransport() {
    return HTTP_TRANSPORT;
  }

  /** Returns the global instance of the JSON factory. */
  public static JsonFactory getJsonFactory() {
    return JSON_FACTORY;
  }

  /** Returns the Persistence Manager factory. */
  public static PersistenceManagerFactory getPersistenceManagerFactory() {
    return PM_FACTORY;
  }
}
