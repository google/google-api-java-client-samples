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

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.docs.DocsClient;
import com.google.api.services.docs.DocsUrl;
import com.google.api.services.docs.model.DocumentListEntry;
import com.google.api.services.docs.model.DocumentListFeed;
import com.google.api.services.samples.shared.cmdline.CmdlineUtils;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class DocsSample {

  public static void main(String[] args) {
    try {
      GoogleAccessProtectedResource accessProtectedResource =
          OAuth2Native
              .authorize(new LocalServerReceiver(), null, "google-chrome", DocsUrl.ROOT_URL);
      DocsClient client =
          new DocsClient(CmdlineUtils.getHttpTransport().createRequestFactory(
              accessProtectedResource));
      client.setApplicationName("Google-DocsSample/1.0");
      try {
        run(client);
      } catch (HttpResponseException e) {
        System.err.println(e.getResponse().parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      try {
        CmdlineUtils.getHttpTransport().shutdown();
      } catch (IOException e) {
        e.printStackTrace();
      }
      System.exit(1);
    }
  }

  public static void run(DocsClient client) throws IOException {
    showDocs(client);
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
