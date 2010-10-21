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

package com.google.api.client.sample.bigquery;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.bigquery.model.BigQueryUrl;
import com.google.api.client.sample.bigquery.model.Debug;
import com.google.api.client.sample.bigquery.model.QueryData;
import com.google.api.client.sample.bigquery.model.QueryRow;
import com.google.api.client.sample.bigquery.model.QueryValue;
import com.google.api.client.sample.bigquery.model.SchemaData;
import com.google.api.client.sample.bigquery.model.SchemaField;

import java.io.IOException;

/**
 * @author Yaniv Inbar
 */
public class BigQuerySample {

  public static void main(String[] args) {
    Debug.enableLogging();
    HttpTransport transport = setUpTransport();
    try {
      try {
        authenticateWithClientLogin(transport);
        executeSchema(transport, "bigquery/samples/shakespeare");
        executeQuery(
            transport, "select count(*) from [bigquery/samples/shakespeare];");
        executeQuery(
            transport,
            "select corpus, word, word_count from [bigquery/samples/shakespeare] where word_count > 600 order by word_count desc;");
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  private static HttpTransport setUpTransport() {
    HttpTransport transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("Google-BigQuerySample/1.0");
    transport.addParser(new JsonCParser());
    return transport;
  }

  public static void authenticateWithClientLogin(HttpTransport transport)
      throws IOException {
    ClientLogin authenticator = new ClientLogin();
    authenticator.authTokenType = "ndev";
    authenticator.username = ClientLoginCredentials.ENTER_USERNAME;
    authenticator.password = ClientLoginCredentials.ENTER_PASSWORD;
    authenticator.authenticate().setAuthorizationHeader(transport);
  }

  static QueryData executeQuery(HttpTransport transport, String query)
      throws IOException {
    header("Query: " + query);
    HttpRequest request = transport.buildGetRequest();
    BigQueryUrl url = BigQueryUrl.fromRelativePath("query");
    url.q = query;
    request.url = url;
    QueryData result = request.execute().parseAs(QueryData.class);
    if (result.fields == null) {
      System.out.println("No fields");
    } else {
      for (SchemaField field : result.fields) {
        System.out.print(field.id + "\t");
      }
      System.out.println();
      for (QueryRow row : result.rows) {
        for (QueryValue value : row.f) {
          System.out.print(value.value + "\t");
        }
        System.out.println();
      }
    }
    return result;
  }

  static SchemaData executeSchema(HttpTransport transport, String tableName)
      throws IOException {
    header("Schema: " + tableName);
    HttpRequest request = transport.buildGetRequest();
    BigQueryUrl url = BigQueryUrl.fromRelativePath("tables");
    url.pathParts.add(tableName);
    request.url = url;
    SchemaData result = request.execute().parseAs(SchemaData.class);
    if (result.fields == null) {
      System.out.println("No fields");
    } else {
      System.out.println(result.fields.size() + " fields:");
      for (SchemaField field : result.fields) {
        System.out.println(field.type + " " + field.id);
      }
    }
    return result;
  }

  private static void header(String name) {
    System.out.println();
    System.out.println("============== " + name + " ==============");
    System.out.println();
  }
}
