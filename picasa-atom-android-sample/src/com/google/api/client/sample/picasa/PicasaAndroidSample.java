/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.picasa;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;

import com.google.api.client.apache.ApacheHttpTransport;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.sample.picasa.model.AlbumEntry;
import com.google.api.client.sample.picasa.model.PicasaUrl;
import com.google.api.client.sample.picasa.model.UserFeed;
import com.google.api.client.sample.picasa.model.Util;
import com.google.api.client.util.DateTime;
import com.google.api.client.xml.atom.AtomParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample for Picasa Web Albums Data API using the Atom wire format. It shows
 * how to authenticate, get albums, add a new album, update it, and delete it.
 * <p>
 * It also demonstrates how to upload a photo to the "Drop Box" album. To see
 * this example in action, take a picture, click "Share", and select "Picasa
 * Basic Android Sample".
 * </p>
 * <p>
 * To enable logging of HTTP requests/responses, run this command: {@code adb
 * shell setprop log.tag.HttpTransport DEBUG}. Then press-and-hold an album, and
 * enable "Logging".
 * </p>
 *
 * @author Yaniv Inbar
 */
public final class PicasaAndroidSample extends ListActivity {

  private static final String AUTH_TOKEN_TYPE = "lh2";

  private static final String TAG = "PicasaAndroidSample";

  private static final int MENU_ADD = 0;

  private static final int MENU_ACCOUNTS = 1;

  private static final int CONTEXT_EDIT = 0;

  private static final int CONTEXT_DELETE = 1;

  private static final int CONTEXT_LOGGING = 2;

  private static final int REQUEST_AUTHENTICATE = 0;

  private static final String PREF = "MyPrefs";

  private static final int DIALOG_ACCOUNTS = 0;

  private static HttpTransport transport;

  private String authToken;

  private String postLink;

  private final List<AlbumEntry> albums = new ArrayList<AlbumEntry>();

  public PicasaAndroidSample() {
    HttpTransport.setLowLevelHttpTransport(ApacheHttpTransport.INSTANCE);
    transport = GoogleTransport.create();
    GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
    headers.setApplicationName("google-picasaandroidsample-1.0");
    headers.gdataVersion = "2";
    AtomParser parser = new AtomParser();
    parser.namespaceDictionary = Util.NAMESPACE_DICTIONARY;
    transport.addParser(parser);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    SharedPreferences settings = getSharedPreferences(PREF, 0);
    setLogging(settings.getBoolean("logging", false));
    getListView().setTextFilterEnabled(true);
    registerForContextMenu(getListView());
    Intent intent = getIntent();
    if (Intent.ACTION_SEND.equals(intent.getAction())) {
      sendData = new SendData(intent, getContentResolver());
    } else if (Intent.ACTION_MAIN.equals(getIntent().getAction())) {
      sendData = null;
    }
    gotAccount(false);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case DIALOG_ACCOUNTS:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Google account");
        final AccountManager manager = AccountManager.get(this);
        final Account[] accounts = manager.getAccountsByType("com.google");
        final int size = accounts.length;
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
          names[i] = accounts[i].name;
        }
        builder.setItems(names, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            gotAccount(manager, accounts[which]);
          }
        });
        return builder.create();
    }
    return null;
  }

  private void gotAccount(boolean tokenExpired) {
    SharedPreferences settings = getSharedPreferences(PREF, 0);
    String accountName = settings.getString("accountName", null);
    if (accountName != null) {
      AccountManager manager = AccountManager.get(this);
      Account[] accounts = manager.getAccountsByType("com.google");
      int size = accounts.length;
      for (int i = 0; i < size; i++) {
        Account account = accounts[i];
        if (accountName.equals(account.name)) {
          if (tokenExpired) {
            manager.invalidateAuthToken("com.google", this.authToken);
          }
          gotAccount(manager, account);
          return;
        }
      }
    }
    showDialog(DIALOG_ACCOUNTS);
  }

  private void gotAccount(final AccountManager manager, final Account account) {
    SharedPreferences settings = getSharedPreferences(PREF, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString("accountName", account.name);
    editor.commit();
    new Thread() {

      @Override
      public void run() {
        try {
          final Bundle bundle =
              manager.getAuthToken(account, AUTH_TOKEN_TYPE, true, null, null)
                  .getResult();
          runOnUiThread(new Runnable() {

            public void run() {
              try {
                if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                  Intent intent =
                      bundle.getParcelable(AccountManager.KEY_INTENT);
                  int flags = intent.getFlags();
                  flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
                  intent.setFlags(flags);
                  startActivityForResult(intent, REQUEST_AUTHENTICATE);
                } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                  authenticatedClientLogin(
                      bundle.getString(AccountManager.KEY_AUTHTOKEN));
                }
              } catch (Exception e) {
                handleException(e);
              }
            }
          });
        } catch (Exception e) {
          handleException(e);
        }
      }
    }.start();
  }

  @Override
  protected void onActivityResult(
      int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode) {
      case REQUEST_AUTHENTICATE:
        if (resultCode == RESULT_OK) {
          gotAccount(false);
        } else {
          showDialog(DIALOG_ACCOUNTS);
        }
        break;
    }
  }

  private void authenticatedClientLogin(String authToken) {
    this.authToken = authToken;
    ((GoogleHeaders) transport.defaultHeaders).setGoogleLogin(authToken);
    authenticated();
  }

  static class SendData {
    String fileName;
    Uri uri;
    String contentType;
    long contentLength;

    SendData(Intent intent, ContentResolver contentResolver) {
      Bundle extras = intent.getExtras();
      if (extras.containsKey(Intent.EXTRA_STREAM)) {
        Uri uri = this.uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
        String scheme = uri.getScheme();
        if (scheme.equals("content")) {
          Cursor cursor = contentResolver.query(uri, null, null, null, null);
          cursor.moveToFirst();
          this.fileName = cursor.getString(
              cursor.getColumnIndexOrThrow(Images.Media.DISPLAY_NAME));
          this.contentType = intent.getType();
          this.contentLength =
              cursor.getLong(cursor.getColumnIndexOrThrow(Images.Media.SIZE));
        }
      }
    }
  }

  static SendData sendData;

  private void authenticated() {
    if (sendData != null) {
      try {
        if (sendData.fileName != null) {
          boolean success = false;
          try {
            HttpRequest request = transport.buildPostRequest();
            request.url = PicasaUrl.relativeToRoot(
                "feed/api/user/default/albumid/default");
            ((GoogleHeaders) request.headers).setSlugFromFileName(
                sendData.fileName);
            InputStreamContent content = new InputStreamContent();
            content.inputStream =
                getContentResolver().openInputStream(sendData.uri);
            content.type = sendData.contentType;
            content.length = sendData.contentLength;
            request.content = content;
            request.execute().ignore();
            success = true;
          } catch (IOException e) {
            handleException(e);
          }
          setListAdapter(new ArrayAdapter<String>(this,
              android.R.layout.simple_list_item_1,
              new String[] {success ? "OK" : "ERROR"}));
        }
      } finally {
        sendData = null;
      }
    } else {
      executeRefreshAlbums();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_ADD, 0, "New album");
    menu.add(0, MENU_ACCOUNTS, 0, "Switch Account");
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_ADD:
        AlbumEntry album = new AlbumEntry();
        album.access = "private";
        album.title = "Album " + new DateTime(new Date());
        try {
          AlbumEntry.executeInsert(transport, album, this.postLink);
        } catch (IOException e) {
          handleException(e);
        }
        executeRefreshAlbums();
        return true;
      case MENU_ACCOUNTS:
        showDialog(DIALOG_ACCOUNTS);
        return true;
    }
    return false;
  }

  @Override
  public void onCreateContextMenu(
      ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, CONTEXT_EDIT, 0, "Update Title");
    menu.add(0, CONTEXT_DELETE, 0, "Delete");
    SharedPreferences settings = getSharedPreferences(PREF, 0);
    boolean logging = settings.getBoolean("logging", false);
    menu.add(0, CONTEXT_LOGGING, 0, "Logging").setCheckable(true).setChecked(
        logging);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    AlbumEntry album = albums.get((int) info.id);
    try {
      switch (item.getItemId()) {
        case CONTEXT_EDIT:
          AlbumEntry patchedAlbum = album.clone();
          patchedAlbum.title =
              album.title + " UPDATED " + new DateTime(new Date());
          patchedAlbum.executePatchRelativeToOriginal(transport, album);
          executeRefreshAlbums();
          return true;
        case CONTEXT_DELETE:
          album.executeDelete(transport);
          executeRefreshAlbums();
          return true;
        case CONTEXT_LOGGING:
          SharedPreferences settings = getSharedPreferences(PREF, 0);
          boolean logging = settings.getBoolean("logging", false);
          setLogging(!logging);
          return true;
        default:
          return super.onContextItemSelected(item);
      }
    } catch (IOException e) {
      handleException(e);
    }
    return false;
  }

  private void executeRefreshAlbums() {
    String[] albumNames;
    List<AlbumEntry> albums = this.albums;
    albums.clear();
    try {
      PicasaUrl url = PicasaUrl.relativeToRoot("feed/api/user/default");
      // page through results
      while (true) {
        UserFeed userFeed = UserFeed.executeGet(transport, url);
        this.postLink = userFeed.getPostLink();
        if (userFeed.albums != null) {
          albums.addAll(userFeed.albums);
        }
        String nextLink = userFeed.getNextLink();
        if (nextLink == null) {
          break;
        }
      }
      int numAlbums = albums.size();
      albumNames = new String[numAlbums];
      for (int i = 0; i < numAlbums; i++) {
        albumNames[i] = albums.get(i).title;
      }
    } catch (IOException e) {
      handleException(e);
      albumNames = new String[] {e.getMessage()};
      albums.clear();
    }
    setListAdapter(new ArrayAdapter<String>(
        this, android.R.layout.simple_list_item_1, albumNames));
  }

  private void setLogging(boolean logging) {
    Logger.getLogger("com.google.api.client").setLevel(
        logging ? Level.CONFIG : Level.OFF);
    SharedPreferences settings = getSharedPreferences(PREF, 0);
    boolean currentSetting = settings.getBoolean("logging", false);
    if (currentSetting != logging) {
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean("logging", logging);
      editor.commit();
    }
  }

  private void handleException(Exception e) {
    e.printStackTrace();
    SharedPreferences settings = getSharedPreferences(PREF, 0);
    boolean log = settings.getBoolean("logging", false);
    if (e instanceof HttpResponseException) {
      HttpResponse response = ((HttpResponseException) e).response;
      int statusCode = response.statusCode;
      try {
        response.ignore();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      if (statusCode == 401 || statusCode == 403) {
        gotAccount(true);
        return;
      }
      if (log) {
        try {
          Log.e(TAG, response.parseAsString());
        } catch (IOException parseException) {
          parseException.printStackTrace();
        }
      }
    }
    if (log) {
      Log.e(TAG, e.getMessage(), e);
    }
  }
}
