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

package com.google.api.services.samples.latitude.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.latitude.Latitude;
import com.google.api.services.latitude.LatitudeRequest;
import com.google.api.services.latitude.model.Location;
import com.google.api.services.latitude.model.LocationFeed;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Yaniv Inbar
 */
public class LatitudeSample {

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** OAuth 2 scope. */
  private static final String SCOPE = "https://www.googleapis.com/auth/latitude.all.best";

  private static void run() throws Exception {
    // authorization
    Credential credential = OAuth2Native.authorize(
        HTTP_TRANSPORT, JSON_FACTORY, new LocalServerReceiver(), Arrays.asList(SCOPE));

    // set up Latitude
    Latitude latitude = new Latitude.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Google-LatitudeSample/1.0")
        .setJsonHttpRequestInitializer(new JsonHttpRequestInitializer() {
            @Override
          public void initialize(JsonHttpRequest request) {
            LatitudeRequest latitudeRequest = (LatitudeRequest) request;
            latitudeRequest.setPrettyPrint(true);
          }
        }).build();

    showCurrentLocation(latitude);
    showLocationHistory(latitude);
  }

  public static void main(String[] args) {
    try {
      try {
        run();
        // success!
        return;
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static void showCurrentLocation(Latitude latitude) throws IOException {
    Latitude.CurrentLocation.Get request = latitude.currentLocation().get();
    // (optional) The finest granularity of locations you want to access. Can
    // be either city or best. If this parameter is omitted, city is assumed.
    request.setGranularity("best");
    Location currentLocation = request.execute();
    System.out.println("Current location:");
    System.out.println(currentLocation);
  }

  private static void showLocationHistory(Latitude latitude) throws IOException {
    Latitude.Location.List request = latitude.location().list();
    // (optional) The finest granularity of locations you want to access. Can
    // be either city or best. If this parameter is omitted, city is assumed.
    request.setGranularity("best");
    LocationFeed locationFeed = request.execute();
    System.out.println("Location History:");
    if (locationFeed.getItems() == null) {
      System.out.println("No location history found.");
    } else {
      for (Location location : locationFeed.getItems()) {
        System.out.println(location);
      }
    }
  }
}
