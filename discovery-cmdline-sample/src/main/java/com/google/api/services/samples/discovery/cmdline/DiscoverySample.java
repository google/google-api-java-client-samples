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

package com.google.api.services.samples.discovery.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.client.util.Maps;
import com.google.api.services.discovery.Discovery;
import com.google.api.services.discovery.model.DirectoryList;
import com.google.api.services.discovery.model.JsonSchema;
import com.google.api.services.discovery.model.RestDescription;
import com.google.api.services.discovery.model.RestMethod;
import com.google.api.services.discovery.model.RestResource;
import com.google.common.base.Joiner;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yaniv Inbar
 */
public class DiscoverySample {

  /** Global instance of the HTTP transport. */
  private static HttpTransport HTTP_TRANSPORT;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  static Discovery DISCOVERY;

  private static final String APP_NAME = "Google Discovery API Client";

  private static final Pattern API_NAME_PATTERN = Pattern.compile("\\w+");

  private static final Pattern API_VERSION_PATTERN = Pattern.compile("[\\w.]+");

  private static final Pattern METHOD_PATTERN = Pattern.compile("((\\w+)\\.)*(\\w+)");

  public static void main(String[] args) throws Exception {
    // parse command argument
    if (args.length == 0) {
      showMainHelp();
    } else {
      String command = args[0];
      if (command.equals("help")) {
        help(args);
      } else if (command.equals("call")) {
        call(args);
      } else if (command.equals("discover")) {
        discover(args);
      } else {
        error(null, "unknown command: " + command);
      }
    }
  }

  private static void help(String[] args) {
    if (args.length == 1) {
      showMainHelp();
    } else {
      String helpCommand = args[1];
      if (helpCommand.equals("call")) {
        System.out.println("Usage: google call apiName apiVersion methodName [parameters]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  google call discovery v1 apis.getRest plus v1");
        System.out.println("  google call plus v1 activities.list me public --max-results 3");
        System.out.println("  google call calendar v3 calendarList.list");
        System.out.println("  echo {\"summary\":\"temporary calendar\"} > /tmp/post.json && "
            + "google call calendar v3 calendars.insert /tmp/post.json");
      } else if (helpCommand.equals("discover")) {
        System.out.println("Usage");
        System.out.println("List all APIs: google discover");
        System.out.println("List method of an API: google discover <apiName> <apiVersion>");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  google discover");
        System.out.println("  google discover plus v1");
        System.out.println("  google discover moderator v1");
      } else {
        error(null, "unknown command: " + helpCommand);
      }
    }
  }

  private static void showMainHelp() {
    System.out.println(APP_NAME);
    System.out.println();
    System.out.println("For more help on a specific command, type one of:");
    System.out.println();
    System.out.println("  google help call");
    System.out.println("  google help discover");
  }

  private static void error(String command, String detail) {
    System.err.println("ERROR: " + detail);
    System.err.println("For help, type: google" + (command == null ? "" : " help " + command));
    System.exit(1);
  }

  private static void putParameter(String argName, Map<String, Object> parameters,
      String parameterName, JsonSchema parameter, String parameterValue) {
    Object value = parameterValue;
    if (parameter != null) {
      if ("boolean".equals(parameter.getType())) {
        value = Boolean.valueOf(parameterValue);
      } else if ("number".equals(parameter.getType())) {
        value = new BigDecimal(parameterValue);
      } else if ("integer".equals(parameter.getType())) {
        value = new BigInteger(parameterValue);
      }
    }
    Object oldValue = parameters.put(parameterName, value);
    if (oldValue != null) {
      error("call", "duplicate parameter: " + argName);
    }
  }

  private static void call(String[] args) throws Exception {
    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    DISCOVERY = new Discovery(HTTP_TRANSPORT, JSON_FACTORY, null);
    
    // load discovery document
    if (args.length == 1) {
      error("call", "missing api name");
    }
    if (args.length == 2) {
      error("call", "missing api version");
    }
    if (args.length == 3) {
      error("call", "missing method name");
    }
    String apiName = args[1];
    String apiVersion = args[2];
    String fullMethodName = args[3];
    Matcher m = METHOD_PATTERN.matcher(fullMethodName);
    if (!m.matches()) {
      error("call", "invalid method name: " + fullMethodName);
    }
    RestDescription restDescription = loadGoogleAPI("call", apiName, apiVersion);
    Map<String, RestMethod> methodMap = null;
    int curIndex = 0;
    int nextIndex = fullMethodName.indexOf('.');
    if (nextIndex == -1) {
      methodMap = restDescription.getMethods();
    } else {
      Map<String, RestResource> resources = restDescription.getResources();
      while (true) {
        RestResource resource = resources.get(fullMethodName.substring(curIndex, nextIndex));
        if (resource == null) {
          break;
        }
        curIndex = nextIndex + 1;
        nextIndex = fullMethodName.indexOf(curIndex + 1, '.');
        if (nextIndex == -1) {
          methodMap = resource.getMethods();
          break;
        }
        resources = resource.getResources();
      }
    }
    RestMethod method =
        methodMap == null ? null : methodMap.get(fullMethodName.substring(curIndex));
    if (method == null) {
      error("call", "method not found: " + fullMethodName);
    }
    HashMap<String, Object> parameters = Maps.newHashMap();
    File requestBodyFile = null;
    String contentType = "application/json";
    int i = 4;
    // required parameters
    if (method.getParameterOrder() != null) {
      for (String parameterName : method.getParameterOrder()) {
        JsonSchema parameter = method.getParameters().get(parameterName);
        if (Boolean.TRUE.equals(parameter.getRequired())) {
          if (i == args.length) {
            error("call", "missing required parameter: " + parameter);
          } else {
            putParameter(null, parameters, parameterName, parameter, args[i++]);
          }
        }
      }
    }
    // possibly required content
    if (!method.getHttpMethod().equals("GET") && !method.getHttpMethod().equals("DELETE")) {
      String fileName = args[i++];
      requestBodyFile = new File(fileName);
      if (!requestBodyFile.canRead()) {
        error("call", "unable to read file: " + fileName);
      }
    }
    while (i < args.length) {
      String argName = args[i++];
      if (!argName.startsWith("--")) {
        error("call", "optional parameters must start with \"--\": " + argName);
      }
      String parameterName = argName.substring(2);
      if (i == args.length) {
        error("call", "missing parameter value for: " + argName);
      }
      String parameterValue = args[i++];
      if (parameterName.equals("contentType")) {
        contentType = parameterValue;
        if (method.getHttpMethod().equals("GET") || method.getHttpMethod().equals("DELETE")) {
          error("call", "HTTP content type cannot be specified for this method: " + argName);
        }
      } else {
        JsonSchema parameter = null;
        if (restDescription.getParameters() != null) {
          parameter = restDescription.getParameters().get(parameterName);
        }
        if (parameter == null && method.getParameters() == null) {
          parameter = method.getParameters().get(parameterName);
        }
        putParameter(argName, parameters, parameterName, parameter, parameterValue);
      }
    }
    GenericUrl url = new GenericUrl(UriTemplate.expand(
        "https://www.googleapis.com" + restDescription.getBasePath() + method.getPath(), parameters,
        true));
    HttpContent content = null;
    if (requestBodyFile != null) {
      content = new FileContent(contentType, requestBodyFile);
    }
    try {
      HttpRequestFactory requestFactory;
      if (method.getScopes() != null) {
        List<String> scopes = Lists.newArrayListWithCapacity(method.getScopes().size());
        for (Object s : method.getScopes()) {
          scopes.add((String) s);
        }
        Credential credential = authorize(method.getId(), scopes);
        requestFactory = HTTP_TRANSPORT.createRequestFactory(credential);
      } else {
        requestFactory = HTTP_TRANSPORT.createRequestFactory();
      }
      HttpRequest request =
          requestFactory.buildRequest(method.getHttpMethod(), url, content);
      String response = request.execute().parseAsString();
      System.out.println(response);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    } catch (Throwable t) {
      t.printStackTrace();
      System.exit(1);
    }
  }

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize(String methodId, List<String> scopes) throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(DiscoverySample.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/ "
          + "into discovery-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up file credential store
    FileCredentialStore credentialStore = new FileCredentialStore(
        new File(System.getProperty("user.home"), ".credentials/discovery-" + methodId + ".json"),
        JSON_FACTORY);
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialStore(credentialStore)
        .build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  private static RestDescription loadGoogleAPI(String command, String apiName, String apiVersion)
      throws IOException {
    if (!API_NAME_PATTERN.matcher(apiName).matches()) {
      error(command, "invalid API name: " + apiName);
    }
    if (!API_VERSION_PATTERN.matcher(apiVersion).matches()) {
      error(command, "invalid API version: " + apiVersion);
    }
    try {
      return DISCOVERY.apis().getRest(apiName, apiVersion).execute();
    } catch (HttpResponseException e) {
      if (e.getStatusCode() == 404) {
        error(command, "API not found: " + apiName);
        return null;
      }
      throw e;
    }
  }

  private static void processMethods(
      ArrayList<MethodDetails> result, String resourceName, Map<String, RestMethod> methodMap) {
    if (methodMap == null) {
      return;
    }
    for (Map.Entry<String, RestMethod> methodEntry : methodMap.entrySet()) {
      MethodDetails details = new MethodDetails();
      String methodName = methodEntry.getKey();
      RestMethod method = methodEntry.getValue();
      details.name = (resourceName.isEmpty() ? "" : resourceName + ".") + methodName;
      details.hasContent =
          !method.getHttpMethod().equals("GET") && !method.getHttpMethod().equals("DELETE");
      // required parameters
      if (method.getParameterOrder() != null) {
        for (String parameterName : method.getParameterOrder()) {
          JsonSchema parameter = method.getParameters().get(parameterName);
          if (Boolean.TRUE.equals(parameter.getRequired())) {
            details.requiredParameters.add(parameterName);
          }
        }
      }
      // optional parameters
      Map<String, JsonSchema> parameters = method.getParameters();
      if (parameters != null) {
        for (Map.Entry<String, JsonSchema> parameterEntry : parameters.entrySet()) {
          String parameterName = parameterEntry.getKey();
          JsonSchema parameter = parameterEntry.getValue();
          if (!Boolean.TRUE.equals(parameter.getRequired())) {
            details.optionalParameters.add(parameterName);
          }
        }
      }
      result.add(details);
    }
  }

  private static void processResources(
      ArrayList<MethodDetails> result, String resourceName, Map<String, RestResource> resourceMap) {
    if (resourceMap == null) {
      return;
    }
    for (Map.Entry<String, RestResource> entry : resourceMap.entrySet()) {
      RestResource resource = entry.getValue();
      String curResourceName = (resourceName.isEmpty() ? "" : resourceName + ".") + entry.getKey();
      processMethods(result, curResourceName, resource.getMethods());
      processResources(result, curResourceName, resource.getResources());
    }
  }

  private static void discover(String[] args) throws Exception {
    HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    DISCOVERY = new Discovery(HTTP_TRANSPORT, JSON_FACTORY, null);
    System.out.println(APP_NAME);
    if (args.length == 1) {
      DirectoryList directoryList = DISCOVERY.apis().list().execute();
      for (DirectoryList.Items item : directoryList.getItems()) {
        System.out.println();
        System.out.print(item.getTitle() + " " + item.getVersion());
        if (item.getLabels() != null) {
          System.out.print(" (" + Joiner.on(", ").join(item.getLabels()) + ")");
        }
        System.out.println();
        System.out.println("Description: " + item.getDescription());
        System.out.println("Methods: google discover " + item.getName() + " " + item.getVersion());
      }
      System.out.println();
      return;
    }
    // load discovery doc
    if (args.length == 2) {
      error("discover", "missing api version");
    }
    String apiName = args[1];
    String apiVersion = args[2];
    System.out.println();
    System.out.println("API Name: " + apiName);
    System.out.println("API Version: " + apiVersion);
    System.out.println();
    System.out.println("Methods:");
    RestDescription restDescription = loadGoogleAPI("discover", apiName, apiVersion);
    // compute method details
    ArrayList<MethodDetails> result = Lists.newArrayList();
    String resourceName = "";
    processMethods(result, resourceName, restDescription.getMethods());
    processResources(result, resourceName, restDescription.getResources());
    Collections.sort(result);
    // display method details
    for (MethodDetails methodDetail : result) {
      System.out.println();
      System.out.print("google call " + apiName + " " + apiVersion + " " + methodDetail.name);
      for (String param : methodDetail.requiredParameters) {
        System.out.print(" <" + param + ">");
      }
      if (methodDetail.hasContent) {
        System.out.print(" contentFile");
      }
      if (methodDetail.optionalParameters.isEmpty() && !methodDetail.hasContent) {
        System.out.println();
      } else {
        System.out.println(" [optional parameters...]");
        System.out.println("  --contentType <value> (default is \"application/json\")");
        for (String param : methodDetail.optionalParameters) {
          System.out.println("  --" + param + " <value>");
        }
      }
    }
  }
}
