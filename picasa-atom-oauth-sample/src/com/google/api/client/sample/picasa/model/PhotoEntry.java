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

package com.google.api.client.sample.picasa.model;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.MultipartRelatedContent;
import com.google.api.client.util.Key;
import com.google.api.client.xml.atom.AtomContent;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class PhotoEntry extends Entry {

  @Key
  public Category category = Category.newKind("photo");

  @Key("media:group")
  public MediaGroup mediaGroup;

  public static PhotoEntry executeInsert(HttpTransport transport,
      String albumFeedLink, InputStreamContent content, String fileName)
      throws IOException {
    HttpRequest request = transport.buildPostRequest();
    request.setUrl(albumFeedLink);
    GoogleHeaders headers = (GoogleHeaders) request.headers;
    headers.setSlugFromFileName(fileName);
    request.content = content;
    return request.execute().parseAs(PhotoEntry.class);
  }

  public PhotoEntry executeInsertWithMetadata(
      HttpTransport transport, String albumFeedLink, InputStreamContent content)
      throws IOException {
    HttpRequest request = transport.buildPostRequest();
    request.setUrl(albumFeedLink);
    AtomContent atomContent = new AtomContent();
    atomContent.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
    atomContent.entry = this;
    MultipartRelatedContent multiPartContent =
        MultipartRelatedContent.forRequest(request);
    multiPartContent.parts.add(atomContent);
    multiPartContent.parts.add(content);
    request.content = multiPartContent;
    return request.execute().parseAs(PhotoEntry.class);
  }
}
