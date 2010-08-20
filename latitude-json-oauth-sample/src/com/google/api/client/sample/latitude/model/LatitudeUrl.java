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

package com.google.api.client.sample.latitude.model;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;

/**
 * Latitude URL builder.
 *
 * @author Yaniv Inbar
 */
public final class LatitudeUrl extends GoogleUrl {

  @Key
  public String granularity;
  
  @Key("min-time")
  public String minTime;
  
  @Key("max-time")
  public String maxTime;
  
  @Key("max-results")
  public String maxResults;
  
  /** Constructs a new Latitude URL from the given encoded URI. */
  public LatitudeUrl(String encodedUrl) {
    super(encodedUrl);
    if (Debug.ENABLED) {
      prettyprint = true;
    }
  }

  private static LatitudeUrl root() {
    return new LatitudeUrl("https://www.googleapis.com/latitude/v1");
  }

  public static LatitudeUrl forCurrentLocation() {
    LatitudeUrl result = root();
    result.pathParts.add("currentLocation");
    return result;
  }

  public static LatitudeUrl forLocation() {
    LatitudeUrl result = root();
    result.pathParts.add("location");
    return result;
  }

  public static LatitudeUrl forLocation(Long timestampMs) {
    LatitudeUrl result = forLocation();
    result.pathParts.add(timestampMs.toString());
    return result;
  }
}
