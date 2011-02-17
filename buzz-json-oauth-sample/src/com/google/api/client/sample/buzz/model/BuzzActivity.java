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

package com.google.api.client.sample.buzz.model;

import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Key;

import java.io.IOException;

/**
 * Buzz activity, such as a Buzz post.
 *
 * <p>
 * The JSON of a typical activity looks like this:
 *
 * <pre>
 * <code>{
 *  "id": "tag:google.com,2010:buzz:z12puk22ajfyzsz",
 *  "updated": "2010-10-04T16:27:15.169Z",
 *  "object": {
 *   "content": "Hey, this is my first Buzz Post!",
 *   ...
 *  },
 *  ...
 * }</code>
 * </pre>
 *
 * @author Yaniv Inbar
 */
public class BuzzActivity extends GenericJson {

  /** Activity identifier. */
  @Key
  public String id;

  /** Buzz details containing the content of the activity. */
  @Key
  public BuzzObject object;

  /** Last time the activity was updated. */
  @Key
  public DateTime updated;

  /**
   * Post this Buzz Activity.
   *
   * @return posted Buzz Activity response from the Buzz server
   * @throws IOException any I/O exception
   */
  public BuzzActivity post() throws IOException {
    HttpRequest request = Util.TRANSPORT.buildPostRequest();
    request.url = BuzzUrl.forMyActivityFeed();
    request.content = toContent();
    return request.execute().parseAs(BuzzActivity.class);
  }

  /**
   * Update this Buzz Activity.
   *
   * @return updated Buzz Activity response from the Buzz server
   * @throws IOException any I/O exception
   */
  public BuzzActivity update() throws IOException {
    HttpRequest request = Util.TRANSPORT.buildPutRequest();
    request.url = BuzzUrl.forMyActivity(this.id);
    request.content = toContent();
    return request.execute().parseAs(BuzzActivity.class);
  }

  /**
   * Post this Buzz Activity.
   *
   * @throws IOException any I/O exception
   */
  public void delete() throws IOException {
    HttpRequest request = Util.TRANSPORT.buildDeleteRequest();
    request.url = BuzzUrl.forMyActivity(this.id);
    request.execute().ignore();
  }

  /** Returns a new JSON-C content serializer for this Buzz activity. */
  private JsonCContent toContent() {
    JsonCContent result = new JsonCContent();
    result.data = this;
    result.jsonFactory = Util.JSON_FACTORY;
    return result;
  }
}
