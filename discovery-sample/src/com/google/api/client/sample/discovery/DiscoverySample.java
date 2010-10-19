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

package com.google.api.client.sample.discovery;

import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceMethod;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceParameter;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceResource;
import com.google.api.client.googleapis.json.GoogleApi;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.InputStreamContent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yaniv Inbar
 */
public class DiscoverySample {

  private static final String APP_NAME = "Google Discovery API Client 1.0.1";

  private static final Pattern API_NAME_PATTERN = Pattern.compile("\\w+");

  private static final Pattern API_VERSION_PATTERN = Pattern.compile("[\\w.]+");

  private static final Pattern METHOD_PATTERN =
      Pattern.compile("(\\w+)\\.(\\w+)");

  public static void main(String[] args) throws Exception {
    Debug.enableLogging();
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
        System.out.println(
            "Usage: google call apiName apiVersion methodName [parameters]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  google call discovery 0.1 apis.get --api buzz");
        System.out.println(
            "  google call buzz v1 activities.list --scope @self --userId @me");
        System.out.println(
            "  echo {\\\"data\\\":{\\\"object\\\":{\\\"content\\\":"
                + "\\\"Posting using Google command-line tool based on "
                + "Discovery \\(http://bit.ly/avYMh1\\)\\\"}}} > "
                + "buzzpost.json && google call buzz v1 activities.insert "
                + "--userId @me buzzpost.json");
      } else if (helpCommand.equals("discover")) {
        System.out.println("Usage: google discover apiName apiVersion");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  google discover buzz v1");
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
    System.err.println(
        "For help, type: google" + (command == null ? "" : " help " + command));
    System.exit(1);
  }

  private static void call(String[] args) throws Exception {
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
    String resourceName = m.group(1);
    String methodName = m.group(2);
    GoogleApi api = loadGoogleAPI("call", apiName, apiVersion);
    Map<String, ServiceResource> resources = api.serviceDefinition.resources;
    ServiceMethod method = null;
    if (resources != null) {
      ServiceResource resource = resources.get(resourceName);
      Map<String, ServiceMethod> methods = resource.methods;
      if (methods != null) {
        method = methods.get(methodName);
      }
    }
    if (method == null) {
      error("call", "method not found: " + fullMethodName);
    }
    HashMap<String, String> parameters = new HashMap<String, String>();
    HashMap<String, String> queryParameters = new HashMap<String, String>();
    File requestBodyFile = null;
    int i = 4;
    while (i < args.length) {
      String argName = args[i++];
      if (argName.startsWith("--")) {
        String parameterName = argName.substring(2);
        if (i == args.length) {
          error("call", "missing parameter value for: " + argName);
        }
        String parameterValue = args[i++];
        if (method.parameters == null
            || !method.parameters.containsKey(parameterName)) {
          queryParameters.put(parameterName, parameterValue);
        } else {
          String oldValue = parameters.put(parameterName, parameterValue);
          if (oldValue != null) {
            error("call", "duplicate parameter: " + argName);
          }
        }
      } else {
        if (requestBodyFile != null) {
          error(
              "call", "multiple HTTP request body files specified: " + argName);
        }
        String fileName = argName;
        requestBodyFile = new File(fileName);
        if (!requestBodyFile.canRead()) {
          error("call", "unable to read file: " + argName);
        }
      }
    }
    for (Map.Entry<String, ServiceParameter> parameter :
        method.parameters.entrySet()) {
      String paramName = parameter.getKey();
      if (parameter.getValue().required && !parameters.containsKey(paramName)) {
        error("call", "missing required parameter: " + paramName);
      }
    }
    HttpRequest request =
        api.buildRequest(resourceName + "." + methodName, parameters);
    request.url.putAll(queryParameters);
    if (!request.url.containsKey("alt")) {
      request.url.put("alt", "json");
    }
    if (!request.url.containsKey("prettyprint")) {
      request.url.put("prettyprint", "true");
    }
    if (requestBodyFile != null) {
      InputStreamContent fileContent = new InputStreamContent();
      // TODO: support other content types?
      fileContent.type = "application/json";
      fileContent.setFileInput(requestBodyFile);
      request.content = fileContent;
    }
    try {
      if (apiName.equals("bigquery") || apiName.equals("prediction")
          || apiName.equals("latitude")) {
        error("call", "API not supported: " + apiName);
      }
      if (!apiName.equals("discovery") && !apiName.equals("diacritize")) {
        Auth.authorize(api.transport, apiName, method);
      }
      String response = request.execute().parseAsString();
      System.out.println(response);
      Auth.revoke();
    } catch (HttpResponseException e) {
      System.err.println(e.response.parseAsString());
      Auth.revoke();
      System.exit(1);
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }
  }

  private static GoogleApi loadGoogleAPI(
      String command, String apiName, String apiVersion) throws IOException {
    if (!API_NAME_PATTERN.matcher(apiName).matches()) {
      error(command, "invalid API name: " + apiName);
    }
    if (!API_VERSION_PATTERN.matcher(apiVersion).matches()) {
      error(command, "invalid API version: " + apiVersion);
    }
    GoogleApi api = new GoogleApi();
    api.name = apiName;
    api.version = apiVersion;
    try {
      api.load();
    } catch (HttpResponseException e) {
      if (e.response.statusCode == 404) {
        error(command, "API not found: " + apiName);
      }
    } catch (IllegalArgumentException e) {
      error(command, "Version not found: " + apiVersion);
    }
    return api;
  }

  private static void discover(String[] args) throws IOException {
    System.out.println(APP_NAME);
    // load discovery doc
    if (args.length == 1) {
      error("discover", "missing api name");
    }
    if (args.length == 2) {
      error("discover", "missing api version");
    }
    String apiName = args[1];
    String apiVersion = args[2];
    GoogleApi api = loadGoogleAPI("discover", apiName, apiVersion);
    // compute method details
    ArrayList<MethodDetails> result = new ArrayList<MethodDetails>();
    Map<String, ServiceResource> resources = api.serviceDefinition.resources;
    if (resources != null) {
      for (Map.Entry<String, ServiceResource> resourceEntry :
          resources.entrySet()) {
        String resourceName = apiName + "." + resourceEntry.getKey();
        ServiceResource resource = resourceEntry.getValue();
        Map<String, ServiceMethod> methods = resource.methods;
        if (methods != null) {
          for (Map.Entry<String, ServiceMethod> methodEntry :
              methods.entrySet()) {
            MethodDetails details = new MethodDetails();
            details.name = resourceName + "." + methodEntry.getKey();
            Map<String, ServiceParameter> parameters =
                methodEntry.getValue().parameters;
            if (parameters != null) {
              for (Map.Entry<String, ServiceParameter> parameterEntry :
                  parameters.entrySet()) {
                String parameterName = parameterEntry.getKey();
                if (parameterEntry.getValue().required) {
                  details.requiredParameters.add(parameterName);
                } else {
                  details.optionalParameters.add(parameterName);
                }
              }
              Collections.sort(details.requiredParameters);
              Collections.sort(details.optionalParameters);
              result.add(details);
            }
          }
        }
      }
    }
    Collections.sort(result);
    // display method details
    for (MethodDetails methodDetail : result) {
      System.out.println();
      System.out.println(methodDetail.name);
      System.out.println("  required parameters: "
          + showParams(methodDetail.requiredParameters));
      System.out.println("  optional parameters: "
          + showParams(methodDetail.optionalParameters));
    }
  }

  private static String showParams(ArrayList<String> params) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < params.size(); i++) {
      if (i != 0) {
        buf.append(", ");
      }
      buf.append(params.get(i));
    }
    return buf.toString();
  }
}
