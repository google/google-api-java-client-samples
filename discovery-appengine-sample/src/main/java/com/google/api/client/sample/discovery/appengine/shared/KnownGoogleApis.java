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

package com.google.api.client.sample.discovery.appengine.shared;


/**
 * Details of known Google API's.
 *
 * @author Yaniv Inbar
 */
public enum KnownGoogleApis {

  BIG_QUERY("BigQuery", "bigquery", "v1"),
  DISCOVERY("Discovery", "discovery", "0.1"),
  BUZZ("Google Buzz API", "buzz", "v1"),
  LATITUDE("Google Latitude API", "latitude", "v1"),
  MODERATOR("Google Moderator API", "moderator", "v1"),
  PREDICTION("Google Prediction API", "prediction", "v1");

  public final String displayName;
  public final String apiName;
  public final String version;

  KnownGoogleApis(String displayName, String apiName, String version) {
    this.displayName = displayName;
    this.apiName = apiName;
    this.version = version;
  }

}
