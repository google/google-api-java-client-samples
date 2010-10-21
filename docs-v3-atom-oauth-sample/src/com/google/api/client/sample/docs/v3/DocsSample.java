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

package com.google.api.client.sample.docs.v3;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.docs.v3.model.Debug;
import com.google.api.client.sample.docs.v3.model.DocsUrl;
import com.google.api.client.sample.docs.v3.model.DocumentListEntry;
import com.google.api.client.sample.docs.v3.model.DocumentListFeed;
import com.google.api.client.sample.docs.v3.model.Namespace;
import com.google.api.client.xml.atom.AtomParser;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class DocsSample {

  private final HttpTransport transport;

  public DocsSample(HttpTransport transport) {
    super();
    this.transport = transport;
  }

  public static void main(String[] args) {
    Debug.enableLogging();
    try {
      try {
        HttpTransport transport = setUpTransport();
        DocsSample sample = new DocsSample(transport);
        sample.authorize();
        sample.showDocs();
        Auth.revoke();
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }
  }

  private static HttpTransport setUpTransport() {
    HttpTransport transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("Google-DocsSample/1.0");
    headers.gdataVersion = "3";
    AtomParser parser = new AtomParser();
    parser.namespaceDictionary = Namespace.DICTIONARY;
    transport.addParser(parser);
    return transport;
  }

  private void showDocs() throws IOException {
    header("Show Documents List");
    DocumentListFeed feed =
        DocumentListFeed.executeGet(transport, DocsUrl.forDefaultPrivateFull());
    display(feed);
  }

  private void authorize() throws Exception {
    Auth.authorize(transport);
  }

  private static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }

  private void display(DocumentListFeed feed) {
    for (DocumentListEntry doc : feed.docs) {
      display(doc);
    }
  }

  private void display(DocumentListEntry entry) {
    System.out.println(entry.title);
  }
}
