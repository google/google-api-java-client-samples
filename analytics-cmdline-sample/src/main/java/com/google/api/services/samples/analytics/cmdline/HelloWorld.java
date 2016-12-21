package com.google.api.services.samples.analytics.cmdline;

/**
 * Access and manage a Google Tag Manager account.
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tagmanager.TagManager;
import com.google.api.services.tagmanager.TagManager.Accounts.List;
import com.google.api.services.tagmanager.TagManagerScopes;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class HelloWorld {
  // Path to client_secrets.json file downloaded from the Developer's Console.
  // The path is relative to HelloWorld.java.
  private static final String CLIENT_SECRET_JSON_RESOURCE = "/client_secrets.json";

  // The directory where the user's credentials will be stored for the application.
  private static final File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".store/analytics_sample");

  private static final String APPLICATION_NAME = "HelloWorld";
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static NetHttpTransport httpTransport;
  private static FileDataStoreFactory dataStoreFactory;

  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

      // Authorization flow.
      Credential credential = authorize();
      TagManager manager = new TagManager.Builder(httpTransport, JSON_FACTORY, credential)
          .setApplicationName(APPLICATION_NAME).build();
      int size = manager.accounts().list().size();
      List accountsList = manager.accounts().list();
      Set<String> set = accountsList.keySet();
      for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
        String string = iterator.next();
        System.out.println(string);
        
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static Credential authorize() throws Exception {
    // Load client secrets.
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(HelloWorld.class.getResourceAsStream(CLIENT_SECRET_JSON_RESOURCE)));

    // Set up authorization code flow for all auth scopes.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
        JSON_FACTORY, clientSecrets, TagManagerScopes.all()).setDataStoreFactory(dataStoreFactory)
        .build();

    // Authorize.
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }
}

    