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

package com.google.api.services.samples.storage.examples;

import static java.net.HttpURLConnection.HTTP_CONFLICT;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.samples.storage.util.CredentialsProvider;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;

import java.io.IOException;


/** Example of creating a GCS bucket. */
public class BucketsInsertExample {
  
  private static final String PROJECT_NAME = "*** project name ***";
  private static final String BUCKET_NAME = "*** bucket name ***";
  private static final String BUCKET_LOCATION = "*** bucket location ***";

  public static Bucket create(Storage storage, Bucket bucket) throws IOException {
    return createInProject(storage, bucket.getProjectNumber().toString(), bucket);
  }
  
  public static Bucket createInProject(Storage storage, String project, Bucket bucket)
      throws IOException {
    try {
      Storage.Buckets.Insert insertBucket = storage.buckets().insert(project, bucket);
      return insertBucket.execute();
    } catch (GoogleJsonResponseException e) {
      GoogleJsonError error = e.getDetails();
      if (error != null && error.getCode() == HTTP_CONFLICT
          && error.getMessage().contains("You already own this bucket.")) {
        System.out.println("already exists");
        return bucket;
      }
      System.err.println(error.getMessage());
      throw e;
    }
  }
  
  public static void main(String[] args) throws Exception {
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    Credential credential = CredentialsProvider.authorize(httpTransport, jsonFactory);
    Storage storage = new Storage.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName("Google-BucketsInsertExample/1.0").build();
    createInProject(storage, PROJECT_NAME, new Bucket().setName(BUCKET_NAME)
        .setLocation(BUCKET_LOCATION));
  }

}
