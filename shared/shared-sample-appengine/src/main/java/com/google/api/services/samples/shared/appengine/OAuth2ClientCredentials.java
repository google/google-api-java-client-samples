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

package com.google.api.services.samples.shared.appengine;

import com.google.api.client.json.JsonParser;
import com.google.api.client.util.Key;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * OAuth 2 credentials found in the <a href="https://code.google.com/apis/console">Google apis
 * console</a>.
 * 
 * <p>
 * Once at the Google APIs console, click on "Add project...". If you've already set up a project,
 * you may use that one instead, or create a new one by clicking on the arrow next to the project
 * name and click on "Create..." under "Other projects". For each API you want to use, click on the
 * status switch to flip it to "ON", and agree to the terms of service.
 * </p>
 * 
 * <p>
 * Next, click on "API Access", and then on "Create an OAuth 2.0 Client ID...". Enter your product
 * name and click "Next". For running the App Engine application locally, select "Installed
 * application" and click "Create client ID". For the deployed App Engine application, use "Web
 * application" instead and follow the prompts.
 * </p>
 * 
 * @author Yaniv Inbar
 */
public class OAuth2ClientCredentials {

  /** Client ID and secret. */
  static class Credentials {

    @Key("client_id")
    String clientId;

    @Key("client_secret")
    String clientSecret;
  }

  private static final String RESOURCE_LOCATION = "/client_secrets.json";

  private static Credentials credentials = null;

  /** Returns the client ID. */
  public static String getClientId() throws IOException {
    return getCredentials().clientId;
  }

  /** Returns the client secret. */
  public static String getClientSecret() throws IOException {
    return getCredentials().clientSecret;
  }

  private static Credentials getCredentials() throws IOException {
    if (credentials == null) {
      InputStream inputStream =
          OAuth2ClientCredentials.class.getResourceAsStream(RESOURCE_LOCATION);
      Preconditions.checkNotNull(inputStream, "missing resource", RESOURCE_LOCATION);
      JsonParser parser = AppEngineUtils.getJsonFactory().createJsonParser(inputStream);
      parser.skipToKey("web");
      credentials = new Credentials();
      parser.parse(credentials, null);
      Preconditions.checkArgument(
          !credentials.clientId.isEmpty() && !credentials.clientSecret.isEmpty(),
          "Please enter your client ID and secret from the Google APIs Console in %s",
          RESOURCE_LOCATION);
    }
    return credentials;
  }
}
