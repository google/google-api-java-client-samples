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

package com.google.api.client.sample.youtube;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class YouTubeClient {

  private final JsonFactory jsonFactory = new JacksonFactory();

  private final HttpTransport transport = new NetHttpTransport();

  private final HttpRequestFactory requestFactory;

  public YouTubeClient() {
    final JsonCParser parser = new JsonCParser(jsonFactory);
    requestFactory = transport.createRequestFactory(new HttpRequestInitializer() {

      @Override
      public void initialize(HttpRequest request) {
        // headers
        GoogleHeaders headers = new GoogleHeaders();
        headers.setApplicationName("Google-YouTubeSample/1.0");
        headers.setGDataVersion("2");
        request.setHeaders(headers);
        request.setParser(parser);
      }
    });
  }

  public VideoFeed executeGetVideoFeed(YouTubeUrl url) throws IOException {
    return executeGetFeed(url, VideoFeed.class);
  }

  private <F extends Feed<? extends Item>> F executeGetFeed(YouTubeUrl url, Class<F> feedClass)
      throws IOException {
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(feedClass);
  }
}
