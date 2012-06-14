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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.prediction.Prediction;
import com.google.api.services.prediction.PredictionScopes;
import com.google.api.services.prediction.model.Input;
import com.google.api.services.prediction.model.Input.InputInput;
import com.google.api.services.prediction.model.Output;
import com.google.api.services.prediction.model.Training;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class PredictionSample {

  static final String MODEL_ID = "mymodel";
  static final String STORAGE_DATA_LOCATION = "enter_bucket/language_id.txt";

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  private static void run() throws Exception {
    // authorization
    Credential credential = OAuth2Native.authorize(
        HTTP_TRANSPORT, JSON_FACTORY, new LocalServerReceiver(),
        Arrays.asList(PredictionScopes.PREDICTION));
    Prediction prediction = new Prediction.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Google-PredictionSample/1.0")
        .build();
    train(prediction);
    predict(prediction, "Is this sentence in English?");
    predict(prediction, "¿Es esta frase en Español?");
    predict(prediction, "Est-ce cette phrase en Français?");
  }

  private static void train(Prediction prediction) throws IOException {
    Training training = new Training();
    training.setId(MODEL_ID);
    training.setStorageDataLocation(STORAGE_DATA_LOCATION);
    prediction.trainedmodels().insert(training).execute();
    System.out.println("Training started.");
    System.out.print("Waiting for training to complete");
    System.out.flush();

    int triesCounter = 0;
    while (triesCounter < 100) {
      // NOTE: if model not found, it will throw an HttpResponseException with a 404 error
      try {
        HttpResponse response = prediction.trainedmodels().get(MODEL_ID).executeUnparsed();
        if (response.getStatusCode() == 200) {
          training = response.parseAs(Training.class);
          String trainingStatus = training.getTrainingStatus();
          if (trainingStatus.equals("DONE")) {
            System.out.println();
            System.out.println("Training completed.");
            System.out.println(training.getModelInfo());
            return;
          }
        }
        response.ignore();
      } catch (HttpResponseException e) {
      }

      try {
        // 5 seconds times the tries counter
        Thread.sleep(5000 * (triesCounter + 1));
      } catch (InterruptedException e) {
        break;
      }
      System.out.print(".");
      System.out.flush();
      triesCounter++;
    }
    error("ERROR: training not completed.");
  }

  private static void error(String errorMessage) {
    System.err.println();
    System.err.println(errorMessage);
    System.exit(1);
  }

  private static void predict(Prediction prediction, String text) throws IOException {
    Input input = new Input();
    InputInput inputInput = new InputInput();
    inputInput.setCsvInstance(Collections.<Object>singletonList(text));
    input.setInput(inputInput);
    Output output = prediction.trainedmodels().predict(MODEL_ID, input).execute();
    System.out.println("Text: " + text);
    System.out.println("Predicted language: " + output.getOutputLabel());
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
}
