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

package com.google.api.services.samples.docs.cmdline;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.services.samples.shared.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.oauth2.OAuth2ClientCredentials;
import com.google.api.services.samples.shared.oauth2.OAuth2Native;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class DocsClient {

  private static final String SCOPE = DocsUrl.ROOT_URL;

  static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary()
      .set("", "http://www.w3.org/2005/Atom")
      .set("app", "http://www.w3.org/2007/app")
      .set("batch", "http://schemas.google.com/gdata/batch")
      .set("docs", "http://schemas.google.com/docs/2007")
      .set("gAcl", "http://schemas.google.com/acl/2007")
      .set("gd", "http://schemas.google.com/g/2005")
      .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
      .set("xml", "http://www.w3.org/XML/1998/namespace");

  private final HttpTransport transport = new NetHttpTransport();

  private HttpRequestFactory requestFactory;

  public void authorize() throws Exception {
    if (OAuth2ClientCredentials.CLIENT_ID == null
        || OAuth2ClientCredentials.CLIENT_SECRET == null) {
      System.err.println(
          "Please enter your client ID and secret in " + OAuth2ClientCredentials.class);
      System.exit(1);
    }
    final GoogleAccessProtectedResource accessProtectedResource = OAuth2Native.authorize(transport,
        new JacksonFactory(),
        new LocalServerReceiver(),
        null,
        "google-chrome",
        OAuth2ClientCredentials.CLIENT_ID,
        OAuth2ClientCredentials.CLIENT_SECRET,
        SCOPE);
    final MethodOverride override = new MethodOverride(); // needed for PATCH
    requestFactory = transport.createRequestFactory(new HttpRequestInitializer() {

      @Override
      public void initialize(HttpRequest request) {
        GoogleHeaders headers = new GoogleHeaders();
        headers.setApplicationName("Google-DocsSample/1.0");
        headers.gdataVersion = "3";
        request.setHeaders(headers);
        request.setInterceptor(new HttpExecuteInterceptor() {

          @Override
          public void intercept(HttpRequest request) throws IOException {
            override.intercept(request);
            accessProtectedResource.intercept(request);
          }
        });
        request.addParser(new AtomParser(DICTIONARY));
        request.setUnsuccessfulResponseHandler(accessProtectedResource);
      }
    });
  }

  public void shutdown() throws IOException {
    transport.shutdown();
  }

  <F extends Feed> F executeGetFeed(DocsUrl url, Class<F> feedClass) throws IOException {
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(feedClass);
  }

  public DocumentListFeed executeGetDocumentListFeed(DocsUrl url) throws IOException {
    return executeGetFeed(url, DocumentListFeed.class);
  }
}
