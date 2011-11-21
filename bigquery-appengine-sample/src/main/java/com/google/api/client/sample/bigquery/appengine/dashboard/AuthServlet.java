// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import com.google.api.client.extensions.appengine.auth.AbstractAppEngineFlowServlet;
import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.http.GenericUrl;
import com.google.common.base.Preconditions;

import java.io.IOException;

import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that handles authorization.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */
public abstract class AuthServlet extends AbstractAppEngineFlowServlet {

  private String callbackUrl;

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    setCallbackUrl(request);
    super.service(request, response);
  }

  @Override
  protected PersistenceManagerFactory getPersistenceManagerFactory() {
    return AuthUtils.PM_FACTORY;
  }

  @Override
  protected ThreeLeggedFlow newFlow(String userId) throws SampleDashboardException {
    AuthUtils authUtils = new AuthUtils(userId, getHttpTransport(), getJsonFactory());
    return authUtils.newFlow(getCallbackUrl());
  }

  protected String getCallbackUrl() {
    return Preconditions.checkNotNull(callbackUrl);
  }

  private void setCallbackUrl(HttpServletRequest request) {
    if (callbackUrl == null) {
      GenericUrl url = new GenericUrl(request.getRequestURL().toString());
      url.setRawPath("/oauth2callback");
      callbackUrl = url.build();
    }
  }
}
