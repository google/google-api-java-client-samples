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

package com.google.api.services.samples.docs.cmdline;

import com.google.api.client.http.HttpResponseException;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class DocsSample {

  public static void main(String[] args) {
    DocsClient client = new DocsClient();
    try {
      client.authorize();
      try {
        showDocs(client);
        shutdown(client);
      } catch (HttpResponseException e) {
        System.err.println(e.getResponse().parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      shutdown(client);
      System.exit(1);
    }
  }

  private static void shutdown(DocsClient client) {
    try {
      client.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void showDocs(DocsClient client) throws IOException {
    header("Show Documents List");
    DocumentListFeed feed = client.executeGetDocumentListFeed(DocsUrl.forDefaultPrivateFull());
    display(feed);
  }

  private static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }

  private static void display(DocumentListFeed feed) {
    System.out.println("Displaying " + feed.docs.size() + " documents");
    System.out.println();
    for (DocumentListEntry doc : feed.docs) {
      display(doc);
    }
  }

  private static void display(DocumentListEntry entry) {
    System.out.println(entry.title);
  }
}
