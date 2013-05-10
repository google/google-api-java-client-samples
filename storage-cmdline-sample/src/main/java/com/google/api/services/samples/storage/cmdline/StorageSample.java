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

import static java.net.HttpURLConnection.HTTP_CONFLICT;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.api.client.util.Lists;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.ObjectAccessControl;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * @author nherring@google.com (Nathan Herring)
 */
public class StorageSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  /**
   * Whether or not we're running on AppEngine. Not set for this command-line sample, but
   * is relevant if you copy this code for use there.
   */
  private static final boolean IS_APP_ENGINE = false;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** Global instance of this sample's settings. */
  private static SampleSettings settings;

  private static Storage storage;


  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, 
        new InputStreamReader(StorageSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=storage_api "
          + "into storage-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"), ".credentials/storage.json"), JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(StorageScopes.DEVSTORAGE_FULL_CONTROL))
            .setCredentialStore(credentialStore).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /** Loads sample-specific settings from a JSON file. */
  private static void readSettings() {
    settings = SampleSettings.load(JSON_FACTORY,
        StorageSample.class.getResourceAsStream("/sample_settings.json"));
    if (settings.getProject().startsWith("Enter ") ||
        settings.getBucket().startsWith("Enter ")) {
      System.out.println(
          "Enter sample settings into "
          + "storage-cmdline-sample/src/main/resources/sample_settings.json");
      System.exit(1);
    }
  }

  /** Samples settings JSON Model. */
  public static final class SampleSettings extends GenericJson {
    @Key("project")
    private String project;

    @Key("bucket")
    private String bucket;

    @Key("prefix")
    private String prefix;

    @Key("email")
    private String email;

    @Key("domain")
    private String domain;

    public String getProject() {
      return project;
    }

    public String getBucket() {
      return bucket;
    }

    public String getPrefix() {
      return prefix;
    }

    public String getEmail() {
      return email;
    }

    public String getDomain() {
      return domain;
    }

    public static SampleSettings load(JsonFactory jsonFactory,
        InputStream inputStream) {
      try {
        return jsonFactory.fromInputStream(inputStream, SampleSettings.class);
      } catch (IOException e) {
        return new SampleSettings();
      }
    }
  }

  /** Provides simple cmdline UI. */
  public static class View {

    static void header1(String name) {
      System.out.println();
      System.out.println("================== " + name + " ==================");
      System.out.println();
    }

    static void header2(String name) {
      System.out.println();
      System.out.println("~~~~~~~~~~~~~~~~~~ " + name + " ~~~~~~~~~~~~~~~~~~");
      System.out.println();
    }

    static void show(Bucket bucket) {
      System.out.println("name: " + bucket.getName());
      System.out.println("location: " + bucket.getLocation());
      System.out.println("timeCreated: " + bucket.getTimeCreated());
      System.out.println("owner: " + bucket.getOwner());
      System.out.println("acl: " + bucket.getAcl());
    }

    static void show(StorageObject object) {
      System.out.println("name: " + object.getName());
      System.out.println("size: " + object.getSize());
      System.out.println("contentType: " + object.getContentType());
      System.out.println("updated: " + object.getUpdated());
      System.out.println("owner: " + object.getOwner());
      // should only show up if projection is full.
      //System.out.println("acl: " + object.getAcl());
    }

    static void separator() {
      System.out.println();
      System.out.println("------------------------------------------------------");
      System.out.println();
    }
  }

  public static void main(String[] args) {
    try {
      try {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // settings
        readSettings();
        // authorization
        Credential credential = authorize();
        // set up global Storage instance
        storage = new Storage.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
        // run commands
        tryCreateBucket();
        getBucket();
        listObjects();
        getObjectMetadata();
        uploadObject(true /* useCustomMetadata */);
        getObjectData();
        getPartialObjectData();
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
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static void tryCreateBucket() throws IOException {
    View.header1("Trying to create a new bucket " + settings.getBucket());
    Storage.Buckets.Insert insertBucket = storage.buckets().insert(
        settings.getProject(),
        new Bucket()
            .setName(settings.getBucket())
            .setLocation("US")
            //.setDefaultObjectAcl(ImmutableList.of(
            //    new ObjectAccessControl().setEntity("allAuthenticatedUsers").setRole("READER")))
        );
    try {
      @SuppressWarnings("unused")
      Bucket createdBucket = insertBucket.execute();
    } catch (GoogleJsonResponseException e) {
      GoogleJsonError error = e.getDetails();
      if (error.getCode() == HTTP_CONFLICT
          && error.getMessage().contains("You already own this bucket.")) {
        System.out.println("already exists");
      } else {
        throw e;
      }
    }
  }

  private static void getBucket() throws IOException {
    View.header1("Getting bucket " + settings.getBucket() + " metadata");
    Storage.Buckets.Get getBucket = storage.buckets().get(settings.getBucket());
    getBucket.setProjection("full");
    Bucket bucket = getBucket.execute();
    View.show(bucket);
  }

  private static void listObjects() throws IOException {
    View.header1("Listing objects in bucket " + settings.getBucket());
    Storage.Objects.List listObjects = storage.objects().list(settings.getBucket());
    listObjects.setMaxResults(5L);
    Objects objects = listObjects.execute();
    // Keep track of the page number in case we're listing objects
    // for a bucket with thousands of objects. We'll limit ourselves
    // to 5 pages
    int currentPageNumber = 0;
    while (objects.getItems() != null && !objects.getItems().isEmpty()
        && ++currentPageNumber <= 5) {
      for (StorageObject object : objects.getItems()) {
        View.show(object);
        View.separator();
      }
      // Fetch the next page
      String nextPageToken = objects.getNextPageToken();
      if (nextPageToken == null) {
        break;
      }
      listObjects.setPageToken(nextPageToken);
      View.header2("New page of objects");
      objects = listObjects.execute();
    }
  }

  private static void getObjectMetadata() throws IOException {
    View.header1("Getting object metadata from gs://pub/SomeOfTheTeam.jpg");
    Storage.Objects.Get getObject = storage.objects().get("pub", "SomeOfTheTeam.jpg");
    StorageObject object = getObject.execute();
    View.show(object);
  }

  /**
   * Generates a random data block and repeats it to provide the stream.
   *
   * Using a buffer instead of just filling from java.util.Random because the
   * latter causes noticeable lag in stream reading, which detracts from upload
   * speed. This class takes all that cost in the constructor.
   */
  private static class RandomDataBlockInputStream extends InputStream {

    private long byteCountRemaining;
    private final byte[] buffer;

    public RandomDataBlockInputStream(long size, int blockSize) {
      byteCountRemaining = size;
      final Random random = new Random();
      buffer = new byte[blockSize];
      random.nextBytes(buffer);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() {
      throw new AssertionError("Not implemented; too slow.");
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte [], int, int)
     */
    @Override
    public int read(byte b[], int off, int len) {
      if (b == null) {
        throw new NullPointerException();
      } else if (off < 0 || len < 0 || len > b.length - off) {
        throw new IndexOutOfBoundsException();
      } else if (len == 0) {
        return 0;
      } else if (byteCountRemaining == 0) {
        return -1;
      }
      int actualLen = len > byteCountRemaining ? (int) byteCountRemaining : len;
      for (int i = off; i < actualLen; i++) {
        b[i] = buffer[i % buffer.length];
      }
      byteCountRemaining -= actualLen;
      return actualLen;
    }
  }

  private static class CustomUploadProgressListener implements MediaHttpUploaderProgressListener {
    private final Stopwatch stopwatch = new Stopwatch();

    public CustomUploadProgressListener() {
    }

    @Override
    public void progressChanged(MediaHttpUploader uploader) {
      switch (uploader.getUploadState()) {
        case INITIATION_STARTED:
          stopwatch.start();
          System.out.println("Initiation has started!");
          break;
        case INITIATION_COMPLETE:
          System.out.println("Initiation is complete!");
          break;
        case MEDIA_IN_PROGRESS:
          // TODO(nherring): Progress works iff you have a content length specified.
          //System.out.println(uploader.getProgress());
          System.out.println(uploader.getNumBytesUploaded());
          break;
        case MEDIA_COMPLETE:
          stopwatch.stop();
          System.out.println(String.format("Upload is complete! (%s)", stopwatch));
          break;
        case NOT_STARTED:
          break;
      }
    }
  }

  private static void uploadObject(boolean useCustomMetadata) throws IOException {
    View.header1("Uploading object.");
    final long objectSize = 100 * 1000 * 1000 /* 100 MB */;
    InputStreamContent mediaContent = new InputStreamContent("application/octet-stream",
        new RandomDataBlockInputStream(objectSize, 1024));
    // Not strictly necessary, but allows optimization in the cloud.
    //mediaContent.setLength(OBJECT_SIZE);

    StorageObject objectMetadata = null;

    if (useCustomMetadata) {
      // If you have custom settings for metadata on the object you want to set
      // then you can allocate a StorageObject and set the values here. You can
      // leave out setBucket(), since the bucket is in the insert command's
      // parameters.
      List<ObjectAccessControl> acl = Lists.newArrayList();
      if (settings.getEmail() != null && !settings.getEmail().isEmpty()) {
        acl.add(new ObjectAccessControl()
            .setEntity("user-" + settings.getEmail()).setRole("OWNER"));
      }
      if (settings.getDomain() != null && !settings.getDomain().isEmpty()) {
        acl.add(new ObjectAccessControl()
            .setEntity("domain-" + settings.getDomain()).setRole("READER"));
      }
      objectMetadata = new StorageObject()
          .setName(settings.getPrefix() + "myobject")
          .setMetadata(ImmutableMap.of("key1", "value1", "key2", "value2"))
          .setAcl(acl)
          .setContentDisposition("attachment");
    }

    Storage.Objects.Insert insertObject = storage.objects().insert(settings.getBucket(),
        objectMetadata, mediaContent);

    if (!useCustomMetadata) {
      // If you don't provide metadata, you will have specify the object
      // name by parameter. You will probably also want to ensure that your
      // default object ACLs (a bucket property) are set appropriately:
      // https://developers.google.com/storage/docs/json_api/v1/buckets#defaultObjectAcl
      insertObject.setName(settings.getPrefix() + "myobject");
    }

    insertObject.getMediaHttpUploader().setProgressListener(new CustomUploadProgressListener())
        .setDisableGZipContent(true);
    // For small files, you may wish to call setDirectUploadEnabled(true), to
    // reduce the number of HTTP requests made to the server.
    if (mediaContent.getLength() > 0 && mediaContent.getLength() <= 2 * 1000 * 1000 /* 2MB */) {
      insertObject.getMediaHttpUploader().setDirectUploadEnabled(true);
    }
    insertObject.execute();
  }

  private static class CustomDownloadProgressListener implements MediaHttpDownloaderProgressListener
      {
    private final Stopwatch stopwatch;

    public CustomDownloadProgressListener(final Stopwatch stopwatch) {
      this.stopwatch = stopwatch;
    }

    @Override
    public void progressChanged(MediaHttpDownloader downloader) {
      switch (downloader.getDownloadState()) {
        case MEDIA_IN_PROGRESS:
          System.out.println(downloader.getProgress());
          break;
        case MEDIA_COMPLETE:
          stopwatch.stop();
          System.out.println(String.format("Download is complete! (%s)", stopwatch));
          break;
        case NOT_STARTED:
          break;
      }
    }
  }

  private static void getObjectData() throws IOException {
    View.header1("Getting object data uploaded object.");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Storage.Objects.Get getObject = storage.objects().get(settings.getBucket(),
        settings.getPrefix() + "myobject");

    Stopwatch stopwatch = new Stopwatch();
    getObject.getMediaHttpDownloader().setDirectDownloadEnabled(!IS_APP_ENGINE)
        .setProgressListener(new CustomDownloadProgressListener(stopwatch.start()));
    getObject.executeMediaAndDownloadTo(out);
    System.out.println(new String(Arrays.copyOfRange(out.toByteArray(), 0, 5 * 80)));
    if (out.size() != 5 * 80) {
      System.out.println("...truncated...");
    }
    System.out.println(String.format("Output buffer was size %d.", out.size()));
  }

  private static void getPartialObjectData() throws IOException {
    View.header1("Getting part of object data uploaded object.");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Storage.Objects.Get getObject = storage.objects().get(settings.getBucket(),
        settings.getPrefix() + "myobject");
    getObject.setRequestHeaders(new HttpHeaders()
        .setRange(String.format("bytes=%d-%d", 2 * 1000 * 1000, 3 * 1000 * 1000 - 1 /* 2-3MB */)));

    Stopwatch stopwatch = new Stopwatch();
    getObject.getMediaHttpDownloader().setDirectDownloadEnabled(!IS_APP_ENGINE)
        .setProgressListener(new CustomDownloadProgressListener(stopwatch.start()));
    getObject.executeMediaAndDownloadTo(out);
    System.out.println(new String(Arrays.copyOfRange(out.toByteArray(), 0, 5 * 80)));
    if (out.size() != 5 * 80) {
      System.out.println("...truncated...");
    }
    System.out.println(String.format("Output buffer was size %d.", out.size()));
  }

  // TODO(nherring): get a crc32 implementation, e.g., http://goo.gl/4oOlY
}


