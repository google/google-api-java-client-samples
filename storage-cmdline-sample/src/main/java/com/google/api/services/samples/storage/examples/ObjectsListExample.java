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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.samples.storage.util.CredentialsProvider;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;


/** Example of listing objects in a GCS bucket. */
public class ObjectsListExample {

  private static final String BUCKET_NAME = "*** bucket name ***";
  
  public static Iterable<StorageObject> list(Storage storage, String bucketName)
      throws IOException {
    List<List<StorageObject>> pagedList = Lists.newArrayList();
    Storage.Objects.List listObjects = storage.objects().list(bucketName);
    Objects objects;
    do {
      objects = listObjects.execute();
      List<StorageObject> items = objects.getItems();
      if (items != null) {
        pagedList.add(objects.getItems());
      }
      listObjects.setPageToken(objects.getNextPageToken());
    } while (objects.getNextPageToken() != null);
    return Iterables.concat(pagedList);
  }
  
  public static void main(String[] args) throws Exception {
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    Credential credential = CredentialsProvider.authorize(httpTransport, jsonFactory);
    Storage storage = new Storage.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName("Google-ObjectsListExample/1.0").build();
    for (StorageObject object : list(storage, BUCKET_NAME)) {
      System.out.println(object.getName() + " (size: " + object.getSize() + ")");
    }
  }
}
