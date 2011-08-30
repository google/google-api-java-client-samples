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

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.latitude.Latitude;
import com.google.api.services.latitude.model.LatitudeCurrentlocationResourceJson;
import com.google.api.services.latitude.model.Location;
import com.google.api.services.latitude.model.LocationFeed;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2ClientCredentials;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class LatitudeSample {

  /** OAuth 2 scope. */
  private static final String SCOPE = "https://www.googleapis.com/auth/latitude.all.best";

  private static void run(JsonFactory jsonFactory) throws Exception {
    // authorization
    HttpTransport transport = new NetHttpTransport();
    GoogleAccessProtectedResource accessProtectedResource = OAuth2Native.authorize(transport,
        jsonFactory,
        new LocalServerReceiver(),
        null,
        "google-chrome",
        OAuth2ClientCredentials.CLIENT_ID,
        OAuth2ClientCredentials.CLIENT_SECRET,
        SCOPE);

    // set up Latitude
    Latitude latitude = new Latitude(transport, accessProtectedResource, jsonFactory);
    latitude.setApplicationName("Google-LatitudeSample/1.0");
    latitude.setPrettyPrint(true);

    showCurrentLocation(latitude);
    showLocationHistory(latitude);
  }

  public static void main(String[] args) {
    JsonFactory jsonFactory = new JacksonFactory();
    try {
      try {
        OAuth2ClientCredentials.errorIfNotSpecified();
        run(jsonFactory);
        // success!
        return;
      } catch (HttpResponseException e) {
        if (!Json.CONTENT_TYPE.equals(e.getResponse().getContentType())) {
          System.err.println(e.getResponse().parseAsString());
        } else {
          GoogleJsonError errorResponse = GoogleJsonError.parse(jsonFactory, e.getResponse());
          System.err.println(errorResponse.code + " Error: " + errorResponse.message);
          for (ErrorInfo error : errorResponse.errors) {
            System.err.println(jsonFactory.toString(error));
          }
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static void showCurrentLocation(Latitude latitude) throws IOException {
    Latitude.CurrentLocation.Get request = latitude.currentLocation.get();
    // (optional) The finest granularity of locations you want to access. Can
    // be either city or best. If this parameter is omitted, city is assumed.
    request.setGranularity("best");
    LatitudeCurrentlocationResourceJson currentLocation = request.execute();
    System.out.println("Current location:");
    System.out.println(currentLocation);
  }

  private static void showLocationHistory(Latitude latitude) throws IOException {
    Latitude.Location.List request = latitude.location.list();
    // (optional) The finest granularity of locations you want to access. Can
    // be either city or best. If this parameter is omitted, city is assumed.
    request.setGranularity("best");
    LocationFeed locationFeed = request.execute();
    System.out.println("Location History:");
    if (locationFeed.getItems() == null) {
      System.out.println("No location history found.");
    } else {
      for(Location location : locationFeed.getItems()) {
        System.out.println(location);
      }
    }
  }
}
