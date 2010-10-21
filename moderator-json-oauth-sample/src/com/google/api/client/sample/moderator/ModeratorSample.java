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

package com.google.api.client.sample.moderator;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.moderator.model.Attribution;
import com.google.api.client.sample.moderator.model.Debug;
import com.google.api.client.sample.moderator.model.SeriesResource;
import com.google.api.client.sample.moderator.model.SubmissionResource;
import com.google.api.client.sample.moderator.model.TopicResource;
import com.google.api.client.sample.moderator.model.VoteResource;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class ModeratorSample {

  public static void main(String[] args) {
    Debug.enableLogging();
    HttpTransport transport = setUpTransport();
    try {
      try {
        Auth.authorize(transport);
        SeriesResource series = createSeries(transport);
        TopicResource topic = createTopic(transport, series);
        SubmissionResource submission = createSubmission(transport, topic);
        VoteResource vote = createVote(transport, submission);
        updateVote(transport, vote);
        Auth.revoke();
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }
  }

  private static HttpTransport setUpTransport() {
    HttpTransport transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("Google-ModeratorSample/1.0");
    transport.addParser(new JsonCParser());
    return transport;
  }

  private static SeriesResource createSeries(HttpTransport transport)
      throws IOException {
    SeriesResource resource = new SeriesResource();
    resource.description =
        "Share and rank tips for eating healthily on the cheaps!";
    resource.name = "Eating Healthy & Cheap";
    SeriesResource result = resource.executeInsert(transport);
    System.out.println(result);
    return result;
  }

  private static TopicResource createTopic(
      HttpTransport transport, SeriesResource series) throws IOException {
    TopicResource topic = new TopicResource();
    topic.description = "Share your ideas on eating healthy!";
    topic.name = "Ideas";
    topic.presenter = "liz";
    TopicResource result = topic.executeInsert(transport, series.id.seriesId);
    System.out.println(result);
    return result;
  }

  private static SubmissionResource createSubmission(
      HttpTransport transport, TopicResource topic) throws IOException {
    SubmissionResource submission = new SubmissionResource();
    submission.attachmentUrl = "http://www.youtube.com/watch?v=1a1wyc5Xxpg";
    submission.text = "Charlie Ayers @ Google";
    Attribution attribution = new Attribution();
    attribution.displayName = "Bashan";
    attribution.location = "Bainbridge Island, WA";
    SubmissionResource result = submission.executeInsert(
        transport, topic.id.seriesId, topic.id.topicId);
    System.out.println(result);
    return result;
  }

  private static VoteResource createVote(
      HttpTransport transport, SubmissionResource submission)
      throws IOException {
    VoteResource vote = new VoteResource();
    vote.vote = "PLUS";
    VoteResource result = vote.executeInsert(
        transport, submission.id.seriesId, submission.id.submissionId);
    System.out.println(result);
    return result;
  }

  private static VoteResource updateVote(
      HttpTransport transport, VoteResource vote) throws IOException {
    vote.vote = "MINUS";
    VoteResource result = vote.executeUpdate(transport);
    System.out.println(result);
    return result;
  }
}
