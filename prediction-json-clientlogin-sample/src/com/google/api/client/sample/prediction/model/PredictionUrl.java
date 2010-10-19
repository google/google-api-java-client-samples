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

package com.google.api.client.sample.prediction.model;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;

/**
 * Prediction URL builder.
 *
 * @author Yaniv Inbar
 */
public final class PredictionUrl extends GoogleUrl {

  @Key
  public String data;

  /** Constructs a new Prediction URL from the given encoded URL. */
  public PredictionUrl(String encodedUrl) {
    super(encodedUrl);
    if (Debug.ENABLED) {
      prettyprint = true;
    }
  }

  private static PredictionUrl root() {
    return new PredictionUrl(
        "https://www.googleapis.com/prediction/v1.1/training");
  }

  /**
   * Constructs a new training URL based on the given object path of the form
   * {@code "mybucket/myobject"}.
   */
  public static PredictionUrl forTraining(String objectPath) {
    PredictionUrl result = root();
    result.data = objectPath;
    return result;
  }

  /**
   * Constructs a new check training URL based on the given object path of the
   * form {@code "mybucket/myobject"}.
   */
  public static PredictionUrl forCheckingTraining(String objectPath) {
    PredictionUrl result = root();
    // this will ensure that objectPath is encoded properly, e.g. "/" -> "%2F"
    result.pathParts.add(objectPath);
    return result;
  }

  /**
   * Constructs a new prediction URL based on the given object path of the form
   * {@code "mybucket/myobject"}.
   */
  public static PredictionUrl forPrediction(String objectPath) {
    PredictionUrl result = forCheckingTraining(objectPath);
    result.pathParts.add("predict");
    return result;
  }
}
