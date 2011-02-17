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

package com.google.api.client.sample.calendar.v2.appengine.server;

/**
 * Constants.
 *
 * @author Yaniv Inbar
 */
class Constants {

  /**
   * OAuth signature method.
   * <p>
   * A real deployed application normally uses only one of them. However, anonymous HMAC is the only
   * option when debugging on a local server.
   * </p>
   */
  enum SignatureMethod {

    /** Sign requests using {@code "RSA-SHA1"}. */
    RSA,

    /** Sign requests using {@code "HMAC-SHA1"}. */
    HMAC,
  }

  static final SignatureMethod SIGNATURE_METHOD = SignatureMethod.HMAC;

  /** Domain this web application is running on or {@code "anonymous"} by default. */
  static final String ENTER_DOMAIN = "anonymous";

  static class ConstantsForHMAC {

    /**
     * OAuth Consumer Key obtained from the <a
     * href="https://www.google.com/accounts/ManageDomains">Manage your domains</a> page or {@code
     * "anonymous"} by default.
     */
    static final String ENTER_OAUTH_CONSUMER_KEY = "anonymous";
  }

  static class ConstantsForRSA {
    static final String ENTER_PATH_TO_KEY_STORE_FILE = "WEB-INF/calendar.jks";
    static final String ENTER_KEY_STORE_PASSWORD = "calendar2store";
    static final String ENTER_ALIAS_FOR_PRIVATE_KEY = "calendar";
    static final String ENTER_PRIVATE_KEY_PASSWORD = "calendar2key";
  }

  static final String APP_NAME = "Google Calendar Data API Sample Web Client";

  static final String GWT_MODULE_NAME = "calendar";

  private Constants() {
  }
}
