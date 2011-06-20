/*
 * Copyright (c) 2011 Google Inc.
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

package com.google.api.client.sample.buzz.appengine.oauth;

/**
 * OAuth 1.0 credentials found in the <a href="https://code.google.com/apis/console">Google APIs
 * Console</a>.
 *
 * <p>
 * Once at the Google APIs Console, click on "Add project...", or if you've already set up a
 * project, click the arrow next to the project name and click on "Create..." under "Other
 * projects". For "Buzz API", click on the status switch to flip it to "ON", and agree to the terms
 * of service. Next, click on "API Access". Click on "Create an OAuth 2.0 Client ID...". Select a
 * product name and click "Next". Make sure you select "Installed application" and click "Create
 * client ID".
 * </p>
 *
 * @author Jacob Moshenko
 */
class OAuthClientCredentials {
  /** OAuth 1 Consumer Key available from the "Keys" page of the Google APIs Console */
  static final String CONSUMER_KEY = "anonymous";

  /** OAuth 1 Consumer Secret available from the "Keys" page of the Google APIs Console */
  static final String CONSUMER_SECRET = "anonymous";

  /** OAuth scope(s) to which this app requires access */
  static final String SCOPE = "https://www.googleapis.com/auth/buzz";

  /** Display name that will be presented to the user when requesting access */
  static final String X_OAUTH_DISPLAYNAME = "Hey Bro, mind if I access your Buzz?";
}