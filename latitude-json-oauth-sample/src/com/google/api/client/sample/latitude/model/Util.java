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

package com.google.api.client.sample.latitude.model;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Yaniv Inbar
 */
public class Util {
  public static final boolean DEBUG = false;
  public static final JsonFactory JSON_FACTORY = new JacksonFactory();
  public static final HttpTransport TRANSPORT = newTransport(false);
  public static final HttpTransport AUTH_TRANSPORT = newTransport(true);

  static HttpTransport newTransport(boolean forAuth) {
    HttpTransport result = new NetHttpTransport();
    GoogleUtils.useMethodOverride(result);
    GoogleHeaders headers = new GoogleHeaders();
    headers.setApplicationName("Google-LatitudeSample/1.0");
    result.defaultHeaders = headers;
    if (!forAuth) {
      JsonCParser parser = new JsonCParser();
      parser.jsonFactory = JSON_FACTORY;
      result.addParser(parser);
    }
    return result;
  }

  public static void enableLogging() {
    if (DEBUG) {
      Logger logger = Logger.getLogger("com.google.api.client");
      logger.setLevel(Level.CONFIG);
      logger.addHandler(new Handler() {

        @Override
        public void close() throws SecurityException {
        }

        @Override
        public void flush() {
        }

        @Override
        public void publish(LogRecord record) {
          // default ConsoleHandler will take care of >= INFO
          if (record.getLevel().intValue() < Level.INFO.intValue()) {
            System.out.println(record.getMessage());
          }
        }
      });
    }
  }
}
