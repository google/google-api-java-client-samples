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

package com.google.api.services.samples.calendar.android;

import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleKeyInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.common.collect.Lists;

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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample for Google Calendar API v3. It shows how to authenticate, get calendars, add a new
 * calendar, update it, and delete it.
 * 
 * <p>
 * To enable logging of HTTP requests/responses, change {@link #LOGGING_LEVEL} to
 * {@link Level#CONFIG} or {@link Level#ALL} and run this command:
 * </p>
 * 
 * <pre>
adb shell setprop log.tag.HttpTransport DEBUG
 * </pre>
 * 
 * @author Yaniv Inbar
 */
public final class CalendarSample extends ListActivity {

  /** Logging level for HTTP requests/responses. */
  private static final Level LOGGING_LEVEL = Level.OFF;

  private static final String TAG = "CalendarSample";

  private static final String AUTH_TOKEN_TYPE = "cl";

  private static final int MENU_ACCOUNTS = 0;

  private static final int MENU_ADD = 1;

  private static final int CONTEXT_EDIT = 0;

  private static final int CONTEXT_DELETE = 1;

  private static final int REQUEST_AUTHENTICATE = 0;

  final HttpTransport transport = AndroidHttp.newCompatibleTransport();

  final JsonFactory jsonFactory = new JacksonFactory();

  static final String PREF_ACCOUNT_NAME = "accountName";

  static final String PREF_AUTH_TOKEN = "authToken";

  GoogleAccountManager accountManager;

  SharedPreferences settings;

  String accountName;

  String authToken;

  com.google.api.services.calendar.Calendar client;

  private final List<CalendarListEntry> calendars = Lists.newArrayList();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    client = com.google.api.services.calendar.Calendar.builder(transport, jsonFactory)
        .setApplicationName("Google-CalendarAndroidSample/1.0")
        .setHttpRequestInitializer(new HttpRequestInitializer() {
          public void initialize(HttpRequest request) throws IOException {
            request.getHeaders().setAuthorization(GoogleHeaders.getGoogleLoginValue(authToken));
          }
        })
        .setJsonHttpRequestInitializer(new GoogleKeyInitializer(ClientCredentials.KEY))
        .build();
    settings = getPreferences(MODE_PRIVATE);
    accountName = settings.getString(PREF_ACCOUNT_NAME, null);
    authToken = settings.getString(PREF_AUTH_TOKEN, null);
    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
    accountManager = new GoogleAccountManager(this);
    registerForContextMenu(getListView());
    gotAccount();
  }

  void gotAccount() {
    Account account = accountManager.getAccountByName(accountName);
    if (account == null) {
      chooseAccount();
      return;
    }
    if (authToken != null) {
      onAuthToken();
      return;
    }
    accountManager.getAccountManager().getAuthToken(
        account, AUTH_TOKEN_TYPE, true, new AccountManagerCallback<Bundle>() {

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
        CalendarSample.this,
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
    this.authToken = authToken;
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
    menu.add(0, MENU_ADD, 0, "New Calendar");
    if (accountManager.getAccounts().length >= 2) {
      menu.add(0, MENU_ACCOUNTS, 0, "Switch Account");
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_ADD:
        Calendar entry = new Calendar();
        entry.setSummary("Calendar " + new DateTime(new Date()));
        try {
          client.calendars().insert(entry).execute();
        } catch (IOException e) {
          handleGoogleException(e);
        }
        onAuthToken();
        return true;
      case MENU_ACCOUNTS:
        chooseAccount();
        return true;
    }
    return false;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, CONTEXT_EDIT, 0, "Update Title");
    menu.add(0, CONTEXT_DELETE, 0, "Delete");
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    CalendarListEntry calendar = calendars.get((int) info.id);
    try {
      switch (item.getItemId()) {
        case CONTEXT_EDIT:
          Calendar entry = new Calendar();
          entry.setSummary(calendar.getSummary() + " UPDATED " + new DateTime(new Date()));
          client.calendars().patch(calendar.getId(), entry).execute();
          onAuthToken();
          return true;
        case CONTEXT_DELETE:
          client.calendars().delete(calendar.getId()).execute();
          onAuthToken();
          return true;
        default:
          return super.onContextItemSelected(item);
      }
    } catch (IOException e) {
      handleGoogleException(e);
    }
    return false;
  }

  void onAuthToken() {
    List<String> calendarNames = new ArrayList<String>();
    calendars.clear();
    try {
      CalendarList feed = client.calendarList().list().execute();
      if (feed.getItems() != null) {
        for (CalendarListEntry calendar : feed.getItems()) {
          calendars.add(calendar);
          calendarNames.add(calendar.getSummary());
        }
      }
    } catch (IOException e) {
      handleGoogleException(e);
    }
    setListAdapter(
        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, calendarNames));
  }

  void handleGoogleException(IOException e) {
    if (e instanceof GoogleJsonResponseException) {
      GoogleJsonResponseException exception = (GoogleJsonResponseException) e;
      // TODO(yanivi): should only try this once to avoid infinite loop
      if (exception.getStatusCode() == 401) {
        accountManager.invalidateAuthToken(authToken);
        authToken = null;
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
