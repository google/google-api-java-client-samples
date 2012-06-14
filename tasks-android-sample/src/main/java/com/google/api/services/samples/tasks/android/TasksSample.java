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
package com.google.api.services.samples.tasks.android;


import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample for Tasks API on Android. It shows how to authenticate using OAuth 2.0, and get the list
 * of tasks.
 * <p>
 * To enable logging of HTTP requests/responses, change {@link #LOGGING_LEVEL} to
 * {@link Level#CONFIG} or {@link Level#ALL} and run this command:
 * </p>
 * 
 * <pre>
adb shell setprop log.tag.HttpTransport DEBUG
 * </pre>
 * 
 * @author Johan Euphrosine
 */
public final class TasksSample extends ListActivity {

  /** Logging level for HTTP requests/responses. */
  private static final Level LOGGING_LEVEL = Level.OFF;

  private static final String TAG = "TasksSample";

  // This must be the exact string, and is a special for alias OAuth 2 scope
  // "https://www.googleapis.com/auth/tasks"
  private static final String AUTH_TOKEN_TYPE = "Manage your tasks";

  private static final int MENU_ACCOUNTS = 0;

  private static final int REQUEST_AUTHENTICATE = 0;

  final HttpTransport transport = AndroidHttp.newCompatibleTransport();

  final JsonFactory jsonFactory = new GsonFactory();

  static final String PREF_ACCOUNT_NAME = "accountName";

  static final String PREF_AUTH_TOKEN = "authToken";

  GoogleAccountManager accountManager;

  SharedPreferences settings;

  String accountName;

  GoogleCredential credential = new GoogleCredential();

  com.google.api.services.tasks.Tasks service;

  private boolean received401;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ClientCredentials.errorIfNotSpecified();
    service = new com.google.api.services.tasks.Tasks.Builder(transport, jsonFactory, credential)
        .setApplicationName("Google-TasksAndroidSample/1.0")
        .setJsonHttpRequestInitializer(new GoogleKeyInitializer(ClientCredentials.KEY))
        .build();
    settings = getPreferences(MODE_PRIVATE);
    accountName = settings.getString(PREF_ACCOUNT_NAME, null);
    credential.setAccessToken(settings.getString(PREF_AUTH_TOKEN, null));
    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
    accountManager = new GoogleAccountManager(this);
    gotAccount();
  }

  void gotAccount() {
    Account account = accountManager.getAccountByName(accountName);
    if (account == null) {
      chooseAccount();
      return;
    }
    if (credential.getAccessToken() != null) {
      onAuthToken();
      return;
    }
    accountManager.getAccountManager()
        .getAuthToken(account, AUTH_TOKEN_TYPE, true, new AccountManagerCallback<Bundle>() {

          public void run(AccountManagerFuture<Bundle> future) {
            try {
              Bundle bundle = future.getResult();
              if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_AUTHENTICATE);
              } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
                onAuthToken();
              }
            } catch (Exception e) {
              Log.e(TAG, e.getMessage(), e);
            }
          }
        }, null);
  }

  private void chooseAccount() {
    accountManager.getAccountManager().getAuthTokenByFeatures(GoogleAccountManager.ACCOUNT_TYPE,
        AUTH_TOKEN_TYPE,
        null,
        TasksSample.this,
        null,
        null,
        new AccountManagerCallback<Bundle>() {

          public void run(AccountManagerFuture<Bundle> future) {
            Bundle bundle;
            try {
              bundle = future.getResult();
              setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
              setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
              onAuthToken();
            } catch (OperationCanceledException e) {
              // user canceled
            } catch (AuthenticatorException e) {
              Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
              Log.e(TAG, e.getMessage(), e);
            }
          }
        },
        null);
  }

  void setAccountName(String accountName) {
    SharedPreferences.Editor editor = settings.edit();
    editor.putString(PREF_ACCOUNT_NAME, accountName);
    editor.commit();
    this.accountName = accountName;
  }

  void setAuthToken(String authToken) {
    SharedPreferences.Editor editor = settings.edit();
    editor.putString(PREF_AUTH_TOKEN, authToken);
    editor.commit();
    credential.setAccessToken(authToken);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_AUTHENTICATE:
        if (resultCode == RESULT_OK) {
          gotAccount();
        } else {
          chooseAccount();
        }
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (accountManager.getAccounts().length >= 2) {
      menu.add(0, MENU_ACCOUNTS, 0, "Switch Account");
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_ACCOUNTS:
        chooseAccount();
        return true;
    }
    return false;
  }

  void onAuthToken() {
    new AsyncLoadTasks(this).execute();
  }

  void onRequestCompleted() {
    received401 = false;
  }

  void handleGoogleException(IOException e) {
    if (e instanceof GoogleJsonResponseException) {
      GoogleJsonResponseException exception = (GoogleJsonResponseException) e;
      if (exception.getStatusCode() == 401 && !received401) {
        received401 = true;
        accountManager.invalidateAuthToken(credential.getAccessToken());
        credential.setAccessToken(null);
        SharedPreferences.Editor editor2 = settings.edit();
        editor2.remove(PREF_AUTH_TOKEN);
        editor2.commit();
        gotAccount();
        return;
      }
    }
    Log.e(TAG, e.getMessage(), e);
  }
}
