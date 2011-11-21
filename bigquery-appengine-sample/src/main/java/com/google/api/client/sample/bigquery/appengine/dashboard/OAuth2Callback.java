// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import com.google.api.client.extensions.appengine.auth.AbstractAppEngineCallbackServlet;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth2.draft10.GoogleOAuth2ThreeLeggedFlow;


import javax.jdo.PersistenceManagerFactory;

/**
 * Holds information used in the authorization flow, such as which URL to redirect
 * to on success/failure.
 *
 * @author lparkinson@google.com (Laura Parkinson)
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
    return AuthUtils.PM_FACTORY;
  }
}
