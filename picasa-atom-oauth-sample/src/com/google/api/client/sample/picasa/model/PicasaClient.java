/*
 * Copyright (c) 2011 Google Inc.
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


package com.google.api.client.sample.picasa.model;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.googleapis.xml.atom.AtomPatchRelativeToOriginalContent;
import com.google.api.client.googleapis.xml.atom.GoogleAtom;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.MultipartRelatedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public final class PicasaClient {

  static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary()
      .set("", "http://www.w3.org/2005/Atom")
      .set("exif", "http://schemas.google.com/photos/exif/2007")
      .set("gd", "http://schemas.google.com/g/2005")
      .set("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#")
      .set("georss", "http://www.georss.org/georss")
      .set("gml", "http://www.opengis.net/gml")
      .set("gphoto", "http://schemas.google.com/photos/2007")
      .set("media", "http://search.yahoo.com/mrss/")
      .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
      .set("xml", "http://www.w3.org/XML/1998/namespace");


  private final HttpTransport transport = new NetHttpTransport();

  private HttpRequestFactory requestFactory;

  public void authorize() throws Exception {
    final OAuthParameters parameters = Auth.authorize(transport); // OAuth
    final MethodOverride override = new MethodOverride(); // needed for PATCH
    requestFactory = transport.createRequestFactory(new HttpRequestInitializer() {

      @Override
      public void initialize(HttpRequest request) {
        GoogleHeaders headers = new GoogleHeaders();
        headers.setApplicationName("Google-PicasaSample/1.0");
        headers.gdataVersion = "2";
        request.headers = headers;
        request.interceptor = new HttpExecuteInterceptor() {

          @Override
          public void intercept(HttpRequest request) throws IOException {
            override.intercept(request);
            parameters.intercept(request);
          }
        };
        AtomParser parser = new AtomParser();
        parser.namespaceDictionary = DICTIONARY;
        request.addParser(parser);
      }
    });
  }

  public void shutdown() throws IOException {
    transport.shutdown();
    Auth.revoke(transport);
  }

  public void executeDeleteEntry(Entry entry) throws IOException {
    HttpRequest request = requestFactory.buildDeleteRequest(new GenericUrl(entry.getEditLink()));
    request.headers.ifMatch = entry.etag;
    request.execute().ignore();
  }

  Entry executeGetEntry(PicasaUrl url, Class<? extends Entry> entryClass) throws IOException {
    url.fields = GoogleAtom.getFieldsFor(entryClass);
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(entryClass);
  }

  Entry executePatchEntryRelativeToOriginal(Entry updated, Entry original) throws IOException {
    AtomPatchRelativeToOriginalContent content = new AtomPatchRelativeToOriginalContent();
    content.namespaceDictionary = DICTIONARY;
    content.originalEntry = original;
    content.patchedEntry = updated;
    HttpRequest request =
        requestFactory.buildPatchRequest(new GenericUrl(updated.getEditLink()), content);
    request.headers.ifMatch = updated.etag;
    return request.execute().parseAs(updated.getClass());
  }

  public AlbumEntry executeGetAlbum(String link) throws IOException {
    PicasaUrl url = new PicasaUrl(link);
    return (AlbumEntry) executeGetEntry(url, AlbumEntry.class);
  }

  public AlbumEntry executePatchAlbumRelativeToOriginal(AlbumEntry updated, AlbumEntry original)
      throws IOException {
    return (AlbumEntry) executePatchEntryRelativeToOriginal(updated, original);
  }

  <F extends Feed> F executeGetFeed(PicasaUrl url, Class<F> feedClass)
      throws IOException {
    url.fields = GoogleAtom.getFieldsFor(feedClass);
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(feedClass);
  }

  Entry executeInsert(Feed feed, Entry entry) throws IOException {
    AtomContent content = new AtomContent();
    content.namespaceDictionary = DICTIONARY;
    content.entry = entry;
    HttpRequest request =
        requestFactory.buildPostRequest(new GenericUrl(feed.getPostLink()), content);
    return request.execute().parseAs(entry.getClass());
  }

  public AlbumFeed executeGetAlbumFeed(PicasaUrl url) throws IOException {
    url.kinds = "photo";
    url.maxResults = 5;
    return executeGetFeed(url, AlbumFeed.class);
  }

  public UserFeed executeGetUserFeed(PicasaUrl url) throws IOException {
    url.kinds = "album";
    url.maxResults = 3;
    return executeGetFeed(url, UserFeed.class);
  }

  public AlbumEntry insertAlbum(UserFeed userFeed, AlbumEntry entry) throws IOException {
    return (AlbumEntry) executeInsert(userFeed, entry);
  }

  public PhotoEntry executeInsertPhotoEntry(
      String albumFeedLink, InputStreamContent content, String fileName) throws IOException {
    HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(albumFeedLink), content);
    GoogleHeaders headers = (GoogleHeaders) request.headers;
    headers.setSlugFromFileName(fileName);
    return request.execute().parseAs(PhotoEntry.class);
  }

  public PhotoEntry executeInsertPhotoEntryWithMetadata(
      PhotoEntry photo, String albumFeedLink, AbstractInputStreamContent content)
      throws IOException {
    HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(albumFeedLink), null);
    AtomContent atomContent = new AtomContent();
    atomContent.namespaceDictionary = DICTIONARY;
    atomContent.entry = photo;
    MultipartRelatedContent multiPartContent = MultipartRelatedContent.forRequest(request);
    multiPartContent.parts.add(atomContent);
    multiPartContent.parts.add(content);
    request.content = multiPartContent;
    return request.execute().parseAs(PhotoEntry.class);
  }
}
