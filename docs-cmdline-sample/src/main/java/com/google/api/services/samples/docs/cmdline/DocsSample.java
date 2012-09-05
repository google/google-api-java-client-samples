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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.DocsClient;
import com.google.api.services.docs.DocsUrl;
import com.google.api.services.docs.model.DocumentListEntry;
import com.google.api.services.docs.model.DocumentListFeed;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class DocsSample {

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, DocsSample.class.getResourceAsStream("/client_secrets.json"));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
          + "into docs-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"), ".credentials/docs.json"), JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
        Collections.singleton(DocsUrl.ROOT_URL)).setCredentialStore(credentialStore).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public static void main(String[] args) {
    try {
      Credential credential = authorize();
      DocsClient client = new DocsClient(HTTP_TRANSPORT.createRequestFactory(credential));
      client.setApplicationName("Google-DocsSample/1.0");
      try {
        run(client);
      } catch (IOException e) {
        System.err.println(e.getMessage());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      try {
        HTTP_TRANSPORT.shutdown();
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
