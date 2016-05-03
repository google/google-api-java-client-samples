/*
 * Copyright (c) 2012 Google Inc.
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

package com.google.api.services.samples.fusiontables.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.fusiontables.Fusiontables;
import com.google.api.services.fusiontables.Fusiontables.Query.Sql;
import com.google.api.services.fusiontables.Fusiontables.Table.Delete;
import com.google.api.services.fusiontables.FusiontablesScopes;
import com.google.api.services.fusiontables.model.Column;
import com.google.api.services.fusiontables.model.Table;
import com.google.api.services.fusiontables.model.TableList;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

/**
 * @author Christian Junk
 *
 */
public class FusionTablesSample {

  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      new java.io.File(System.getProperty("user.home"), ".store/fusion_tables_sample");
  
  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;

  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static Fusiontables fusiontables;

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
        JSON_FACTORY, new InputStreamReader(
            FusionTablesSample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=fusiontables "
          + "into fusiontables-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(FusiontablesScopes.FUSIONTABLES)).setDataStoreFactory(
        dataStoreFactory).build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public static void main(String[] args) {
    try {
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
      // authorization
      Credential credential = authorize();
      // set up global FusionTables instance
      fusiontables = new Fusiontables.Builder(
          httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
      // run commands
      listTables();
      String tableId = createTable();
      insertData(tableId);
      showRows(tableId);
      deleteTable(tableId);
      // success!
      return;
    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  /**
   * @param tableId
   * @throws IOException
   */
  private static void showRows(String tableId) throws IOException {
    View.header("Showing Rows From Table");

    Sql sql = fusiontables.query().sql("SELECT Text,Number,Location,Date FROM " + tableId);

    try {
      sql.execute();
    } catch (IllegalArgumentException e) {
      // For google-api-services-fusiontables-v1-rev1-1.7.2-beta this exception will always
      // been thrown.
      // Please see issue 545: JSON response could not be deserialized to Sqlresponse.class
      // http://code.google.com/p/google-api-java-client/issues/detail?id=545
    }
  }

  /** List tables for the authenticated user. */
  private static void listTables() throws IOException {
    View.header("Listing My Tables");

    // Fetch the table list
    Fusiontables.Table.List listTables = fusiontables.table().list();
    TableList tablelist = listTables.execute();

    if (tablelist.getItems() == null || tablelist.getItems().isEmpty()) {
      System.out.println("No tables found!");
      return;
    }

    for (Table table : tablelist.getItems()) {
      View.show(table);
      View.separator();
    }
  }

  /** Create a table for the authenticated user. */
  private static String createTable() throws IOException {
    View.header("Create Sample Table");

    // Create a new table
    Table table = new Table();
    table.setName(UUID.randomUUID().toString());
    table.setIsExportable(false);
    table.setDescription("Sample Table");

    // Set columns for new table
    table.setColumns(Arrays.asList(new Column().setName("Text").setType("STRING"),
        new Column().setName("Number").setType("NUMBER"),
        new Column().setName("Location").setType("LOCATION"),
        new Column().setName("Date").setType("DATETIME")));

    // Adds a new column to the table.
    Fusiontables.Table.Insert t = fusiontables.table().insert(table);
    Table r = t.execute();

    View.show(r);

    return r.getTableId();
  }

  /** Inserts a row in the newly created table for the authenticated user. */
  private static void insertData(String tableId) throws IOException {
    Sql sql = fusiontables.query().sql("INSERT INTO " + tableId + " (Text,Number,Location,Date) "
        + "VALUES (" + "'Google Inc', " + "1, " + "'1600 Amphitheatre Parkway Mountain View, "
        + "CA 94043, USA','" + new DateTime(new Date()) + "')");

    try {
      sql.execute();
    } catch (IllegalArgumentException e) {
      // For google-api-services-fusiontables-v1-rev1-1.7.2-beta this exception will always
      // been thrown.
      // Please see issue 545: JSON response could not be deserialized to Sqlresponse.class
      // http://code.google.com/p/google-api-java-client/issues/detail?id=545
    }
  }

  /** Deletes a table for the authenticated user. */
  private static void deleteTable(String tableId) throws IOException {
    View.header("Delete Sample Table");
    // Deletes a table
    Delete delete = fusiontables.table().delete(tableId);
    delete.execute();
  }
}
