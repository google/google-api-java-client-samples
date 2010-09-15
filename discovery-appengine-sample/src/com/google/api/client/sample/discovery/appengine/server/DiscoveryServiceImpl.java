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

package com.google.api.client.sample.discovery.appengine.server;

import com.google.api.client.googleapis.json.DiscoveryDocument;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceDefinition;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceMethod;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceParameter;
import com.google.api.client.googleapis.json.DiscoveryDocument.ServiceResource;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.sample.discovery.appengine.client.DiscoveryService;
import com.google.api.client.sample.discovery.appengine.shared.KnownGoogleApis;
import com.google.api.client.sample.discovery.appengine.shared.MethodDetails;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yaniv Inbar
 */
public class DiscoveryServiceImpl extends RemoteServiceServlet
    implements DiscoveryService {

  static final HashMap<String, ServiceDefinition> CACHED =
      new HashMap<String, ServiceDefinition>();

  @Override
  public ArrayList<MethodDetails> getMethods(String apiName)
      throws IOException, IllegalArgumentException {
    KnownGoogleApis knownApi = KnownGoogleApis.valueOf(apiName);
    ServiceDefinition def = CACHED.get(apiName);
    if (def == null) {
      try {
        DiscoveryDocument discoveryDocument =
            DiscoveryDocument.load(knownApi.apiName);
        def = discoveryDocument.apiDefinition.get(knownApi.version);
        CACHED.put(apiName, def);
      } catch (HttpResponseException e) {
        e.response.ignore();
        throw e;
      }
    }
    ArrayList<MethodDetails> result = new ArrayList<MethodDetails>();
    Map<String, ServiceResource> resources = def.resources;
    if (resources != null) {
      for (Map.Entry<String, ServiceResource> resourceEntry :
          resources.entrySet()) {
        String resourceName = knownApi.apiName + "." + resourceEntry.getKey();
        Map<String, ServiceMethod> methods = resourceEntry.getValue().methods;
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
    return result;
  }
}
