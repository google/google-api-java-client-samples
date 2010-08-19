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

package com.google.api.client.sample.moderator.model;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.List;

/**
 * @author Yaniv Inbar
 */
public class VoteResource extends Resource {

  @Key
  public String vote;

  @Key
  public String flag;

  /** @param url URL built using {@link ModeratorUrl#forMyVotes(String)}. */
  public static List<VoteResource> executeList(
      HttpTransport transport, ModeratorUrl url) throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = url;
    return request.execute().parseAs(VoteList.class).items;
  }

  public static VoteResource executeGet(
      HttpTransport transport, String seriesId, String submissionId)
      throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = ModeratorUrl.forMyVote(seriesId, submissionId);
    return request.execute().parseAs(VoteResource.class);
  }

  public VoteResource executeInsert(
      HttpTransport transport, String seriesId, String submissionId)
      throws IOException {
    HttpRequest request = transport.buildPostRequest();
    request.content = toContent();
    request.url = ModeratorUrl.forMyVote(seriesId, submissionId);
    return request.execute().parseAs(VoteResource.class);
  }

  public VoteResource executeUpdate(HttpTransport transport)
      throws IOException {
    HttpRequest request = transport.buildPutRequest();
    // hack around moderator bug
    Id id = this.id;
    this.id = null;
    request.content = toContent();
    request.url = ModeratorUrl.forMyVote(id.seriesId, id.submissionId);
    return request.execute().parseAs(VoteResource.class);
  }
}
