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

package com.google.api.services.samples.picasa.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.picasa.PicasaClient;
import com.google.api.services.picasa.PicasaUrl;
import com.google.api.services.picasa.model.AlbumEntry;
import com.google.api.services.picasa.model.AlbumFeed;
import com.google.api.services.picasa.model.PhotoEntry;
import com.google.api.services.picasa.model.UserFeed;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class PicasaSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";
  
  /** Global instance of the HTTP transport. */
  private static HttpTransport HTTP_TRANSPORT;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(PicasaSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
          + "into picasa-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"), ".credentials/picasa.json"), JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
        Collections.singleton(PicasaUrl.ROOT_URL)).setCredentialStore(credentialStore).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public static void main(String[] args) {
    try {
      HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
      Credential credential = authorize();
      PicasaClient client = new PicasaClient(HTTP_TRANSPORT.createRequestFactory(credential));
      client.setApplicationName(APPLICATION_NAME);
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

  public static void run(PicasaClient client) throws IOException, InterruptedException {
    UserFeed feed = showAlbums(client);
    AlbumEntry album = postAlbum(client, feed);
    postPhoto(client, album);
    // postVideo(client, album);

    // The server will update the e-tag of the album multiple times
    // Wait for the latest version ...
    Thread.sleep(1000);

    album = getUpdatedAlbum(client, album);
    album = updateTitle(client, album);
    deleteAlbum(client, album);
  }

  private static UserFeed showAlbums(PicasaClient client) throws IOException {
    // build URL for the default user feed of albums
    PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
    // execute GData request for the feed
    UserFeed feed = client.executeGetUserFeed(url);
    System.out.println("User: " + feed.author.name);
    System.out.println("Total number of albums: " + feed.totalResults);
    // show albums
    if (feed.albums != null) {
      for (AlbumEntry album : feed.albums) {
        showAlbum(client, album);
      }
    }
    return feed;
  }

  private static void showAlbum(PicasaClient client, AlbumEntry album) throws IOException {
    System.out.println();
    System.out.println("-----------------------------------------------");
    System.out.println("Album title: " + album.title);
    System.out.println("Updated: " + album.updated);
    System.out.println("Album ETag: " + album.etag);
    if (album.summary != null) {
      System.out.println("Description: " + album.summary);
    }
    if (album.numPhotos != 0) {
      System.out.println("Total number of photos: " + album.numPhotos);
      PicasaUrl url = new PicasaUrl(album.getFeedLink());
      AlbumFeed feed = client.executeGetAlbumFeed(url);
      for (PhotoEntry photo : feed.photos) {
        System.out.println();
        System.out.println("Photo title: " + photo.title);
        if (photo.summary != null) {
          System.out.println("Photo description: " + photo.summary);
        }
        System.out.println("Image MIME type: " + photo.mediaGroup.content.type);
        System.out.println("Image URL: " + photo.mediaGroup.content.url);
      }
    }
  }

  private static AlbumEntry postAlbum(PicasaClient client, UserFeed feed) throws IOException {
    System.out.println();
    AlbumEntry newAlbum = new AlbumEntry();
    newAlbum.access = "private";
    newAlbum.title = "A new album";
    newAlbum.summary = "My favorite photos";
    AlbumEntry album = client.executeInsert(feed, newAlbum);
    showAlbum(client, album);
    return album;
  }

  private static PhotoEntry postPhoto(PicasaClient client, AlbumEntry album) throws IOException {
    String fileName = "picasaweblogo-en_US.gif";
    String photoUrlString = "http://www.google.com/accounts/lh2/" + fileName;
    InputStreamContent content =
        new InputStreamContent("image/jpeg", new URL(photoUrlString).openStream());
    PhotoEntry photo =
        client.executeInsertPhotoEntry(new PicasaUrl(album.getFeedLink()), content, fileName);
    System.out.println("Posted photo: " + photo.title);
    return photo;
  }

  @SuppressWarnings("unused")
  private static PhotoEntry postVideo(PicasaClient client, AlbumEntry album) throws IOException {
    // NOTE: this video is not included in the sample
    File file = new File("myvideo.3gp");
    FileContent imageContent = new FileContent("video/3gpp", file);
    PhotoEntry video = new PhotoEntry();
    video.title = file.getName();
    video.summary = "My video";
    PhotoEntry result = client.executeInsertPhotoEntryWithMetadata(
        video, new PicasaUrl(album.getFeedLink()), imageContent);
    System.out.println("Posted video (pending processing): " + result.title);
    return result;
  }

  private static AlbumEntry getUpdatedAlbum(PicasaClient client, AlbumEntry album)
      throws IOException {
    album = client.executeGetAlbum(album.getSelfLink());
    showAlbum(client, album);
    return album;
  }

  private static AlbumEntry updateTitle(PicasaClient client, AlbumEntry album) throws IOException {
    AlbumEntry patched = album.clone();
    patched.title = "My favorite web logos";
    album = client.executePatchRelativeToOriginal(album, patched);
    showAlbum(client, album);
    return album;
  }

  private static void deleteAlbum(PicasaClient client, AlbumEntry album) throws IOException {
    client.executeDelete(album);
    System.out.println();
    System.out.println("Album deleted.");
  }
}
