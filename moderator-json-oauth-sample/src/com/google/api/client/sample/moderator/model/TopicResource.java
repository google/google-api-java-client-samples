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
public class TopicResource extends Resource {

  @Key
  public String name;

  @Key
  public String description;

  @Key
  public String presenter;

  @Key
  public Counters counters;

  @Key
  public List<SubmissionResource> submissions;

  /**
   * @param url URL built using {@link ModeratorUrl#forTopics(String)}
   */
  public static List<TopicResource> executeList(
      HttpTransport transport, ModeratorUrl url) throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = url;
    return request.execute().parseAs(TopicList.class).items;
  }

  public static TopicResource executeGet(
      HttpTransport transport, String seriesId, String topicId)
      throws IOException {
    HttpRequest request = transport.buildGetRequest();
    request.url = ModeratorUrl.forTopic(seriesId, topicId);
    return request.execute().parseAs(TopicResource.class);
  }

  public TopicResource executeInsert(HttpTransport transport, String seriesId)
      throws IOException {
    HttpRequest request = transport.buildPostRequest();
    request.content = toContent();
    request.url = ModeratorUrl.forTopics(seriesId);
    return request.execute().parseAs(TopicResource.class);
  }
}
