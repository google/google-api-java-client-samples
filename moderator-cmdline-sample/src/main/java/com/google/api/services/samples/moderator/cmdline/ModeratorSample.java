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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.moderator.Moderator;
import com.google.api.services.moderator.ModeratorRequest;
import com.google.api.services.moderator.ModeratorRequestInitializer;
import com.google.api.services.moderator.ModeratorScopes;
import com.google.api.services.moderator.model.Series;
import com.google.api.services.moderator.model.Submission;
import com.google.api.services.moderator.model.Topic;
import com.google.api.services.moderator.model.Vote;
import com.google.api.services.moderator.model.VoteList;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class ModeratorSample {

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, ModeratorSample.class.getResourceAsStream("/client_secrets.json"));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=moderator "
          + "into moderator-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"), ".credentials/moderator.json"), JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets,
        Collections.singleton(ModeratorScopes.MODERATOR)).setCredentialStore(credentialStore).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  private static void run() throws Exception {
    // authorization
    Credential credential = authorize();

    // set up Moderator
    Moderator moderator = new Moderator.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Google-ModeratorSample/1.0")
        .setGoogleClientRequestInitializer(new ModeratorRequestInitializer() {
            @Override
          public void initializeModeratorRequest(ModeratorRequest<?> request) {
            request.setPrettyPrint(true);
          }
        }).build();

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
    try {
      try {
        run();
        // success!
        return;
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static Series createSeries(Moderator moderator) throws IOException {
    // setup series
    Series series = new Series();
    series.setDescription("Share and rank tips for eating healthily on the cheaps!");
    series.setName("Eating Healthy & Cheap");
    // insert the series
    Moderator.Series.Insert request = moderator.series().insert(series);
    return request.execute();
  }

  private static Topic createTopic(Moderator moderator, long seriesId) throws IOException {
    // setup topic
    Topic topic = new Topic();
    topic.setDescription("Share your ideas on eating healthy!");
    topic.setName("Ideas");
    topic.setPresenter("liz");
    // insert the topic
    Moderator.Topics.Insert request = moderator.topics().insert(seriesId, topic);
    return request.execute();
  }

  private static Submission createSubmission(Moderator moderator, long seriesId, long topicId)
      throws IOException {
    // setup submission
    Submission submission = new Submission();
    submission.setAttachmentUrl("http://www.youtube.com/watch?v=1a1wyc5Xxpg");
    submission.setText("Charlie Ayers @ Google");
    // setup attribution
    Submission.Attribution attribution = new Submission.Attribution();
    attribution.setDisplayName("Bashan");
    attribution.setLocation("Bainbridge Island, WA");
    submission.setAttribution(attribution);
    // insert the submission
    Moderator.Submissions.Insert request =
        moderator.submissions().insert(seriesId, topicId, submission);
    return request.execute();
  }

  private static Vote createVote(Moderator moderator, long seriesId, long submissionId)
      throws IOException {
    // setup vote
    Vote vote = new Vote();
    vote.setVote("PLUS");
    // insert the vote
    Moderator.Votes.Insert request = moderator.votes().insert(seriesId, submissionId, vote);
    return request.execute();
  }

  private static Vote updateVote(Moderator moderator, long seriesId, long submissionId, Vote vote)
      throws IOException {
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
