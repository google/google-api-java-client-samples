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

package com.google.api.client.sample.youtube;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Yaniv Inbar
 */
public class Util {
  /**
   * Set to {@code true} to show HTTP request/response or {@code false} to show only normal output.
   */
  static final boolean LOG_REQUESTS = false;

  static final HttpTransport TRANSPORT = newTransport();

  public static void enableLogging() {
    if (LOG_REQUESTS) {
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

  private static HttpTransport newTransport() {
    HttpTransport result = new NetHttpTransport();
    GoogleUtils.useMethodOverride(result);
    GoogleHeaders headers = new GoogleHeaders();
    headers.setApplicationName("Google-YouTubeSample/1.0");
    result.defaultHeaders = headers;
    headers.gdataVersion = "2";
    JsonCParser parser = new JsonCParser();
    parser.jsonFactory = new JacksonFactory();
    result.addParser(parser);
    return result;
  }
}
