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

package com.google.api.services.samples.prediction.cmdline;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.InputInput;
import com.google.api.services.prediction.model.Output;
import com.google.api.services.prediction.model.Training;
import com.google.api.services.samples.shared.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.oauth2.OAuth2ClientCredentials;
import com.google.api.services.samples.shared.oauth2.OAuth2Native;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class PredictionSample {

  static final String OBJECT_PATH = "enter_bucket/language_id.txt";

  /** OAuth 2 scope. */
  private static final String SCOPE = "https://www.googleapis.com/auth/prediction";

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
    Prediction prediction = new Prediction(transport, accessProtectedResource, jsonFactory);
    prediction.setApplicationName("Google-PredictionSample/1.0");
    train(prediction);
    predict(prediction, "Is this sentence in English?");
    predict(prediction, "¿Es esta frase en Español?");
    predict(prediction, "Est-ce cette phrase en Français?");
  }

  private static void train(Prediction prediction) throws IOException {
    Training training = new Training();
    training.setId(OBJECT_PATH);
    prediction.training.insert(training).execute();
    System.out.println("Training started.");
    System.out.print("Waiting for training to complete");
    System.out.flush();
    while (true) {
      training = prediction.training.get(OBJECT_PATH).execute();
      String trainingStatus = training.getTrainingStatus();
      if (!trainingStatus.equals("RUNNING")) {
        if (trainingStatus.startsWith("ERROR")) {
          System.err.println();
          System.err.println(trainingStatus);
          System.exit(1);
        }
        System.out.println();
        System.out.println("Training completed.");
        System.out.println(training.getModelInfo());
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        break;
      }
      System.out.print(".");
      System.out.flush();
    }
  }

  private static void predict(Prediction prediction, String text) throws IOException {
    Input input = new Input();
    InputInput inputInput = new InputInput();
    inputInput.setCsvInstance(Collections.<Object>singletonList(text));
    input.setInput(inputInput);
    Output output = prediction.training.predict(OBJECT_PATH, input).execute();
    System.out.println("Text: " + text);
    System.out.println("Predicted language: " + output.getOutputLabel());
  }

  public static void main(String[] args) {
    JsonFactory jsonFactory = new JacksonFactory();
    try {
      try {
        if (OAuth2ClientCredentials.CLIENT_ID == null
            || OAuth2ClientCredentials.CLIENT_SECRET == null) {
          System.err.println(
              "Please enter your client ID and secret in " + OAuth2ClientCredentials.class);
        } else {
          run(jsonFactory);
        }
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
}
