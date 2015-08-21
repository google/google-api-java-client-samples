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

package com.google.api.services.samples.storage.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.samples.storage.examples.BucketsGetExample;
import com.google.api.services.samples.storage.examples.BucketsInsertExample;
import com.google.api.services.samples.storage.examples.ObjectsDownloadExample;
import com.google.api.services.samples.storage.examples.ObjectsGetMetadataExample;
import com.google.api.services.samples.storage.examples.ObjectsListExample;
import com.google.api.services.samples.storage.examples.ObjectsUploadExample;
import com.google.api.services.samples.storage.util.CredentialsProvider;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Ints;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.DigestOutputStream;
import java.security.MessageDigest;


/**
 * Sample for Google Cloud Storage JSON API client.
 */
public class StorageSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "Google-StorageSample/1.1";

  public static void main(String[] args) {
    try {
      // initialize network, sample settings, credentials, and the storage client.
      HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      SampleSettings settings = SampleSettings.load(jsonFactory);
      Credential credential = CredentialsProvider.authorize(httpTransport, jsonFactory);
      Storage storage = new Storage.Builder(httpTransport, jsonFactory, credential)
          .setApplicationName(APPLICATION_NAME).build();

      //
      // run commands
      //
      View.header1("Trying to create a new bucket " + settings.getBucket());
      BucketsInsertExample.createInProject(storage, settings.getProject(),
          new Bucket().setName(settings.getBucket()).setLocation("US"));

      View.header1("Getting bucket " + settings.getBucket() + " metadata");
      Bucket bucket = BucketsGetExample.get(storage, settings.getBucket());
      View.show(bucket);
      
      View.header1("Listing objects in bucket " + settings.getBucket());
      for (StorageObject object : ObjectsListExample.list(storage, settings.getBucket())) {
        View.show(object);
      }

      View.header1("Getting object metadata from gs://pub/SomeOfTheTeam.jpg");
      StorageObject object = ObjectsGetMetadataExample.get(storage, "pub", "SomeOfTheTeam.jpg");
      View.show(object);

      View.header1("Uploading object.");
      final long objectSize = 100 * 1024 * 1024 /* 100 MB */;
      InputStream data = new Helpers.RandomDataBlockInputStream(objectSize, 1024);
      object = new StorageObject()
          .setBucket(settings.getBucket())
          .setName(settings.getPrefix() + "myobject")
          .setMetadata(ImmutableMap.of("key1", "value1", "key2", "value2"))
          .setCacheControl("max-age=3600, must-revalidate")
          .setContentDisposition("attachment");
      object = ObjectsUploadExample.uploadWithMetadata(storage, object, data);
      View.show(object);
      System.out.println("md5Hash: " + object.getMd5Hash());
      System.out.println("crc32c: " + object.getCrc32c() + ", decoded to " + 
          ByteBuffer.wrap(BaseEncoding.base64().decode(object.getCrc32c())).getInt());

      View.header1("Getting object data of uploaded object, calculate hashes/crcs.");
      OutputStream nullOutputStream = new OutputStream() {
        // Throws away the bytes.
        @Override public void write(int b) throws IOException {}
        @Override public void write(byte b[], int off, int len) {}
      };
      DigestOutputStream md5DigestOutputStream = new DigestOutputStream(
          nullOutputStream, MessageDigest.getInstance("MD5"));
      HashingOutputStream crc32cHashingOutputStream = new HashingOutputStream(Hashing.crc32c(),
          md5DigestOutputStream);
      ObjectsDownloadExample.downloadToOutputStream(storage, settings.getBucket(),
          settings.getPrefix() + "myobject", crc32cHashingOutputStream);
      String calculatedMD5 = BaseEncoding.base64().encode(
          md5DigestOutputStream.getMessageDigest().digest());
      System.out.println("md5Hash: " + calculatedMD5 + " "
          + (object.getMd5Hash().equals(calculatedMD5)
          ? "(MATCHES)" : "(MISMATCHES; data altered in transit)"));
      int calculatedCrc32c = crc32cHashingOutputStream.hash().asInt();
      String calculatedEncodedCrc32c = BaseEncoding.base64().encode(
          Ints.toByteArray(calculatedCrc32c));
      // NOTE: Don't compare HashCode.asBytes() directly, as that encodes the crc32c in
      // little-endien. One would have to reverse the bytes first.
      System.out.println("crc32c: " + calculatedEncodedCrc32c + ", decoded to "
          + crc32cHashingOutputStream.hash().asInt() + " "
          + (object.getCrc32c().equals(calculatedEncodedCrc32c)
          ? "(MATCHES)" : "(MISMATCHES; data altered in transit)"));
      
      // success!
      return;
    } catch (GoogleJsonResponseException e) {
      // An error came back from the API.
      GoogleJsonError error = e.getDetails();
      System.err.println(error.getMessage());
      // More error information can be retrieved with error.getErrors().
    } catch (HttpResponseException e) {
      // No JSON body was returned by the API.
      System.err.println(e.getHeaders());
      System.err.println(e.getMessage());
    } catch (IOException e) {
      // Error formulating a HTTP request or reaching the HTTP service.
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

}
