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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/** Example of downloading a GCS object. */
public class ObjectsDownloadExample {

  private static final boolean IS_APP_ENGINE = false;
  
  private static final String BUCKET_NAME = "*** bucket name ***";
  private static final String OBJECT_NAME = "*** object name ***";

  public static InputStream download(Storage storage, String bucketName, String objectName)
      throws IOException {
    Storage.Objects.Get getObject = storage.objects().get(bucketName, objectName);
    getObject.getMediaHttpDownloader().setDirectDownloadEnabled(!IS_APP_ENGINE);
    return getObject.executeMediaAsInputStream(); 
  }

  public static void downloadToOutputStream(Storage storage, String bucketName, String objectName,
      OutputStream data) throws IOException {
    Storage.Objects.Get getObject = storage.objects().get(bucketName, objectName);
    getObject.getMediaHttpDownloader().setDirectDownloadEnabled(!IS_APP_ENGINE);
    getObject.executeMediaAndDownloadTo(data); 
  }
  
  /**
   * This shows how to download a portion of an object. Especially useful for
   * resuming after a download fails, but can also be used to download in
   * parallel.
   */
  public static void downloadRangeToOutputStream(Storage storage, String bucketName,
      String objectName, long firstBytePos, long lastBytePos, OutputStream data)
      throws IOException {
    Storage.Objects.Get getObject = storage.objects().get(bucketName, objectName);
    // Remove cast after https://github.com/google/google-api-java-client/issues/937 is addressed.
    getObject.getMediaHttpDownloader().setDirectDownloadEnabled(!IS_APP_ENGINE)
        .setContentRange(firstBytePos, (int) lastBytePos);
    getObject.executeMediaAndDownloadTo(data);
  }
     
  public static void main(String[] args) throws Exception {
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    Credential credential = CredentialsProvider.authorize(httpTransport, jsonFactory);
    Storage storage = new Storage.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName("Google-ObjectsDownloadExample/1.0").build();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    downloadToOutputStream(storage, BUCKET_NAME, OBJECT_NAME, out);
    System.out.println("Downloaded " + out.toByteArray().length + " bytes");
  }
}
