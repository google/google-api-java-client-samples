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
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Activity;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Yaniv Inbar
 */
public class PlusServiceAccountSample {

  /** E-mail address of the service account. */
  private static final String SERVICE_ACCOUNT_EMAIL = "[[INSERT SERVICE ACCOUNT EMAIL HERE]]";

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  private static Plus plus;

  public static void main(String[] args) {
    try {
      try {
        // check for valid setup
        Preconditions.checkArgument(!SERVICE_ACCOUNT_EMAIL.startsWith("[["),
            "Please enter your service account e-mail from the Google APIs Console to the "
            + "SERVICE_ACCOUNT_EMAIL constant in %s", PlusServiceAccountSample.class.getName());
        String p12Content = Files.readFirstLine(new File("key.p12"), Charset.defaultCharset());
        Preconditions.checkArgument(!p12Content.startsWith("Please"), p12Content);
        // service account credential (uncomment setServiceAccountUser for domain-wide delegation)
        GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT)
            .setJsonFactory(JSON_FACTORY)
            .setServiceAccountId(SERVICE_ACCOUNT_EMAIL)
            .setServiceAccountScopes(PlusScopes.PLUS_ME)
            .setServiceAccountPrivateKeyFromP12File(new File("key.p12"))
            // .setServiceAccountUser("user@example.com")
            .build();
        // set up global Plus instance
        plus = new Plus.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(
            "Google-PlusServiceAccountSample/1.0").build();
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
