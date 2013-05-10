/*
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

package com.google.api.services.samples.computeengine.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.ComputeScopes;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Command-line sample to demo listing Google Compute Engine instances
 * using Java and the Google Compute Engine API
 *
 * @author Jonathan Simon
 */
public class ComputeEngineSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  /** Set projectId to your Project ID from Overview pane in the APIs console */
  private static final String projectId = "YOUR_PROJECT_ID";

  /** Set Compute Engine zone  */
  private static final String zoneName = "us-central1-a";

  /** Global instance of the HTTP transport. */
  private static HttpTransport HTTP_TRANSPORT;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /** OAuth 2.0 scopes */
  private static final List<String> SCOPES = Arrays.asList(ComputeScopes.COMPUTE_READONLY);

  public static void main(String[] args) {

    //Start Authorization process
    try {
      try {
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        // Authorization
        Credential credential = authorize();

        //Create compute engine object for listing instances
        Compute compute = new Compute.Builder(HTTP_TRANSPORT, JSON_FACTORY, null)
            .setApplicationName(APPLICATION_NAME)
            .setHttpRequestInitializer(credential)
            .build();

        //List out instances
        printInstances(compute, projectId);
        //Success!
        return;
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // initialize client secrets object
    GoogleClientSecrets clientSecrets;
    // load client secrets
    clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(
        ComputeEngineSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
          + "into compute-engine-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"),
            ".credentials/compute-engine.json"), JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES).setCredentialStore(credentialStore)
        .build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  /**
  * Print available machine instances.
  * @param compute The main API access point
  * @param projectId The project ID.
  */
  public static void printInstances(Compute compute, String projectId) throws IOException {
    System.out.println("================== Listing Compute Engine Instances ==================");
    Compute.Instances.List instances = compute.instances().list(projectId, zoneName);
    InstanceList list = instances.execute();
    if (list.getItems() == null) {
      System.out.println("No instances found. Sign in to the Google APIs Console and create "
          + "an instance at: code.google.com/apis/console");
    }
    else
    {
      for (Instance instance : list.getItems()) {
        System.out.println(instance.toPrettyString());
      }
    }
  }
}
