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

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;

/**
 * Moderator URL builder.
 *
 * @author Yaniv Inbar
 */
public final class ModeratorUrl extends GoogleUrl {

  @Key("start-index")
  public Integer startIndex;

  @Key("max-results")
  public Integer maxResults;

  @Key
  public String sort;
  
  @Key
  public Boolean hasAttachedVideo;
  
  @Key
  public String author;
  
  @Key
  public String q;
  
  /** Constructs a new Moderator URL from the given encoded URI. */
  public ModeratorUrl(String encodedUrl) {
    super(encodedUrl);
    if (Debug.ENABLED) {
      prettyprint = true;
    }
  }

  public static ModeratorUrl forSeries() {
    return new ModeratorUrl("https://www.googleapis.com/moderator/v1/series");
  }

  public static ModeratorUrl forSeries(String seriesId) {
    ModeratorUrl result = forSeries();
    result.pathParts.add(seriesId);
    return result;
  }

  public static ModeratorUrl forTopics(String seriesId) {
    ModeratorUrl result = forSeries(seriesId);
    result.pathParts.add("topics");
    return result;
  }

  public static ModeratorUrl forTopic(String seriesId, String topicId) {
    ModeratorUrl result = forTopics(seriesId);
    result.pathParts.add(topicId);
    return result;
  }

  public static ModeratorUrl forSubmissions(String seriesId) {
    ModeratorUrl result = forSeries(seriesId);
    result.pathParts.add("submissions");
    return result;
  }

  public static ModeratorUrl forSubmissions(String seriesId, String topicId) {
    ModeratorUrl result = forTopic(seriesId, topicId);
    result.pathParts.add("submissions");
    return result;
  }

  public static ModeratorUrl forSubmission(
      String seriesId, String submissionId) {
    ModeratorUrl result = forSubmissions(seriesId);
    result.pathParts.add(submissionId);
    return result;
  }

  public static ModeratorUrl forMyVotes(String seriesId) {
    ModeratorUrl result = forSeries(seriesId);
    result.pathParts.add("votes");
    result.pathParts.add("@me");
    return result;
  }

  public static ModeratorUrl forMyVote(
      String seriesId, String submissionId) {
    ModeratorUrl result = forSubmission(seriesId, submissionId);
    result.pathParts.add("votes");
    result.pathParts.add("@me");
    return result;
  }
}
