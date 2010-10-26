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

package com.google.api.client.sample.buzz.model;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Buzz activity feed.
 * 
 * <p>
 * The JSON of a typical activity feed looks like this:
 * 
 * <pre>
 * <code>{
 * "data": {
 *   "items": [
 *    {
 *     "id": "tag:google.com,2010:buzz:z12puk22ajfyzsz",
 *     "object": {
 *      "content": "Hey, this is my first Buzz Post!",
 *      ...
 *     },
 *     ...
 *    }
 *   ]
 *  ]
 * }
 * }</code>
 * </pre>
 * 
 * @author Yaniv Inbar
 */
public class BuzzActivityFeed {

  /** List of activities. */
  @Key
  public List<BuzzActivity> items = new ArrayList<BuzzActivity>();

  /**
   * List the user's Buzz activities.
   * 
   * @param transport Google transport
   * @return Buzz activities feed response from the Buzz server
   * @throws IOException any I/O exception
   */
  public static BuzzActivityFeed list(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = BuzzUrl.forMyActivityFeed();
    return request.execute().parseAs(BuzzActivityFeed.class);
  }
}
