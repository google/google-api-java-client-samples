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

package com.google.api.client.sample.buzz.v1.cmdline;

/**
 * Identity credentials found in the "OAuth 2 Credentials" section of the "Identity" tab in the <a
 * href="https://code.google.com/apis/console">Google apis console</a>
 *
 * @author Yaniv Inbar
 */
class OAuth2ClientCredentials {

  /** Value of the "Client ID" shown in the console. */
  static final String CLIENT_ID = "enter_client_id";

  /** Value of the "Client secret" shown in the console. */
  static final String CLIENT_SECRET = "enter_client_secret";

  /**
   * Redirect URIs must be {@code "oob"}. This means the authorization is "out of band", or in other
   * words that there is no redirect.
   * <p>
   * Do not change the value here. Instead, in the console click "Edit redirect URIs...", type
   * "oob", and click "Update".
   * </p>
   */
  static final String REDIRECT_URIs = "oob";

  /** Value of the "Access Key" shown in the console. */
  static final String ACCESS_KEY = "enter_access_key";

  /** OAuth 2 scope to use (may also append {@code ".readonly"} for the read-only scope). */
  static final String SCOPE = "https://www.googleapis.com/auth/buzz";
}
