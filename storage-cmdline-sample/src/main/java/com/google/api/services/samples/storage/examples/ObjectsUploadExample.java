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
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.samples.storage.util.CredentialsProvider;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/** Example of uploading data to create a GCS object. */
public class ObjectsUploadExample {

  private static final String BUCKET_NAME = "*** bucket name ***";
  private static final String OBJECT_NAME = "*** object name ***";
  private static final String FILE_NAME = "*** upload file name ***";  

  public static StorageObject uploadSimple(Storage storage, String bucketName, String objectName,
      String data) throws UnsupportedEncodingException, IOException {
    return uploadSimple(storage, bucketName, objectName, new ByteArrayInputStream(
        data.getBytes("UTF-8")), "text/plain");
  }
  
  public static StorageObject uploadSimple(Storage storage, String bucketName, String objectName,
      File data) throws FileNotFoundException, IOException {
    return uploadSimple(storage, bucketName, objectName, new FileInputStream(data),
        "application/octet-stream");
  }

  public static StorageObject uploadSimple(Storage storage, String bucketName, String objectName,
      InputStream data, String contentType) throws IOException {
    InputStreamContent mediaContent = new InputStreamContent(contentType, data);
    Storage.Objects.Insert insertObject = storage.objects().insert(bucketName, null, mediaContent)
        .setName(objectName);
    // The media uploader gzips content by default, and alters the Content-Encoding accordingly.
    // GCS dutifully stores content as-uploaded. This line disables the media uploader behavior,
    // so the service stores exactly what is in the InputStream, without transformation.
    insertObject.getMediaHttpUploader().setDisableGZipContent(true);
    return insertObject.execute();
  }
  
  public static StorageObject uploadWithMetadata(Storage storage, StorageObject object,
      InputStream data) throws IOException {
    InputStreamContent mediaContent = new InputStreamContent(object.getContentType(), data);
    Storage.Objects.Insert insertObject = storage.objects().insert(object.getBucket(), object,
        mediaContent);
    insertObject.getMediaHttpUploader().setDisableGZipContent(true);
    return insertObject.execute();
  }
  
  public static void main(String[] args) throws Exception {
    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
    Credential credential = CredentialsProvider.authorize(httpTransport, jsonFactory);
    Storage storage = new Storage.Builder(httpTransport, jsonFactory, credential)
        .setApplicationName("Google-ObjectsUploadExample/1.0").build();
    StorageObject object = uploadSimple(storage, BUCKET_NAME, OBJECT_NAME, new File(FILE_NAME));
    System.out.println(object.getName() + " (size: " + object.getSize() + ")");
  }

}
