/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.youtube;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.youtube.model.Debug;
import com.google.api.client.sample.youtube.model.VideoFeed;
import com.google.api.client.sample.youtube.model.YouTubeUrl;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class YouTubeSample {

  public static void main(String[] args) {
    Debug.enableLogging();
    try {
      try {
        HttpTransport transport = setUpTransport();
        showVideos(transport);
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  private static HttpTransport setUpTransport() {
    HttpTransport transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("Google-YouTubeSample/1.0");
    headers.gdataVersion = "2";
    transport.addParser(new JsonCParser());
    return transport;
  }

  private static VideoFeed showVideos(HttpTransport transport)
      throws IOException {
    View.header("Get Videos");
    // build URL for the video feed for "search stories"
    YouTubeUrl url = YouTubeUrl.forVideosFeed();
    url.author = "searchstories";
    // execute GData request for the feed
    VideoFeed feed = VideoFeed.executeGet(transport, url);
    View.display(feed);
    return feed;
  }
}
