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

package com.google.api.client.sample.bigquery.model;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;

/**
 * BigQuery URL builder.
 * 
 * @author Yaniv Inbar
 */
public final class BigQueryUrl extends GoogleUrl {

  private static final String ROOT_URL =
    "https://www.googleapis.com/bigquery/v1/";

  @Key
  public String q;

  /** Constructs a new BigQuery URL from the given encoded URL. */
  public BigQueryUrl(String encodedUrl) {
    super(encodedUrl);
    if (Debug.ENABLED) {
      prettyprint = true;
    }
  }

  /**
   * Constructs a new BigQuery URL based on the given relative path.
   * 
   * @param relativePath encoded path relative to the {@link #ROOT_URL}
   * @return new BigQuery URL
   */
  public static BigQueryUrl fromRelativePath(String relativePath) {
    return new BigQueryUrl(ROOT_URL + relativePath);
  }
}
