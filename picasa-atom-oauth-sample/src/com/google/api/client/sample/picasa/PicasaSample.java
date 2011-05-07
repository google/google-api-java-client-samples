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

package com.google.api.client.sample.picasa;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.sample.picasa.model.AlbumEntry;
import com.google.api.client.sample.picasa.model.AlbumFeed;
import com.google.api.client.sample.picasa.model.PhotoEntry;
import com.google.api.client.sample.picasa.model.PicasaClient;
import com.google.api.client.sample.picasa.model.PicasaUrl;
import com.google.api.client.sample.picasa.model.UserFeed;
import com.google.api.client.sample.picasa.model.Util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Yaniv Inbar
 */
public class PicasaSample {

  public static void main(String[] args) {
    Util.enableLogging();
    PicasaClient client = new PicasaClient();
    try {
      client.authorize();
      try {
        UserFeed feed = showAlbums(client);
        AlbumEntry album = postAlbum(client, feed);
        postPhoto(client, album);
        // postVideo(client, album);
        album = getUpdatedAlbum(client, album);
        album = updateTitle(client, album);
        deleteAlbum(client, album);
        shutdown(client);
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      shutdown(client);
      System.exit(1);
    }
  }

  private static void shutdown(PicasaClient client) {
    try {
      client.shutdown();
    } catch (IOException e) {
      e.printStackTrace();
    }
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
    AlbumEntry album = client.insertAlbum(feed, newAlbum);
    showAlbum(client, album);
    return album;
  }

  private static PhotoEntry postPhoto(PicasaClient client, AlbumEntry album) throws IOException {
    String fileName = "picasaweblogo-en_US.gif";
    String photoUrlString = "http://www.google.com/accounts/lh2/" + fileName;
    InputStreamContent content = new InputStreamContent();
    content.inputStream = new URL(photoUrlString).openStream();
    content.type = "image/jpeg";
    PhotoEntry photo = client.executeInsertPhotoEntry(album.getFeedLink(), content, fileName);
    System.out.println("Posted photo: " + photo.title);
    return photo;
  }

  @SuppressWarnings("unused")
  private static PhotoEntry postVideo(PicasaClient client, AlbumEntry album) throws IOException {
    // NOTE: this video is not included in the sample
    File file = new File("myvideo.3gp");
    FileContent imageContent = new FileContent(file);
    imageContent.type = "video/3gpp";
    PhotoEntry video = new PhotoEntry();
    video.title = file.getName();
    video.summary = "My video";
    PhotoEntry result =
        client.executeInsertPhotoEntryWithMetadata(video, album.getFeedLink(), imageContent);
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
    album = client.executePatchAlbumRelativeToOriginal(patched, album);
    showAlbum(client, album);
    return album;
  }

  private static void deleteAlbum(PicasaClient client, AlbumEntry album) throws IOException {
    client.executeDeleteEntry(album);
    System.out.println();
    System.out.println("Album deleted.");
  }
}
