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
import com.google.api.services.storage.model.StorageObject;

import java.io.IOException;


/** Example of getting GCS object metadata. */
public class ObjectsGetMetadataExample {

  private static final String BUCKET_NAME = "*** bucket name ***";
  private static final String OBJECT_NAME = "*** object name ***";

  public static StorageObject get(Storage storage, String bucketName, String objectName)
      throws IOException {
    Storage.Objects.Get getObject = storage.objects().get(bucketName, objectName);
    return getObject.execute(); 
  }
  
  public static void main(String[] args) throws Exception {
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    Credential credential = CredentialsProvider.authorize(httpTransport, jsonFactory);
    Storage storage = new Storage.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName("Google-ObjectsGetMetadataExample/1.0").build();
    StorageObject object = get(storage, BUCKET_NAME, OBJECT_NAME);
    System.out.println(object.getName() + " (size: " + object.getSize() + ")");
  }

}
