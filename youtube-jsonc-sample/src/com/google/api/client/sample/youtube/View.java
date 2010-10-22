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

import com.google.api.client.sample.youtube.model.Video;
import com.google.api.client.sample.youtube.model.VideoFeed;

/**
 * @author Yaniv Inbar
 */
public class View {

  static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }

  static void display(VideoFeed feed) {
    System.out.println("Total number of videos: " + feed.totalItems);
    for (Video video : feed.items) {
      System.out.println();
      System.out.println("-----------------------------------------------");
      display(video);
    }
  }

  static void display(Video video) {
    System.out.println("Video title: " + video.title);
    System.out.println("Updated: " + video.updated);
    if (video.description != null) {
      System.out.println("Description: " + video.description);
    }
    if (!video.tags.isEmpty()) {
      System.out.println("Tags: " + video.tags);
    }
    if (video.player != null) {
      System.out.println("Play URL: " + video.player.defaultUrl);
    }
  }
}
