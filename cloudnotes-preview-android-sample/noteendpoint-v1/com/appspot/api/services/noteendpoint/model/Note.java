/*
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
/*
 * Warning! This file is generated. Modify at your own risk.
 */

package com.appspot.api.services.noteendpoint.model;


import com.google.api.client.http.HttpHeaders;
import com.google.api.client.json.GenericJson;


/**
 * Model definition for Note.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the . For a detailed explanation see:
 * <a href="http://code.google.com/p/google-api-java-client/wiki/Json">http://code.google.com/p/google-api-java-client/wiki/Json</a>
 * </p>
 *
 * @author Google, Inc.
 */
public final class Note extends GenericJson {


  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String emailAddress;



  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String description;



  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private String id;




  /**

   * The value returned may be {@code null}.
   */
  public String getEmailAddress() {
    return emailAddress;
  }

  /**

   * The value set may be {@code null}.
   */
  public Note setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public String getDescription() {
    return description;
  }

  /**

   * The value set may be {@code null}.
   */
  public Note setDescription(String description) {
    this.description = description;
    return this;
  }

  /**

   * The value returned may be {@code null}.
   */
  public String getId() {
    return id;
  }

  /**

   * The value set may be {@code null}.
   */
  public Note setId(String id) {
    this.id = id;
    return this;
  }


  private HttpHeaders responseHeaders;

  /**
   * Sets the HTTP headers returned with the server response, or <code>null</code>.
   *
   * This member should only be non-null if this object was the top level element of a response. For
   * example, a request that returns a single {@link Note} would include the response headers, while
   * a request which returns an array of {@link Note}, would have a non-null response header in the
   * enclosing object only.
   */
  public void setResponseHeaders(HttpHeaders responseHeaders) {
    this.responseHeaders = responseHeaders;
  }

  /**
   * Returns the HTTP headers that were returned with the server response, or
   * <code>null</code>.
   */
  public HttpHeaders getResponseHeaders() {
    return responseHeaders;
  }



}
