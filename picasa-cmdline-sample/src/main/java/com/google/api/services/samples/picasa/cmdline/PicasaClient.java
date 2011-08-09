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


package com.google.api.services.samples.picasa.cmdline;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
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
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.services.samples.shared.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.oauth2.OAuth2ClientCredentials;
import com.google.api.services.samples.shared.oauth2.OAuth2Native;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public final class PicasaClient {

  private static final String SCOPE = PicasaUrl.ROOT_URL;

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
    if (OAuth2ClientCredentials.CLIENT_ID == null
        || OAuth2ClientCredentials.CLIENT_SECRET == null) {
      System.err.println(
          "Please enter your client ID and secret in " + OAuth2ClientCredentials.class);
      System.exit(1);
    }
    final GoogleAccessProtectedResource accessProtectedResource = OAuth2Native.authorize(transport,
        new JacksonFactory(),
        new LocalServerReceiver(),
        null,
        "google-chrome",
        OAuth2ClientCredentials.CLIENT_ID,
        OAuth2ClientCredentials.CLIENT_SECRET,
        SCOPE);
    final MethodOverride override = new MethodOverride(); // needed for PATCH
    requestFactory = transport.createRequestFactory(new HttpRequestInitializer() {

      @Override
      public void initialize(HttpRequest request) {
        GoogleHeaders headers = new GoogleHeaders();
        headers.setApplicationName("Google-PicasaSample/1.0");
        headers.gdataVersion = "2";
        request.setHeaders(headers);
        request.setInterceptor(new HttpExecuteInterceptor() {

          @Override
          public void intercept(HttpRequest request) throws IOException {
            override.intercept(request);
            accessProtectedResource.intercept(request);
          }
        });
        request.addParser(new AtomParser(DICTIONARY));
        request.setUnsuccessfulResponseHandler(accessProtectedResource);
      }
    });
  }

  public void shutdown() throws IOException {
    transport.shutdown();
  }

  public void executeDeleteEntry(Entry entry) throws IOException {
    HttpRequest request = requestFactory.buildDeleteRequest(new GenericUrl(entry.getEditLink()));
    request.getHeaders().setIfMatch(entry.etag);
    request.execute().ignore();
  }

  Entry executeGetEntry(PicasaUrl url, Class<? extends Entry> entryClass) throws IOException {
    url.fields = GoogleAtom.getFieldsFor(entryClass);
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(entryClass);
  }

  Entry executePatchEntryRelativeToOriginal(Entry updated, Entry original) throws IOException {
    AtomPatchRelativeToOriginalContent content =
        new AtomPatchRelativeToOriginalContent(DICTIONARY, original, updated);
    HttpRequest request =
        requestFactory.buildPatchRequest(new GenericUrl(updated.getEditLink()), content);
    request.getHeaders().setIfMatch(updated.etag);
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

  <F extends Feed> F executeGetFeed(PicasaUrl url, Class<F> feedClass) throws IOException {
    url.fields = GoogleAtom.getFieldsFor(feedClass);
    HttpRequest request = requestFactory.buildGetRequest(url);
    return request.execute().parseAs(feedClass);
  }

  Entry executeInsert(Feed feed, Entry entry) throws IOException {
    AtomContent content = AtomContent.forEntry(DICTIONARY, entry);
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
    GoogleHeaders headers = (GoogleHeaders) request.getHeaders();
    headers.setSlugFromFileName(fileName);
    return request.execute().parseAs(PhotoEntry.class);
  }

  public PhotoEntry executeInsertPhotoEntryWithMetadata(
      PhotoEntry photo, String albumFeedLink, AbstractInputStreamContent content)
      throws IOException {
    HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(albumFeedLink), null);
    AtomContent atomContent = AtomContent.forEntry(DICTIONARY, photo);
    new MultipartRelatedContent(atomContent, content).forRequest(request);
    return request.execute().parseAs(PhotoEntry.class);
  }
}
