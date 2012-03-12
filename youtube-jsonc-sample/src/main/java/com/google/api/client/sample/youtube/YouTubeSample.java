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

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class YouTubeSample {

  private static void run() throws IOException {
    YouTubeClient client = new YouTubeClient();
    showVideos(client);
  }

  public static void main(String[] args) {
    try {
      try {
        run();
        return;
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static VideoFeed showVideos(YouTubeClient client) throws IOException {
    View.header("Get Videos");
    // build URL for the video feed for "search stories"
    YouTubeUrl url = YouTubeUrl.forVideosFeed();
    url.author = "searchstories";
    // execute GData request for the feed
    VideoFeed feed = client.executeGetVideoFeed(url);
    View.display(feed);
    return feed;
  }
}
