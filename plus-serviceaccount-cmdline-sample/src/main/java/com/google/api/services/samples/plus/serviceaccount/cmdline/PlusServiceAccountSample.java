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

package com.google.api.services.samples.plus.serviceaccount.cmdline;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Activity;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

/**
 * @author Yaniv Inbar
 */
public class PlusServiceAccountSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";
  
  /** E-mail address of the service account. */
  private static final String SERVICE_ACCOUNT_EMAIL = "Enter service account e-mail from "
      + "https://code.google.com/apis/console/?api=plus into SERVICE_ACCOUNT_EMAIL in "
      + PlusServiceAccountSample.class;

  /** Global instance of the HTTP transport. */
  private static HttpTransport HTTP_TRANSPORT;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  private static Plus plus;

  public static void main(String[] args) {
    try {
      try {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        // check for valid setup
        if (SERVICE_ACCOUNT_EMAIL.startsWith("Enter ")) {
          System.err.println(SERVICE_ACCOUNT_EMAIL);
          System.exit(1);
        }
        String p12Content = Files.readFirstLine(new File("key.p12"), Charset.defaultCharset());
        if (p12Content.startsWith("Please")) {
          System.err.println(p12Content);
          System.exit(1);
        }
        // service account credential (uncomment setServiceAccountUser for domain-wide delegation)
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
            .setServiceAccountScopes(Collections.singleton(PlusScopes.PLUS_ME))
            .setServiceAccountPrivateKeyFromP12File(new File("key.p12"))
            // .setServiceAccountUser("user@example.com")
            .build();
        // set up global Plus instance
        plus = new Plus.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME).build();
        // run commands
        getActivity();
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

  /** Get an activity for which we already know the ID. */
  private static void getActivity() throws IOException {
    // A known public activity ID
    String activityId = "z12gtjhq3qn2xxl2o224exwiqruvtda0i";
    // We do not need to be authenticated to fetch this activity
    View.header1("Get an explicit public activity by ID");
    Activity activity = plus.activities().get(activityId).execute();
    View.show(activity);
  }
}
