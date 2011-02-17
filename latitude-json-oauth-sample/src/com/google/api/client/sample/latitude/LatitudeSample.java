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

package com.google.api.client.sample.latitude;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.sample.latitude.model.LocationResource;
import com.google.api.client.sample.latitude.model.Util;

import java.io.IOException;
import java.util.List;

/**
 * @author Yaniv Inbar
 */
public class LatitudeSample {

  public static void main(String[] args) {
    Util.enableLogging();
    try {
      try {
        Auth.authorize();
        showCurrentLocation();
        showLocationHistory();
        Auth.revoke();
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }
  }

  private static void showCurrentLocation() throws IOException {
    System.out.println("Current location:");
    LocationResource current = LocationResource.executeGetCurrentLocation();
    System.out.println(Util.JSON_FACTORY.toString(current));
  }

  private static void showLocationHistory() throws IOException {
    System.out.println();
    System.out.println("Location History:");
    List<LocationResource> locations = LocationResource.executeList();
    for (LocationResource location : locations) {
      System.out.println(Util.JSON_FACTORY.toString(location));
    }
  }
}
