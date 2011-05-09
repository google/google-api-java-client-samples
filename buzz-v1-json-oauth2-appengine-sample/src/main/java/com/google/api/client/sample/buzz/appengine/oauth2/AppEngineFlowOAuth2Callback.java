// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.buzz.appengine.oauth2;

import com.google.api.client.extensions.appengine.auth.AbstractAppEngineCallbackServlet;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth2.draft10.GoogleOAuth2ThreeLeggedFlow;

import javax.jdo.PersistenceManagerFactory;

/**
 * This is the callback that handles completion requests from the token server.
 * It is currently set up for the proper values for OAuth2 Draft 10.
 *
 * @author moshenko@google.com (Jacob Moshenko)
 *
 */
public class AppEngineFlowOAuth2Callback extends AbstractAppEngineCallbackServlet {

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
    return PMF.get();
  }
}
