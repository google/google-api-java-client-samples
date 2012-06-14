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

package com.google.api.services.samples.shared.cmdline.oauth2;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.common.base.Preconditions;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Implements OAuth authentication "native" flow recommended for installed clients in which the end
 * user must grant access in a web browser and then copy a code into the application.
 *
 * <p>
 * The client_secrets.json file contains your client application's client ID and client secret. They
 * can be found in the <a href="https://code.google.com/apis/console/">Google APIs Console</a>. If
 * this is your first time, click "Create project...". Then, activate the Google APIs your client
 * application uses and agree to the terms of service. Now, click on "API Access", and then on
 * "Create an OAuth 2.0 Client ID...". Enter a product name and click "Next". >Select "Installed
 * application" and click "Create client ID". Finally, enter the "Client ID" and "Client secret"
 * shown under "Client ID for installed applications" into
 * {@code src/main/resources/client_secrets.json}.
 * </p>
 *
 * <p>
 * Warning: the client ID and secret are not secured and are plainly visible to users of your
 * application. It is a hard problem to secure client credentials in installed applications.
 * </p>
 *
 * <p>
 * In this sample code, it attempts to open the browser using {@link Desktop#isDesktopSupported()}.
 * If that fails, on Windows it tries {@code rundll32}. If that fails, it opens the browser
 * specified in {@link #BROWSER}, though note that currently we've only tested this code with Google
 * Chrome (hence this is the default value).
 * </p>
 *
 * @author Yaniv Inbar
 */
public class OAuth2Native {

  private static final String RESOURCE_LOCATION = "/client_secrets.json";

  private static final String RESOURCE_PATH =
      ("shared/shared-sample-cmdline/src/main/resources" + RESOURCE_LOCATION).replace(
          '/', File.separatorChar);

  /**
   * Browser to open in case {@link Desktop#isDesktopSupported()} is {@code false} or {@code null}
   * to prompt user to open the URL in their favorite browser.
   */
  private static final String BROWSER = "google-chrome";

  /** Google client secrets or {@code null} before initialized in {@link #authorize}. */
  private static GoogleClientSecrets clientSecrets = null;

  /** Returns the Google client secrets or {@code null} before initialized in {@link #authorize}. */
  public static GoogleClientSecrets getClientSecrets() {
    return clientSecrets;
  }

  /**
   * Loads the Google client secrets (if not already loaded).
   *
   * @param jsonFactory JSON factory
   */
  private static GoogleClientSecrets loadClientSecrets(JsonFactory jsonFactory) throws IOException {
    if (clientSecrets == null) {
      InputStream inputStream = OAuth2Native.class.getResourceAsStream(RESOURCE_LOCATION);
      Preconditions.checkNotNull(inputStream, "missing resource %s", RESOURCE_LOCATION);
      clientSecrets = GoogleClientSecrets.load(jsonFactory, inputStream);
      Preconditions.checkArgument(!clientSecrets.getDetails().getClientId().startsWith("[[")
          && !clientSecrets.getDetails().getClientSecret().startsWith("[["),
          "Please enter your client ID and secret from the Google APIs Console in %s from the "
          + "root samples directory", RESOURCE_PATH);
    }
    return clientSecrets;
  }


  /**
   * Authorizes the installed application to access user's protected data.
   *
   * @param transport HTTP transport
   * @param jsonFactory JSON factory
   * @param receiver verification code receiver
   * @param scopes OAuth 2.0 scopes
   */
  public static Credential authorize(HttpTransport transport, JsonFactory jsonFactory,
      VerificationCodeReceiver receiver, Iterable<String> scopes) throws Exception {
    try {
      String redirectUri = receiver.getRedirectUri();
      GoogleClientSecrets clientSecrets = loadClientSecrets(jsonFactory);
      // redirect to an authorization page
      // TODO(mlinder, 1.11.0-beta): Use setAccessType("offline").setApprovalPrompt("force") with
      // FileCredentialStore.
      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
          transport, jsonFactory, clientSecrets, scopes).setAccessType("online")
          .setApprovalPrompt("auto").build();
      browse(flow.newAuthorizationUrl().setRedirectUri(redirectUri).build());
      // receive authorization code and exchange it for an access token
      String code = receiver.waitForCode();
      GoogleTokenResponse response =
          flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
      // store credential and return it
      return flow.createAndStoreCredential(response, null);
    } finally {
      receiver.stop();
    }
  }

  /** Open a browser at the given URL. */
  private static void browse(String url) {
    // first try the Java Desktop
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (desktop.isSupported(Action.BROWSE)) {
        try {
          desktop.browse(URI.create(url));
          return;
        } catch (IOException e) {
          // handled below
        }
      }
    }
    // Next try rundll32 (only works on Windows)
    try {
      Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
      return;
    } catch (IOException e) {
      // handled below
    }
    // Next try the requested browser (e.g. "google-chrome")
    if (BROWSER != null) {
      try {
        Runtime.getRuntime().exec(new String[] {BROWSER, url});
        return;
      } catch (IOException e) {
        // handled below
      }
    }
    // Finally just ask user to open in their browser using copy-paste
    System.out.println("Please open the following URL in your browser:");
    System.out.println("  " + url);
  }

  private OAuth2Native() {
  }
}
