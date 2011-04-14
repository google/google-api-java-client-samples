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

package com.google.api.client.sample.verification.model;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.sample.verification.ClientCredentials;
import com.google.api.client.util.Key;

/**
 * Latitude URL builder.
 *
 * @author Kevin Marshall
 */
public final class VerificationUrl extends GoogleUrl {
  @Key
  public String key = ClientCredentials.ENTER_API_KEY;

  @Key
  public String verificationMethod;

  @Key
  public String granularity;

  @Key("min-time")
  public String minTime;

  @Key("max-time")
  public String maxTime;

  @Key("max-results")
  public String maxResults;

  /** Constructs a new Site Verification URL from the given encoded URI. */
  public VerificationUrl(String encodedUrl) {
    super(encodedUrl);
    if (Debug.ENABLED) {
      prettyprint = true;
    }
  }

  private static VerificationUrl root() {
    return new VerificationUrl(
        "https://www.googleapis.com/siteVerification/v1");
  }

  public static VerificationUrl forWebResource() {
    VerificationUrl result = root();
    result.pathParts.add("webResource");
    return result;
  }

  public static VerificationUrl forInsertedWebResource(
      String verificationMethod) {
    VerificationUrl result = forWebResource();
    result.verificationMethod = verificationMethod;
    return result;
  }

  public static VerificationUrl forWebResource(String resourceId) {
    VerificationUrl result = forWebResource();
    result.setRawPath(result.getRawPath() + "/" + resourceId);
    return result;
  }

  public static VerificationUrl forToken() {
    VerificationUrl result = root();
    result.pathParts.add("token");
    return result;
  }
}
