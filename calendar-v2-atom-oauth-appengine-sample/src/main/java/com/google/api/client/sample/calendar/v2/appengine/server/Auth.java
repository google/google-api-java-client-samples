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

package com.google.api.client.sample.calendar.v2.appengine.server;

import com.google.api.client.auth.RsaSha;
import com.google.api.client.auth.oauth.AbstractOAuthGetToken;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.auth.oauth.OAuthRsaSigner;
import com.google.api.client.auth.oauth.OAuthSigner;
import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.http.HttpExecuteIntercepter;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.sample.calendar.v2.appengine.server.Constants.ConstantsForHMAC;
import com.google.api.client.sample.calendar.v2.appengine.server.Constants.ConstantsForRSA;
import com.google.api.client.sample.calendar.v2.appengine.server.Constants.SignatureMethod;
import com.google.api.client.sample.calendar.v2.appengine.shared.AuthenticationException;
import com.google.api.client.xml.atom.AtomParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import javax.servlet.http.HttpServletRequest;

/**
 * Manages OAuth authentication.
 *
 * @author Yaniv Inbar
 */
class Auth {

  private static PrivateKey privateKey;

  /** Sets up the HTTP transport. */
  static HttpTransport setUpTransport() throws IOException {
    HttpTransport result = newHttpTransport();
    GoogleUtils.useMethodOverride(result);
    GoogleHeaders headers = newHeaders(result);
    headers.gdataVersion = "2";
    AtomParser parser = new AtomParser();
    parser.namespaceDictionary = Namespace.DICTIONARY;
    result.addParser(parser);
    return result;
  }

  /** Sets up the HTTP transport to use for Authentication. */
  static HttpTransport setUpAuthTransport() throws IOException {
    HttpTransport result = newHttpTransport();
    newHeaders(result);
    return result;
  }

  private static HttpTransport newHttpTransport() throws IOException {
    HttpTransport result = new UrlFetchTransport();
    OAuthParameters parameters = new OAuthParameters();
    parameters.consumerKey = Constants.ENTER_DOMAIN;
    UserCredentials userCredentials = UserCredentials.forCurrentUser();
    parameters.signer = createSigner(userCredentials);
    if (userCredentials != null) {
      parameters.token = userCredentials.token;
    }
    parameters.signRequestsUsingAuthorizationHeader(result);
    return result;
  }

  private static GoogleHeaders newHeaders(HttpTransport transport) {
    GoogleHeaders headers = new GoogleHeaders();
    headers.setApplicationName("Google-CalendarAppEngineSample/1.0");
    transport.defaultHeaders = headers;
    return headers;
  }

  static OAuthSigner createSigner(UserCredentials cred) throws IOException {
    if (Constants.SIGNATURE_METHOD == SignatureMethod.RSA) {
      OAuthRsaSigner result = new OAuthRsaSigner();
      result.privateKey = getPrivateKey();
      return result;
    }
    OAuthHmacSigner result = new OAuthHmacSigner();
    result.clientSharedSecret = ConstantsForHMAC.ENTER_OAUTH_CONSUMER_KEY;
    if (cred != null) {
      result.tokenSharedSecret = cred.tokenSecret;
    }
    return result;
  }

  private static PrivateKey getPrivateKey() throws IOException {
    if (privateKey == null) {
      try {
        privateKey =
            RsaSha.getPrivateKeyFromKeystore(
                new FileInputStream(ConstantsForRSA.ENTER_PATH_TO_KEY_STORE_FILE),
                ConstantsForRSA.ENTER_KEY_STORE_PASSWORD,
                ConstantsForRSA.ENTER_ALIAS_FOR_PRIVATE_KEY,
                ConstantsForRSA.ENTER_PRIVATE_KEY_PASSWORD);
      } catch (GeneralSecurityException e) {
        throw new IOException(e);
      }
    }
    return privateKey;
  }

  /**
   * Returns the full URL of the request including the query parameters.
   */
  static String getFullURL(HttpServletRequest request) {
    StringBuffer buf = request.getRequestURL();
    if (request.getQueryString() != null) {
      buf.append('?').append(request.getQueryString());
    }
    return buf.toString();
  }

  static UserCredentials executeGetToken(
      AbstractOAuthGetToken request, UserCredentials currentCredentials) throws IOException {
    HttpTransport transport = Auth.setUpTransport();
    request.transport = transport;
    OAuthCredentialsResponse response = request.execute();
    UserCredentials result = currentCredentials;
    if (result == null) {
      result = new UserCredentials();
      result.userId = UserCredentials.getCurrentUserId();
    }
    result.token = response.token;
    result.tokenSecret = response.tokenSecret;
    result.temporary = response.callbackConfirmed != null;
    return result.makePersistent();
  }

  static HttpResponse execute(HttpRequest request) throws IOException {
    try {
      return request.execute();
    } catch (HttpResponseException e) {
      switch (e.response.statusCode) {
        case 302:
          // redirect
          CalendarUrl locationUrl = new CalendarUrl(e.response.headers.location);
          UserCredentials credentials = UserCredentials.forCurrentUser();
          credentials.gsessionid = locationUrl.gsessionid;
          credentials.makePersistent();
          request.url = locationUrl;
          new SessionIntercepter(request.transport, locationUrl.gsessionid);
          e.response.ignore(); // force the connection to close
          return request.execute();
        case 401:
          // check for an authentication error (e.g. if user revoked token)
          if (e.response.headers.authenticate != null) {
            UserCredentials.deleteCurrentUserFromStore();
            throw new AuthenticationException();
          }
      }
      throw e;
    }
  }

  /**
   * See <a href="http://code.google.com/apis/calendar/faq.html#redirect_handling">How do I handle
   * redirects...?</a>.
   */
  static class SessionIntercepter implements HttpExecuteIntercepter {

    private String gsessionid;

    SessionIntercepter(HttpTransport transport, String gsessionid) {
      this.gsessionid = gsessionid;
      transport.removeIntercepters(SessionIntercepter.class);
      transport.intercepters.add(0, this); // must be first
    }

    public void intercept(HttpRequest request) {
      request.url.set("gsessionid", gsessionid);
    }
  }

  static IOException newIOException(HttpResponseException e) throws IOException {
    return new IOException(e.response.parseAsString());
  }
}
