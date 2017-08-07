/*
 * Copyright (c) 2017 Google Inc.
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

package com.google.api.services.samples.streetview.publish.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.geo.ugc.streetview.publish.v1.StreetViewPublishResources.Photo;
import com.google.geo.ugc.streetview.publish.v1.StreetViewPublishResources.Pose;
import com.google.geo.ugc.streetview.publish.v1.StreetViewPublishResources.UploadRef;
import com.google.geo.ugc.streetview.publish.v1.StreetViewPublishServiceGrpc;
import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.google.streetview.publish.v1.StreetViewPublishServiceClient;
import com.google.streetview.publish.v1.StreetViewPublishServiceSettings;
import com.google.type.LatLng;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author Louis O'Bryan
 */
public class StreetViewPublishSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/streetview_publish_sample");

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static Credential credential;

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(StreetViewPublishSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://console.cloud.google.com/apis/credentials "
          + "into streetview-publish-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Arrays.asList("https://www.googleapis.com/auth/streetviewpublish")
        ).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

      // Authorize.
      credential = authorize();
      System.out.println("Access token: " + credential.getAccessToken());

      // Build a client to interact with the API.
      StreetViewPublishServiceSettings settings =
        StreetViewPublishServiceSettings.defaultBuilder()
          .setCredentialsProvider(FixedCredentialsProvider.create(new Credentials() {
              public String getAuthenticationType() {
                return "OAuth2";
              }
              public Map<String, List<String>> getRequestMetadata(URI uri) throws IOException {
                Map<String, List<String>> map = new HashMap<String, List<String>>();
                List<String> list = new ArrayList<String>();
                list.add("Bearer " + credential.getAccessToken());
                map.put("Authorization", list);
                return map;
              }
              public boolean hasRequestMetadata() { return true; }
              public boolean hasRequestMetadataOnly() { return true; }
              public void refresh() throws IOException { }
          }))
          .build();
      StreetViewPublishServiceClient client = StreetViewPublishServiceClient.create(settings);

      // Request upload url.
      UploadRef uploadRef = client.startUploadCallable().futureCall(Empty.newBuilder().build()).get();
      System.out.println("Requested upload url: " + uploadRef);

      // Upload photo bytes.
      HttpPost request = new HttpPost(uploadRef.getUploadUrl());
      request.addHeader("Authorization", "Bearer " + credential.getAccessToken());
      request.addHeader("Content-Type", "image/jpeg");
      request.addHeader("x-Goog-Upload-protocol", "raw");
      URL url = StreetViewPublishSample.class.getResource("/sample.jpg");
      Path path = Paths.get(url.toURI());
      byte[] data = Files.readAllBytes(path);
      request.addHeader("X-Goog-Upload-Content-Length", String.valueOf(data.length));
      request.setEntity(EntityBuilder.create().setBinary(data).build());

      HttpClient httpClient = new DefaultHttpClient();
      HttpResponse response = httpClient.execute(request);
      System.out.println("Http response: " + response);

      // Upload photo metadata.
      Photo photo = client.createPhoto(Photo.newBuilder()
          .setUploadReference(uploadRef)
          .setCaptureTime(Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000))
          .setPose(Pose.newBuilder()
            .setHeading(105d)
            .setLatLngPair(LatLng.newBuilder()
              .setLatitude(46.7512623d)
              .setLongitude(-121.9376983d)
              .build())
            .build()
            )
          .build());

      System.out.println("Uploaded photo metadata: " + photo);
      return;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

}
