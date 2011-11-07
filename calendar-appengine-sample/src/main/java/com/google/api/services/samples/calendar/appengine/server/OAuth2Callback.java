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

package com.google.api.services.samples.calendar.appengine.server;

import com.google.api.client.extensions.appengine.auth.AbstractAppEngineCallbackServlet;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth2.draft10.GoogleOAuth2ThreeLeggedFlow;
import com.google.api.services.samples.shared.appengine.AppEngineUtils;

import javax.jdo.PersistenceManagerFactory;

/**
 * HTTP servlet to process access granted from user.
 * 
 * @author Yaniv Inbar
 */
public class OAuth2Callback extends AbstractAppEngineCallbackServlet {

  private static final long serialVersionUID = 1L;

  @Override
  protected Class<? extends ThreeLeggedFlow> getConcreteFlowType() {
    return GoogleOAuth2ThreeLeggedFlow.class;
  }

  @Override
  protected String getCompletionCodeQueryParam() {
    return "code";
  }

  @Override
  protected String getDeniedRedirectUrl() {
    return "/denied";
  }

  @Override
  protected String getSuccessRedirectUrl() {
    return "/";
  }

  @Override
  protected PersistenceManagerFactory getPersistenceManagerFactory() {
    return AppEngineUtils.getPersistenceManagerFactory();
  }
}
