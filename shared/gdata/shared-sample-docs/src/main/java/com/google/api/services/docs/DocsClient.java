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

package com.google.api.services.docs;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.services.docs.model.DocumentListFeed;
import com.google.api.services.samples.shared.gdata.xml.GDataXmlClient;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class DocsClient extends GDataXmlClient {

  static final XmlNamespaceDictionary DICTIONARY = new XmlNamespaceDictionary()
      .set("", "http://www.w3.org/2005/Atom")
      .set("app", "http://www.w3.org/2007/app")
      .set("batch", "http://schemas.google.com/gdata/batch")
      .set("docs", "http://schemas.google.com/docs/2007")
      .set("gAcl", "http://schemas.google.com/acl/2007")
      .set("gd", "http://schemas.google.com/g/2005")
      .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
      .set("xml", "http://www.w3.org/XML/1998/namespace");


  public DocsClient(HttpRequestFactory requestFactory) {
    super("3", requestFactory, DICTIONARY);
    setPartialResponse(false);
  }

  <T> T executeGet(DocsUrl url, Class<T> parseAsType) throws IOException {
    return super.executeGet(url, parseAsType);
  }

  public DocumentListFeed executeGetDocumentListFeed(DocsUrl url) throws IOException {
    return executeGet(url, DocumentListFeed.class);
  }
}
