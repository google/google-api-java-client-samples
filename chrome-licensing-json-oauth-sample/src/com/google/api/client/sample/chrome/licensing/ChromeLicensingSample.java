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

package com.google.api.client.sample.chrome.licensing;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonHttpParser;
import com.google.api.client.sample.chrome.licensing.model.ChromeLicensingUrl;
import com.google.api.client.sample.chrome.licensing.model.ClientCredentials;
import com.google.api.client.sample.chrome.licensing.model.Debug;
import com.google.api.client.sample.chrome.licensing.model.License;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author Yaniv Inbar
 */
public class ChromeLicensingSample {

  public static void main(String[] args) {
    Debug.enableLogging();
    try {
      try {
        HttpTransport transport = setUpTransport();
        authorize(transport);
        showLicense(transport);
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  private static HttpTransport setUpTransport() {
    HttpTransport transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("google-chromelicensingsample-1.0");
    transport.addParser(new JsonHttpParser());
    return transport;
  }

  private static void authorize(HttpTransport transport) {
    OAuthParameters authorizer = new OAuthParameters();
    authorizer.consumerKey = ClientCredentials.ENTER_DOMAIN;
    authorizer.token = ClientCredentials.ENTER_OAUTH_TOKEN;
    OAuthHmacSigner signer = new OAuthHmacSigner();
    signer.clientSharedSecret = ClientCredentials.ENTER_CLIENT_SHARED_SECRET;
    signer.tokenSharedSecret = ClientCredentials.ENTER_OAUTH_TOKEN_SECRET;
    authorizer.signer = signer;
    authorizer.signRequestsUsingAuthorizationHeader(transport);
  }

  private static void showLicense(HttpTransport transport) throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = ChromeLicensingUrl.forUser(inputUsername());
    License license = request.execute().parseAs(License.class);
    System.out.print("YES".equals(license.result)
        ? "FULL".equals(license.accessLevel) ? "Full" : "Free" : "No");
    System.out.println(" License.");
  }

  private static String inputUsername() {
    System.out.print("Username to check license for: ");
    Scanner scanner = new Scanner(System.in);
    return scanner.next();
  }
}
