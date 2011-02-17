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

package com.google.api.client.sample.buzz.model;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;

/**
 * Buzz URL builder.
 *
 * @author Yaniv Inbar
 */
public class BuzzUrl extends GoogleUrl {

  @Key("max-results")
  public Integer maxResults = 5;

  /** Constructs a new Buzz URL from the given encoded URL. */
  public BuzzUrl(String encodedUrl) {
    super(encodedUrl);
    alt = "json";
    if (Util.DEBUG) {
      prettyprint = true;
    }
  }

  public static BuzzUrl forMyActivityFeed() {
    return new BuzzUrl("https://www.googleapis.com/buzz/v1/activities/@me/@self");
  }

  public static BuzzUrl forMyActivity(String activityId) {
    BuzzUrl result = forMyActivityFeed();
    result.pathParts.add(activityId);
    return result;
  }
}
