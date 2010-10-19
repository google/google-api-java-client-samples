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

package com.google.api.client.sample.prediction;

import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.prediction.model.CheckTraining;
import com.google.api.client.sample.prediction.model.Debug;
import com.google.api.client.sample.prediction.model.InputData;
import com.google.api.client.sample.prediction.model.OutputData;
import com.google.api.client.sample.prediction.model.PredictionUrl;
import com.google.api.client.util.ArrayMap;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class PredictionSample {

  public static void main(String[] args) {
    Debug.enableLogging();
    HttpTransport transport = GoogleTransport.create();
    transport.addParser(new JsonCParser());
    try {
      try {
        authenticateWithClientLogin(transport);
        train(transport);
        predict(transport, "Is this sentence in English?");
        predict(transport, "¿Es esta frase en Español?");
        predict(transport, "Est-ce cette phrase en Français?");
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  private static void authenticateWithClientLogin(HttpTransport transport)
      throws IOException {
    ClientLogin authenticator = new ClientLogin();
    authenticator.authTokenType = "xapi";
    authenticator.username = ClientLoginCredentials.ENTER_USERNAME;
    authenticator.password = ClientLoginCredentials.ENTER_PASSWORD;
    authenticator.authenticate().setAuthorizationHeader(transport);
  }

  private static void train(HttpTransport transport) throws IOException {
    HttpRequest request = transport.buildPostRequest();
    JsonCContent content = new JsonCContent();
    content.data = ArrayMap.create();
    request.content = content;
    request.url = PredictionUrl.forTraining(ClientLoginCredentials.OBJECT_PATH);
    request.execute().ignore();
    System.out.println("Training started.");
    System.out.print("Waiting for training to complete");
    System.out.flush();
    while (true) {
      request = transport.buildGetRequest();
      request.url =
          PredictionUrl.forCheckingTraining(ClientLoginCredentials.OBJECT_PATH);
      CheckTraining checkTraining =
          request.execute().parseAs(CheckTraining.class);
      if (checkTraining.modelinfo.toLowerCase().startsWith(
          "estimated accuracy")) {
        System.out.println();
        System.out.println("Training completed.");
        System.out.println(checkTraining.modelinfo);
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

  private static void predict(HttpTransport transport, String text)
      throws IOException {
    HttpRequest request = transport.buildPostRequest();
    request.url =
        PredictionUrl.forPrediction(ClientLoginCredentials.OBJECT_PATH);
    JsonCContent content = new JsonCContent();
    InputData inputData = new InputData();
    inputData.input.text.add(text);
    content.data = inputData;
    request.content = content;
    OutputData outputData = request.execute().parseAs(OutputData.class);
    System.out.println("Text: " + text);
    System.out.println("Predicted language: " + outputData.outputLabel);
  }
}
