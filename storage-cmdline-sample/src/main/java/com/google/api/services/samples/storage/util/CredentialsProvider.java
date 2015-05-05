/*
 * Copyright (c) 2012 Google Inc.
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

package com.google.api.services.samples.storage.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.samples.storage.cmdline.StorageSample;
import com.google.api.services.storage.StorageScopes;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

/**
 * Utility class to provide credentials and cache them in a local file.
 */
public class CredentialsProvider {
  
  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/storage_sample");

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application, but for this sample, it's only useful
   * for saving credentials across runs.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /**
   * Authorizes the installed application to access user's protected data.
   * 
   * <p>If you plan to run on AppEngine or Compute Engine, consider instead
   * {@link GoogleCredential#getApplicationDefault()}, which will use the ambient credentials
   * for the project's service-account.
   */
  public static Credential authorize(HttpTransport httpTransport, JsonFactory jsonFactory)
      throws IOException {
    dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
        new InputStreamReader(StorageSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/"
          + "credential into storage-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, jsonFactory, clientSecrets,
        Collections.singleton(StorageScopes.DEVSTORAGE_FULL_CONTROL)).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  } 

}
