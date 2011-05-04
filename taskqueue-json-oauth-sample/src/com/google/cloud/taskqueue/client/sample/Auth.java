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

package com.google.cloud.taskqueue.client.sample;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetAccessToken;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetTemporaryToken;
import com.google.cloud.taskqueue.client.sample.model.Util;

import java.io.*;
import java.util.Scanner;

/**
 * Implements OAuth authentication.
 *
 * @author Vibhooti Verma
 */
public class Auth {

  private static OAuthHmacSigner signer;
  private static OAuthCredentialsResponse credentials;
  private static String TOKEN_FILE = ".taskqueue_token";

 /**
  * Method to authorize the request using oauth1 authorization. This actually
  * signs the util.TRANSPORT object using the authorization header and makes it
  * eligible to access the the taskqueue service. The same TRANSPORT object is
  * used throughout the application(sample) to build any kind of request (post/
  * get/delete etc).
  * There can be two cases during authorization:
  * 1. First time users: This asks the user to open the google authorization
  * url in browser and to provide their credentail and finally
  * paste the received verifier token. Once authorized, it automatically
  * stores access tokens (also called long lived tokens) in local copy for
  * later use.
  * 2. Once the credentails are found in local copy as plain text,
  * authorization is done seemlessely using them. However, it should be kept
  * in mind that storing token/crdentials in plain text is quite insecure and
  * a real application must use good encryption to store them. Since this is
  * a sample, we are currently storing them as plain text itself.
  *
  * @throws Exception
  */
  static void authorize() throws Exception {
    TOKEN_FILE = System.getProperty("user.home") + "/" +
                 TOKEN_FILE;
    signer = new OAuthHmacSigner();
    signer.clientSharedSecret = ClientCredentials.ENTER_OAUTH_CONSUMER_SECRET;
    File file = new File(TOKEN_FILE);
    // If access tokens are already stored locally, make use of them.
    if (file.exists()) {
      createOAuthParametersFromTokenFile().
          signRequestsUsingAuthorizationHeader(Util.TRANSPORT);
      return;
    }

    String verifier = null;
    String tempToken = null;
    try {
      GoogleOAuthGetTemporaryToken temporaryToken = new GoogleOAuthGetTemporaryToken();
      temporaryToken.transport = Util.AUTH_TRANSPORT;
      temporaryToken.signer = signer;
      temporaryToken.consumerKey = ClientCredentials.ENTER_OAUTH_CONSUMER_KEY;
      temporaryToken.scope = "https://www.googleapis.com/auth/taskqueue";
      temporaryToken.displayName = TaskQueueSample.APP_DESCRIPTION;
      // We are not implementing the callback server since mostly the workers
      // will run on VM and  we want to save  user from opening the brower on
      // VM. Hence setting the callback to oob.
      temporaryToken.callback =  "oob";
      OAuthCredentialsResponse tempCredentials = temporaryToken.execute();
      signer.tokenSharedSecret = tempCredentials.tokenSecret;
      // authorization URL
      OAuthAuthorizeTemporaryTokenUrl authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(
          "https://www.google.com/accounts/OAuthAuthorizeToken");
      authorizeUrl.set("scope", temporaryToken.scope);
      authorizeUrl.set("domain", ClientCredentials.ENTER_OAUTH_CONSUMER_KEY);
      authorizeUrl.set("xoauth_displayname", TaskQueueSample.APP_DESCRIPTION);
      authorizeUrl.temporaryToken = tempToken = tempCredentials.token;
      String authorizationUrl = authorizeUrl.build();
      System.out.println("Please run this URL in a browser and paste the" +
                         "token back here\n" + authorizationUrl);
      System.out.println("Enter verification code: ");
      InputStreamReader converter = new InputStreamReader(System.in);
      Scanner in = new Scanner(System.in);
      verifier = in.nextLine();
    } catch (Exception e){
      System.err.println("Error: " + e.getMessage());
    }
    // access token
    GoogleOAuthGetAccessToken accessToken = new GoogleOAuthGetAccessToken();
    accessToken.transport = Util.AUTH_TRANSPORT;
    accessToken.temporaryToken = tempToken;
    accessToken.signer = signer;
    accessToken.consumerKey = ClientCredentials.ENTER_OAUTH_CONSUMER_KEY;
    accessToken.verifier = verifier;
    credentials = accessToken.execute();
    signer.tokenSharedSecret = credentials.tokenSecret;
    createOAuthParameters().signRequestsUsingAuthorizationHeader(Util.TRANSPORT);
    try {
      FileWriter fstream = new FileWriter(TOKEN_FILE);
      BufferedWriter out = new BufferedWriter(fstream);
      out.write(credentials.tokenSecret);
      out.newLine();
      out.write(credentials.token);
      out.close();
    } catch (Exception e){
      System.err.println("Error: " + e.getMessage());
    }
  }

/*
 *  Method to revoke authorization of long lived access tokens. This should be
 *  called if you do not want to retain the access tokens for later user.
 */
  static void revoke() {
    if (credentials != null) {
      try {
        GoogleOAuthGetAccessToken.revokeAccessToken(Util.AUTH_TRANSPORT, createOAuthParameters());
      } catch (Exception e) {
        e.printStackTrace(System.err);
      }
    }
  }

  /*
   * Create authentication parameters using access token credentials and signer.
   * Both of these are set in function calling this createoauthParameters
   * (mehtod:authorize in this case).
   * Signer contains both
   * 1. Consumer secret key
   * 2. Access token secret
   *
   */
   public static OAuthParameters createOAuthParameters() {
    OAuthParameters authorizer = new OAuthParameters();
    authorizer.consumerKey = ClientCredentials.ENTER_OAUTH_CONSUMER_KEY;
    authorizer.signer = signer;
    authorizer.token = credentials.token;
    return authorizer;
  }

   /*
    * Create authentication parameters using access token credentials stored in
    * file.
    */
  public static OAuthParameters createOAuthParametersFromTokenFile() {
  String tokenSecret = "";
  String token = "";
    try {
      BufferedReader br = new BufferedReader(new FileReader(TOKEN_FILE));
      tokenSecret = br.readLine();
      token = br.readLine();
      if (tokenSecret == null || token == null) {
        System.out.println("Credentials stored in " + TOKEN_FILE +
          " are incomplete or corrupt. Please delete the file and get new" + 
          " credentials by running the sample again.");
        System.exit(0);
      }
      br.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    signer.tokenSharedSecret = tokenSecret;
    OAuthParameters authorizer = new OAuthParameters();
    authorizer.consumerKey = ClientCredentials.ENTER_OAUTH_CONSUMER_KEY;
    authorizer.signer = signer;
    authorizer.token = token;
    return authorizer;
  }
}
