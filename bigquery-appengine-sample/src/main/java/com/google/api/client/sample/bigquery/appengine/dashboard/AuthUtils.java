// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import com.google.api.client.extensions.auth.helpers.ThreeLeggedFlow;
import com.google.api.client.extensions.auth.helpers.oauth2.draft10.OAuth2Credential;
import com.google.api.client.googleapis.extensions.auth.helpers.oauth2.draft10.GoogleOAuth2ThreeLeggedFlow;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.samples.shared.appengine.AppEngineUtils;
import com.google.api.services.samples.shared.appengine.OAuth2ClientCredentials;

import java.io.IOException;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lparkinson@google.com (Laura Parkinson)
 *
 * Contains authorization variables and helper functions that multiple servlets need to access.
 * Also supervises the OAuth tokens, refreshing them when asked and tracking the most
 * recent access token.
 */
public class AuthUtils {

  private static final Logger log = Logger.getLogger(AuthUtils.class.getName());

  static final String BIGQUERY_SCOPE = "https://www.googleapis.com/auth/bigquery";
  static final PersistenceManagerFactory PM_FACTORY =
      AppEngineUtils.getPersistenceManagerFactory();

  private String userId;
  private HttpTransport transport;
  private JsonFactory factory;

  public AuthUtils(String userId, HttpTransport transport, JsonFactory factory) {
    this.userId = userId;
    this.transport = transport;
    this.factory = factory;
  }

  public ThreeLeggedFlow newFlow(String callbackUrl) throws SampleDashboardException {
    try {
      return new GoogleOAuth2ThreeLeggedFlow(userId, OAuth2ClientCredentials.getClientId(),
          OAuth2ClientCredentials.getClientSecret(), BIGQUERY_SCOPE, callbackUrl);
    } catch (IOException ex) {
      throw new SampleDashboardException(ex);
    }
  }

  /**
   * Removes the credentials for the given user from the datastore.
   * @throws SampleDashboardException 
   */
  public void clearTokens() throws SampleDashboardException {
    PersistenceManager persistenceManager = PM_FACTORY.getPersistenceManager();
    try {
      OAuth2Credential credential = getCredential(persistenceManager);
      persistenceManager.deletePersistent(credential);
    } finally {
      persistenceManager.close();
    }
  }

  /**
   * Uses the refresh token in the datastore to get a new access token. Stores and returns it.
   */
  public String refreshAccessToken() {
    String access = null;
    PersistenceManager persistenceManager = PM_FACTORY.getPersistenceManager();
    try {
      OAuth2Credential credential = getCredential(persistenceManager);
      credential.refresh(transport, factory);
      access = credential.getAccessToken();
    } catch (IOException e) {
      log.warning("Unable to refresh access token.");
      access = null;
    } finally {
      persistenceManager.close();
    }
    return access;
  }

  /**
   * Retrieves the access token from the datastore.
   * @throws SampleDashboardException 
   */
  public String getAccessToken() throws SampleDashboardException {
    String access = null;
    PersistenceManager persistenceManager = PM_FACTORY.getPersistenceManager();
    try {
      OAuth2Credential credential = getCredential(persistenceManager);
      access = credential.getAccessToken();
    } finally {
      persistenceManager.close();
    }
    return access;
  }

  private OAuth2Credential getCredential(PersistenceManager persistenceManager)
      throws SampleDashboardException {
    ThreeLeggedFlow oauthFlow = newFlow(null);
    oauthFlow.setJsonFactory(factory);
    oauthFlow.setHttpTransport(transport);
    return (OAuth2Credential) oauthFlow.loadCredential(persistenceManager);
  }

  /**
   * Clears user credentials if a given exception is an unauthorized exception.
   */
  public boolean handleUnauthorizedException(SampleDashboardException ex) {
    if (ex.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
      log.warning("User credentials didn't work, so they were deleted.");
      try {
        clearTokens();
        return true;
      } catch (SampleDashboardException ex2) {
        return false;
      }
    }
    return false;
  }
}
