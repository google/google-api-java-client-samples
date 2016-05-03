/*
 * Copyright 2012 Google Inc.
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
package com.google.samples.cloudnotes;

import com.appspot.api.services.noteendpoint.Noteendpoint;
import com.appspot.api.services.noteendpoint.model.Note;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.samples.cloudnotes.NoteApplication.TaskListener;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Main activity - requests "Hello, World" messages from the server and provides a menu item to
 * invoke the accounts activity.
 * 
 * @author Sriram Saroop
 */
public class CloudNotesActivity extends Activity {

  /**
   * Logging level for HTTP requests/responses.
   * <p>
   * To turn on, set to {@link Level#CONFIG} or {@link Level#ALL} and run this from command line:
   * {@code adb shell setprop log.tag.HttpTransport DEBUG}.
   * </p>
   */
  private static final Level LOGGING_LEVEL = Level.OFF;

  /** Tag for logging. */
  static final String TAG = "CloudNotesActivity";

  /** List view. */
  private ListView listView;

  /** Tasks adapter. */
  NoteAdapter adapter;

  /** Note endpoint. */
  Noteendpoint noteendpoint;

  /** Google Account credential. */
  GoogleAccountCredential credential;

  private static final String PREF_ACCOUNT_NAME = "accountName";
  static final int REQUEST_AUTHORIZATION = 0;
  static final int REQUEST_ACCOUNT_PICKER = 1;
  private final static int ADD_OR_EDIT_TASK_REQUEST = 2;

  private static final int CONTEXT_EDIT = 0;

  private static final int CONTEXT_DELETE = 1;

  int numAsyncTasks;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // enable logging
    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
    // Enable Google Cloud Messaging
    GCMIntentService.register(getApplicationContext());
    // get the task application to store the adapter which will act as the task
    // storage for this demo
    setContentView(R.layout.tasklist);
    listView = (ListView) findViewById(R.id.list);
    NoteApplication taskApplication = (NoteApplication) getApplication();
    adapter = taskApplication.getAdapter(this);
    listView.setAdapter(adapter);
    registerForContextMenu(listView);

    // Google Accounts
    credential = GoogleAccountCredential.usingAudience(this, Ids.AUDIENCE);
    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
    setAccountName(settings.getString(PREF_ACCOUNT_NAME, null));
    // set up the Note endpoint
    Noteendpoint.Builder builder =
        new Noteendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
            credential);
    noteendpoint = Utils.updateBuilder(getResources(), builder).build();
    // if we already have an account, go ahead and fetch tasks now
    if (credential.getAccountName() != null) {
      fetchTasks();
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
        fetchTasks();
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
    menu.add(0, CONTEXT_EDIT, 0, "Edit");
    menu.add(0, CONTEXT_DELETE, 0, "Delete");
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    int calendarIndex = (int) info.id;
    if (calendarIndex < adapter.getCount()) {
      Note note = adapter.getItem(calendarIndex);
      switch (item.getItemId()) {
        case CONTEXT_EDIT:
          startAddOrEditTaskActivity(note);
          return true;
        case CONTEXT_DELETE:
          new AsyncDeleteNote(this, note).execute();
          return true;
      }
    }
    return super.onContextItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();
    NoteApplication taskApplication = (NoteApplication) getApplication();
    taskApplication.setTaskListener(new TaskListener() {
      public void onTaskUpdated(final String id, String operation) {
        if (operation.equals("remove")) {
          adapter.removeTask(id);
          runOnUiThread(new Runnable() {
            public void run() {
              adapter.notifyDataSetChanged();
            }
          });
        } else {
          runOnUiThread(new Runnable() {
            public void run() {
              new AsyncFetchNote(CloudNotesActivity.this, id).execute();
            }
          });
        }
      }
    });
    if (credential.getAccountName() == null) {
      chooseAccount();
    }
  }

  void chooseAccount() {
    startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
  }

  @Override
  protected void onPause() {
    super.onPause();
    NoteApplication taskApplication = (NoteApplication) getApplication();
    taskApplication.setTaskListener(null);
  }

  public void fetchTasks() {
    new AsyncFetchNotes(this).execute();
  }

  public void onAddClick(View view) {
    startAddOrEditTaskActivity(null);
  }

  private void startAddOrEditTaskActivity(Note note) {
    Intent intent = new Intent(this, AddOrEditNoteActivity.class);
    if (note != null) {
      intent.putExtra("id", note.getId());
      intent.putExtra("task", note.getDescription());
    }
    startActivityForResult(intent, ADD_OR_EDIT_TASK_REQUEST);
  }

  private void setAccountName(String accountName) {
    credential.setAccountName(accountName);
    NoteApplication taskApplication = (NoteApplication) getApplication();
    taskApplication.setEmailAddress(accountName);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_AUTHORIZATION:
        if (resultCode == Activity.RESULT_OK) {
          fetchTasks();
        } else {
          chooseAccount();
        }
        break;
      case REQUEST_ACCOUNT_PICKER:
        if (data != null && data.getExtras() != null) {
          String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null) {
            setAccountName(accountName);
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.commit();
            fetchTasks();
          }
        }
        break;
      case ADD_OR_EDIT_TASK_REQUEST:
        if (resultCode == Activity.RESULT_OK) {
          Note addOrEditTask = new Note();
          addOrEditTask.setDescription(data.getStringExtra("task"));
          String id = data.getStringExtra("id");
          if (id == null) {
            addOrEditTask.setId(Long.toString(System.currentTimeMillis()));
            new AsyncAddNote(this, addOrEditTask).execute();
          } else {
            addOrEditTask.setId(id);
            new AsyncEditNote(this, addOrEditTask).execute();
          }
        }
        break;
    }
  }
}
