/*
 * Copyright (c) 2011 Google Inc.
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

package com.google.api.client.sample.buzz.appengine.oauth;

import com.google.api.client.extensions.appengine.auth.AbstractAppEngineCallbackServlet;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth.GoogleOAuthHmacThreeLeggedFlow;

import javax.jdo.PersistenceManagerFactory;

/**
 * This is the callback that handles completion requests from the token server.
 * It is currently set up for the proper values for OAuth2 Draft 10.
 *
 * @author moshenko@google.com (Jacob Moshenko)
 *
 */
public class AppEngineFlowOAuthCallback extends AbstractAppEngineCallbackServlet {

  @Override
  protected Class<? extends ThreeLeggedFlow> getConcreteFlowType() {
    return GoogleOAuthHmacThreeLeggedFlow.class;
  }

  @Override
  protected String getCompletionCodeQueryParam() {
    return "oauth_verifier";
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
    return PMF.get();
  }
}
