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

package com.google.api.client.sample.chrome.licensing.model;

import com.google.api.client.googleapis.GoogleUrl;

/**
 * @author Yaniv Inbar
 */
public class ChromeLicensingUrl extends GoogleUrl {

  public ChromeLicensingUrl(String url) {
    super(url);
    if (Debug.ENABLED) {
      this.prettyprint = true;
    }
  }

  public static ChromeLicensingUrl forUser(String userId) {
    ChromeLicensingUrl result = new ChromeLicensingUrl(
        "https://www.googleapis.com/chromewebstore/v1/licenses");
    result.pathParts.add(ClientCredentials.ENTER_APP_ID);
    result.pathParts.add(userId);
    return result;
  }
}
