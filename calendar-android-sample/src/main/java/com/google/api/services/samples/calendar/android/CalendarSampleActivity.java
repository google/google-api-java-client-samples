/*
 * Copyright (c) 2012 Google Inc.
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

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample activity for Google Calendar API v3. It demonstrates how to use authorization to list
 * calendars, add a new calendar, and edit or delete an existing calendar with the user's
 * permission.
 * 
 * <p>
 * <b>Warning:</b> this sample is for illustrative purposes only. Please instead use the native
 * Calendar APIs like the <a
 * href="http://developer.android.com/guide/topics/providers/calendar-provider.html">Calendar
 * Provider API</a>.
 * </p>
 * 
 * @author Yaniv Inbar
 */
public final class CalendarSampleActivity extends Activity {

  /**
   * Logging level for HTTP requests/responses.
   * 
   * <p>
   * To turn on, set to {@link Level#CONFIG} or {@link Level#ALL} and run this from command line:
   * </p>
   * 
   * <pre>
adb shell setprop log.tag.HttpTransport DEBUG
   * </pre>
   */
  private static final Level LOGGING_LEVEL = Level.OFF;

  private static final String PREF_ACCOUNT_NAME = "accountName";

  static final String TAG = "CalendarSampleActivity";

  private static final int CONTEXT_EDIT = 0;

  private static final int CONTEXT_DELETE = 1;

  private static final int CONTEXT_BATCH_ADD = 2;

  static final int REQUEST_GOOGLE_PLAY_SERVICES = 0;

  static final int REQUEST_AUTHORIZATION = 1;

  static final int REQUEST_ACCOUNT_PICKER = 2;

  private final static int ADD_OR_EDIT_CALENDAR_REQUEST = 3;

  final HttpTransport transport = AndroidHttp.newCompatibleTransport();

  final JsonFactory jsonFactory = new GsonFactory();

  GoogleAccountCredential credential;

  CalendarModel model = new CalendarModel();

  ArrayAdapter<CalendarInfo> adapter;

  com.google.api.services.calendar.Calendar client;

  int numAsyncTasks;

  private ListView listView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // enable logging
    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
    // view and menu
    setContentView(R.layout.calendarlist);
    listView = (ListView) findViewById(R.id.list);
    registerForContextMenu(listView);
    // Google Accounts
    credential =
        GoogleAccountCredential.usingOAuth2(this, Collections.singleton(CalendarScopes.CALENDAR));
    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    credential.setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
    // Calendar client
    client = new com.google.api.services.calendar.Calendar.Builder(
        transport, jsonFactory, credential).setApplicationName("Google-CalendarAndroidSample/1.0")
        .build();
  }

  void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
    runOnUiThread(new Runnable() {
      public void run() {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
            connectionStatusCode, CalendarSampleActivity.this, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
      }
    });
  }

  void refreshView() {
    adapter = new ArrayAdapter<CalendarInfo>(
        this, android.R.layout.simple_list_item_1, model.toSortedArray()) {

        @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        // by default it uses toString; override to use summary instead
        TextView view = (TextView) super.getView(position, convertView, parent);
        CalendarInfo calendarInfo = getItem(position);
        view.setText(calendarInfo.summary);
        return view;
      }
    };
    listView.setAdapter(adapter);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (checkGooglePlayServicesAvailable()) {
      haveGooglePlayServices();
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode == Activity.RESULT_OK) {
          haveGooglePlayServices();
        } else {
          checkGooglePlayServicesAvailable();
        }
        break;
      case REQUEST_AUTHORIZATION:
        if (resultCode == Activity.RESULT_OK) {
          AsyncLoadCalendars.run(this);
        } else {
          chooseAccount();
        }
        break;
      case REQUEST_ACCOUNT_PICKER:
        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
          String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null) {
            credential.setSelectedAccountName(accountName);
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.commit();
            AsyncLoadCalendars.run(this);
          }
        }
        break;
      case ADD_OR_EDIT_CALENDAR_REQUEST:
        if (resultCode == Activity.RESULT_OK) {
          Calendar calendar = new Calendar();
          calendar.setSummary(data.getStringExtra("summary"));
          String id = data.getStringExtra("id");
          if (id == null) {
            new AsyncInsertCalendar(this, calendar).execute();
          } else {
            calendar.setId(id);
            new AsyncUpdateCalendar(this, id, calendar).execute();
          }
        }
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.menu_refresh:
        AsyncLoadCalendars.run(this);
        break;
      case R.id.menu_accounts:
        chooseAccount();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, CONTEXT_EDIT, 0, R.string.edit);
    menu.add(0, CONTEXT_DELETE, 0, R.string.delete);
    menu.add(0, CONTEXT_BATCH_ADD, 0, R.string.batchadd);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    int calendarIndex = (int) info.id;
    if (calendarIndex < adapter.getCount()) {
      final CalendarInfo calendarInfo = adapter.getItem(calendarIndex);
      switch (item.getItemId()) {
        case CONTEXT_EDIT:
          startAddOrEditCalendarActivity(calendarInfo);
          return true;
        case CONTEXT_DELETE:
          new AlertDialog.Builder(this).setTitle(R.string.delete_title)
              .setMessage(calendarInfo.summary)
              .setCancelable(false)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                  new AsyncDeleteCalendar(CalendarSampleActivity.this, calendarInfo).execute();
                }
              })
              .setNegativeButton(R.string.no, null)
              .create()
              .show();
          return true;
        case CONTEXT_BATCH_ADD:
          List<Calendar> calendars = new ArrayList<Calendar>();
          for (int i = 0; i < 3; i++) {
            Calendar cal = new Calendar();
            cal.setSummary(calendarInfo.summary + " [" + (i + 1) + "]");
            calendars.add(cal);
          }
          new AsyncBatchInsertCalendars(this, calendars).execute();
          return true;
      }
    }
    return super.onContextItemSelected(item);
  }

  public void onAddClick(View view) {
    startAddOrEditCalendarActivity(null);
  }

  /** Check that Google Play services APK is installed and up to date. */
  private boolean checkGooglePlayServicesAvailable() {
    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
      showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
      return false;
    }
    return true;
  }

  private void haveGooglePlayServices() {
    // check if there is already an account selected
    if (credential.getSelectedAccountName() == null) {
      // ask user to choose account
      chooseAccount();
    } else {
      // load calendars
      AsyncLoadCalendars.run(this);
    }
  }

  private void chooseAccount() {
    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
  }

  private void startAddOrEditCalendarActivity(CalendarInfo calendarInfo) {
    Intent intent = new Intent(this, AddOrEditCalendarActivity.class);
    if (calendarInfo != null) {
      intent.putExtra("id", calendarInfo.id);
      intent.putExtra("summary", calendarInfo.summary);
    }
    startActivityForResult(intent, ADD_OR_EDIT_CALENDAR_REQUEST);
  }
}
