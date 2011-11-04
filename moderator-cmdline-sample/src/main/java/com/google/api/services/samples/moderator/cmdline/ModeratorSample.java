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

package com.google.api.services.samples.moderator.cmdline;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.moderator.Moderator;
import com.google.api.services.moderator.ModeratorRequest;
import com.google.api.services.moderator.model.Series;
import com.google.api.services.moderator.model.Submission;
import com.google.api.services.moderator.model.SubmissionAttribution;
import com.google.api.services.moderator.model.Topic;
import com.google.api.services.moderator.model.Vote;
import com.google.api.services.moderator.model.VoteList;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2ClientCredentials;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class ModeratorSample {

  /** OAuth 2 scope. */
  private static final String SCOPE = "https://www.googleapis.com/auth/moderator";

  private static void run(JsonFactory jsonFactory) throws Exception {
    // authorization
    HttpTransport transport = new NetHttpTransport();
    GoogleAccessProtectedResource accessProtectedResource = OAuth2Native.authorize(transport,
        jsonFactory,
        new LocalServerReceiver(),
        null,
        "google-chrome",
        OAuth2ClientCredentials.CLIENT_ID,
        OAuth2ClientCredentials.CLIENT_SECRET,
        SCOPE);

    // set up Moderator
    Moderator moderator = Moderator.builder(transport, jsonFactory)
        .setApplicationName("Google-ModeratorSample/1.0")
        .setHttpRequestInitializer(accessProtectedResource)
        .setJsonHttpRequestInitializer(new JsonHttpRequestInitializer() {
          @Override
          public void initialize(JsonHttpRequest request) {
            ModeratorRequest moderatorRequest = (ModeratorRequest) request;
            moderatorRequest.setPrettyPrint(true);
          }
        })
        .build();

    Series series = createSeries(moderator);
    long seriesId = series.getId().getSeriesId();
    Topic topic = createTopic(moderator, seriesId);
    long topicId = topic.getId().getTopicId();
    Submission submission = createSubmission(moderator, seriesId, topicId);
    long submissionId = submission.getId().getSubmissionId();
    Vote vote = createVote(moderator, seriesId, submissionId);
    updateVote(moderator, seriesId, submissionId, vote);
    printVote(moderator, seriesId);
  }

  public static void main(String[] args) {
    JsonFactory jsonFactory = new JacksonFactory();
    try {
      try {
        OAuth2ClientCredentials.errorIfNotSpecified();
        run(jsonFactory);
        // success!
        return;
      } catch (HttpResponseException e) {
        if (!Json.CONTENT_TYPE.equals(e.getResponse().getContentType())) {
          System.err.println(e.getResponse().parseAsString());
        } else {
          GoogleJsonError errorResponse = GoogleJsonError.parse(jsonFactory, e.getResponse());
          System.err.println(errorResponse.code + " Error: " + errorResponse.message);
          for (ErrorInfo error : errorResponse.errors) {
            System.err.println(jsonFactory.toString(error));
          }
        }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static Series createSeries(Moderator moderator)
      throws IOException {
    // setup series
    Series series = new Series();
    series.setDescription("Share and rank tips for eating healthily on the cheaps!");
    series.setName("Eating Healthy & Cheap");
    // insert the series
    Moderator.Series.Insert request = moderator.series().insert(series);
    return request.execute();
  }

  private static Topic createTopic(
      Moderator moderator, long seriesId) throws IOException {
    // setup topic
    Topic topic = new Topic();
    topic.setDescription("Share your ideas on eating healthy!");
    topic.setName("Ideas");
    topic.setPresenter("liz");
    // insert the topic
    Moderator.Topics.Insert request = moderator.topics().insert(seriesId, topic);
    return request.execute();
  }

  private static Submission createSubmission(
      Moderator moderator, long seriesId, long topicId) throws IOException {
    // setup submission
    Submission submission = new Submission();
    submission.setAttachmentUrl("http://www.youtube.com/watch?v=1a1wyc5Xxpg");
    submission.setText("Charlie Ayers @ Google");
    // setup attribution
    SubmissionAttribution attribution = new SubmissionAttribution();
    attribution.setDisplayName("Bashan");
    attribution.setLocation("Bainbridge Island, WA");
    submission.setAttribution(attribution);
    // insert the submission
    Moderator.Submissions.Insert request = moderator.submissions().insert(
        seriesId, topicId, submission);
    return request.execute();
  }

  private static Vote createVote(
      Moderator moderator, long seriesId, long submissionId)
      throws IOException {
    // setup vote
    Vote vote = new Vote();
    vote.setVote("PLUS");
    // insert the vote
    Moderator.Votes.Insert request = moderator.votes().insert(seriesId, submissionId, vote);
    return request.execute();
  }

  private static Vote updateVote(
      Moderator moderator, Long seriesId, Long submissionId, Vote vote) throws IOException {
    vote.setVote("MINUS");
    // update the vote
    Moderator.Votes.Update request = moderator.votes().update(seriesId, submissionId, vote);
    return request.execute();
  }

  private static void printVote(Moderator moderator, long seriesId) throws IOException {
    Moderator.Votes.List request = moderator.votes().list(seriesId);
    VoteList voteList = request.execute();
    for (Vote vote : voteList.getItems()) {
      System.out.println("Vote is: " + vote.getVote());
    }
  }
}
