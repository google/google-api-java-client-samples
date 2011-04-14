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

package com.google.api.client.sample.verification.model;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonHttpContent;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Marshall
 *
 */
public class WebResource extends GenericJson {
  @Key
  public String id;
  
  @Key
  public List<String> owners = new ArrayList<String>();
  
  @Key
  public WebResourceSite site = new WebResourceSite();
  
  public static List<WebResource> executeList(HttpTransport transport)
    throws IOException {
      HttpRequest request = transport.buildGetRequest();
      request.url = VerificationUrl.forWebResource();
      return request.execute().parseAs(WebResourceList.class).items;
  }
  
  public WebResource executeGet(HttpTransport transport)
    throws IOException {
      HttpRequest request = transport.buildGetRequest();
      request.url = VerificationUrl.forWebResource(id);
      return request.execute().parseAs(WebResource.class);
  }
  
  public WebResource executeInsert(HttpTransport transport,
      String verificationMethod) throws IOException {
    HttpRequest request = transport.buildPostRequest();
    setContent(request, this);
    request.url = VerificationUrl.forInsertedWebResource(verificationMethod);
    
    return request.execute().parseAs(WebResource.class);
  }
  
  public void executeDelete(HttpTransport transport) throws IOException {
    HttpRequest request = transport.buildDeleteRequest();
    request.url = VerificationUrl.forWebResource(id);
    
    request.execute().ignore();
  }
  
  public WebResource executeUpdate(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildPutRequest();
    setContent(request, this);
    request.url = VerificationUrl.forWebResource(id);
    
    return request.execute().parseAs(WebResource.class);
  }
  
  public static String getToken(HttpTransport transport, String url,
      String type, String verificationMethod) throws IOException {
    TokenRequest tokRequest = new TokenRequest();
    
    tokRequest.site.identifier = url;
    tokRequest.site.type = type;
    tokRequest.verificationMethod = verificationMethod;
    
    HttpRequest request = transport.buildPostRequest();
    request.url = VerificationUrl.forToken();
    setContent(request, tokRequest);
    
    return request.execute().parseAs(TokenResponse.class).token;
  }
  
  private static void setContent(HttpRequest request, Object obj) {
    JsonHttpContent content = new JsonHttpContent();
    content.data = obj;
    request.content = content;
  }
}
