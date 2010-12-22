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

package com.google.api.client.sample.discovery;

import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceMethod;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceParameter;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceResource;
import com.google.api.client.googleapis.json.GoogleApi;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yaniv Inbar
 */
public class DiscoverySample {

  private static final String APP_NAME = "Google Discovery API Client 1.1.0";

  private static final Pattern API_NAME_PATTERN = Pattern.compile("\\w+");

  private static final Pattern API_VERSION_PATTERN = Pattern.compile("[\\w.]+");

  private static final Pattern METHOD_PATTERN = Pattern.compile("(\\w+)\\.(\\w+)");

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
        System.out.println("Usage: google call apiName apiVersion methodName [parameters]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  google call discovery v0.2beta1 apis.get buzz v1");
        System.out.println("  google call buzz v1 activities.list @me @self "
            + "--max-results 3 --alt json --prettyprint true");
        System.out.println("  echo {\\\"data\\\":{\\\"object\\\":{\\\"content\\\":"
            + "\\\"Posting using Google command-line tool based on "
            + "Discovery \\(http://goo.gl/ojuXq\\)\\\"}}} > "
            + "buzzpost.json && google call buzz v1 activities.insert @me buzzpost.json");
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
    System.err.println("For help, type: google" + (command == null ? "" : " help " + command));
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
    HashMap<String, String> parameters = Maps.newHashMap();
    HashMap<String, String> queryParameters = Maps.newHashMap();
    File requestBodyFile = null;
    String contentType = "application/json";
    int i = 4;
    // required parameters
    for (String reqParam : getRequiredParameters(method).keySet()) {
      if (i == args.length) {
        error("call", "missing required parameter: " + reqParam);
      } else {
        parameters.put(reqParam, args[i++]);
      }
    }
    // possibly required content
    if (!method.httpMethod.equals("GET") && !method.httpMethod.equals("DELETE")) {
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
        if (method.httpMethod.equals("GET") || method.httpMethod.equals("DELETE")) {
          error("call", "HTTP content type cannot be specified for this method: " + argName);
        }
      } else if (method.parameters == null || !method.parameters.containsKey(parameterName)) {
        queryParameters.put(parameterName, parameterValue);
      } else {
        String oldValue = parameters.put(parameterName, parameterValue);
        if (oldValue != null) {
          error("call", "duplicate parameter: " + argName);
        }
      }
    }
    HttpRequest request = api.buildRequest(resourceName + "." + methodName, parameters);
    request.url.putAll(queryParameters);
    if (requestBodyFile != null) {
      InputStreamContent fileContent = new InputStreamContent();
      fileContent.type = contentType;
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

  private static GoogleApi loadGoogleAPI(String command, String apiName, String apiVersion)
      throws IOException {
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
    System.out.println();
    System.out.println("API Name: " + apiName);
    System.out.println("API Version: " + apiVersion);
    System.out.println();
    System.out.println("Methods:");
    GoogleApi api = loadGoogleAPI("discover", apiName, apiVersion);
    // compute method details
    ArrayList<MethodDetails> result = Lists.newArrayList();
    Map<String, ServiceResource> resources = api.serviceDefinition.resources;
    if (resources != null) {
      // iterate over resources
      for (Map.Entry<String, ServiceResource> resourceEntry : resources.entrySet()) {
        String resourceName = resourceEntry.getKey();
        ServiceResource resource = resourceEntry.getValue();
        // iterate over methods
        Map<String, ServiceMethod> methods = resource.methods;
        if (methods != null) {
          for (Map.Entry<String, ServiceMethod> methodEntry : methods.entrySet()) {
            ServiceMethod method = methodEntry.getValue();
            MethodDetails details = new MethodDetails();
            details.name = resourceName + "." + methodEntry.getKey();
            details.hasContent =
                !method.httpMethod.equals("GET") && !method.httpMethod.equals("DELETE");
            // required parameters
            for (String param : getRequiredParameters(method).keySet()) {
              details.requiredParameters.add(param);
            }
            // optional parameters
            Map<String, ServiceParameter> parameters = method.parameters;
            if (parameters != null) {
              for (Map.Entry<String, ServiceParameter> parameterEntry : parameters.entrySet()) {
                String parameterName = parameterEntry.getKey();
                ServiceParameter parameter = parameterEntry.getValue();
                if (!parameter.required) {
                  details.optionalParameters.add(parameterName);
                }
              }
            }
            result.add(details);
          }
        }
      }
    }
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

  static LinkedHashMap<String, ServiceParameter> getRequiredParameters(ServiceMethod method) {
    ArrayList<String> requiredParams = Lists.newArrayList();
    int cur = 0;
    String pathUrl = method.pathUrl;
    int length = pathUrl.length();
    while (cur < length) {
      int next = pathUrl.indexOf('{', cur);
      if (next == -1) {
        break;
      }
      int close = pathUrl.indexOf('}', next + 2);
      String paramName = pathUrl.substring(next + 1, close);
      requiredParams.add(paramName);
      cur = close + 1;
    }
    Map<String, ServiceParameter> parameters = method.parameters;
    if (parameters != null) {
      SortedSet<String> nonPathRequiredParameters = Sets.newTreeSet();
      for (Map.Entry<String, ServiceParameter> parameterEntry : parameters.entrySet()) {
        String parameterName = parameterEntry.getKey();
        ServiceParameter parameter = parameterEntry.getValue();
        if (parameter.required && !requiredParams.contains(parameterName)) {
          nonPathRequiredParameters.add(parameterName);
        }
      }
      requiredParams.addAll(nonPathRequiredParameters);
    }
    LinkedHashMap<String, ServiceParameter> result = Maps.newLinkedHashMap();
    for (String paramName : requiredParams) {
      result.put(paramName, method.parameters.get(paramName));
    }
    return result;
  }
}
