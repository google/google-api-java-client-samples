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

import com.google.api.client.googleapis.xml.atom.AtomPatchRelativeToOriginalContent;
import com.google.api.client.googleapis.xml.atom.GData;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.DataUtil;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.List;

/**
 * @author Yaniv Inbar
 */
public class Entry implements Cloneable {

  @Key("@gd:etag")
  public String etag;

  @Key("link")
  public List<Link> links;

  @Key
  public String summary;

  @Key
  public String title;

  @Key
  public String updated;

  public String getFeedLink() {
    return Link.find(links, "http://schemas.google.com/g/2005#feed");
  }

  public String getSelfLink() {
    return Link.find(links, "self");
  }

  @Override
  protected Entry clone() {
    return DataUtil.clone(this);
  }

  public void executeDelete(HttpTransport transport) throws IOException {
    HttpRequest request = transport.buildDeleteRequest();
    request.setUrl(getEditLink());
    request.headers.ifMatch = etag;
    request.execute().ignore();
  }

  static Entry executeGet(HttpTransport transport, PicasaUrl url,
      Class<? extends Entry> entryClass) throws IOException {
    url.fields = GData.getFieldsFor(entryClass);
    HttpRequest request = transport.buildGetRequest();
    request.url = url;
    return request.execute().parseAs(entryClass);
  }

  Entry executePatchRelativeToOriginal(HttpTransport transport, Entry original)
      throws IOException {
    HttpRequest request = transport.buildPatchRequest();
    request.setUrl(getEditLink());
    request.headers.ifMatch = etag;
    AtomPatchRelativeToOriginalContent content =
        new AtomPatchRelativeToOriginalContent();
    content.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
    content.originalEntry = original;
    content.patchedEntry = this;
    request.content = content;
    return request.execute().parseAs(getClass());
  }

  private String getEditLink() {
    return Link.find(links, "edit");
  }
}
