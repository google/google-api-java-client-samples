/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.picasa;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.sample.picasa.model.AlbumEntry;
import com.google.api.client.sample.picasa.model.AlbumFeed;
import com.google.api.client.sample.picasa.model.PhotoEntry;
import com.google.api.client.sample.picasa.model.PicasaUrl;
import com.google.api.client.sample.picasa.model.UserFeed;
import com.google.api.client.sample.picasa.model.Util;
import com.google.api.client.xml.atom.AtomParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author Yaniv Inbar
 */
public class PicasaSample {

  public static void main(String[] args) {
    Util.enableLogging();
    try {
      try {
        HttpTransport transport = setUpTransport();
        Auth.authorize(transport);
        UserFeed feed = showAlbums(transport);
        AlbumEntry album = postAlbum(transport, feed);
        postPhoto(transport, album);
        // postVideo(transport, album);
        album = getUpdatedAlbum(transport, album);
        album = updateTitle(transport, album);
        deleteAlbum(transport, album);
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }
  }

  private static HttpTransport setUpTransport() {
    HttpTransport transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("Google-PicasaSample/1.0");
    headers.gdataVersion = "2";
    AtomParser parser = new AtomParser();
    parser.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
    transport.addParser(parser);
    return transport;
  }

  private static UserFeed showAlbums(HttpTransport transport)
      throws IOException {
    // build URL for the default user feed of albums
    PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
    // execute GData request for the feed
    UserFeed feed = UserFeed.executeGet(transport, url);
    System.out.println("User: " + feed.author.name);
    System.out.println("Total number of albums: " + feed.totalResults);
    // show albums
    if (feed.albums != null) {
      for (AlbumEntry album : feed.albums) {
        showAlbum(transport, album);
      }
    }
    return feed;
  }

  private static void showAlbum(HttpTransport transport, AlbumEntry album)
      throws IOException {
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
      AlbumFeed feed = AlbumFeed.executeGet(transport, url);
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

  private static AlbumEntry postAlbum(HttpTransport transport, UserFeed feed)
      throws IOException {
    System.out.println();
    AlbumEntry newAlbum = new AlbumEntry();
    newAlbum.access = "private";
    newAlbum.title = "A new album";
    newAlbum.summary = "My favorite photos";
    AlbumEntry album = feed.insertAlbum(transport, newAlbum);
    showAlbum(transport, album);
    return album;
  }

  private static PhotoEntry postPhoto(HttpTransport transport, AlbumEntry album)
      throws IOException {
    String fileName = "picasaweblogo-en_US.gif";
    String photoUrlString = "http://www.google.com/accounts/lh2/" + fileName;
    InputStreamContent content = new InputStreamContent();
    content.inputStream = new URL(photoUrlString).openStream();
    content.type = "image/jpeg";
    PhotoEntry photo = PhotoEntry.executeInsert(
        transport, album.getFeedLink(), content, fileName);
    System.out.println("Posted photo: " + photo.title);
    return photo;
  }

  private static PhotoEntry postVideo(HttpTransport transport, AlbumEntry album)
      throws IOException {
    InputStreamContent imageContent = new InputStreamContent();
    // NOTE: this video is not included in the sample
    File file = new File("myvideo.3gp");
    imageContent.setFileInput(file);
    imageContent.type = "video/3gpp";
    PhotoEntry video = new PhotoEntry();
    video.title = file.getName();
    video.summary = "My video";
    PhotoEntry result = video.executeInsertWithMetadata(
        transport, album.getFeedLink(), imageContent);
    System.out.println("Posted video (pending processing): " + result.title);
    return result;
  }

  private static AlbumEntry getUpdatedAlbum(
      HttpTransport transport, AlbumEntry album) throws IOException {
    album = AlbumEntry.executeGet(transport, album.getSelfLink());
    showAlbum(transport, album);
    return album;
  }

  private static AlbumEntry updateTitle(
      HttpTransport transport, AlbumEntry album) throws IOException {
    AlbumEntry patched = album.clone();
    patched.title = "My favorite web logos";
    album = patched.executePatchRelativeToOriginal(transport, album);
    showAlbum(transport, album);
    return album;
  }

  private static void deleteAlbum(HttpTransport transport, AlbumEntry album)
      throws IOException {
    album.executeDelete(transport);
    System.out.println();
    System.out.println("Album deleted.");
  }
}
