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

package com.google.api.client.sample.docs.v3.model;

import com.google.api.client.googleapis.GoogleUrl;

/**
 * @author Yaniv Inbar
 */
public class DocsUrl extends GoogleUrl {

  public static final String ROOT_URL = "https://docs.google.com/feeds";

  public DocsUrl(String url) {
    super(url);
    if (Debug.ENABLED) {
      this.prettyprint = true;
    }
  }

  private static DocsUrl forRoot() {
    return new DocsUrl(ROOT_URL);
  }

  private static DocsUrl forDefault() {
    DocsUrl result = forRoot();
    result.pathParts.add("default");
    return result;
  }

  public static DocsUrl forDefaultPrivateFull() {
    DocsUrl result = forDefault();
    result.pathParts.add("private");
    result.pathParts.add("full");
    return result;
  }
}
