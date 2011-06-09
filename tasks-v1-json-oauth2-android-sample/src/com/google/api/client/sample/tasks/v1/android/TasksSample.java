package com.google.api.client.sample.tasks.v1.android;

import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.tasks.v1.Tasks;
import com.google.api.services.tasks.v1.model.Task;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ListActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample for Tasks API on Android. 
 * It shows how to authenticate using OAuth, and get the list of tasks.
 * <p>
 * To enable logging of HTTP requests/responses, change {@link #LOGGING_LEVEL} to
 * {@link Level#CONFIG} or {@link Level#ALL} and run this command:
 * </p>
 *
 * <pre>
adb shell setprop log.tag.HttpTransport DEBUG
 * </pre>
 *
 * @author Johan Euphrosine (derived from Yaniv Inbar's Buzz sample)
 */
public class TasksSample extends ListActivity {

  /** Logging level for HTTP requests/responses. */
  private static Level LOGGING_LEVEL = Level.OFF;

  private static final String TAG = "TasksSample";
  private static final String AUTH_TOKEN_TYPE = "oauth2:https://www.googleapis.com/auth/tasks";
  private static final String PREF = "MyPrefs";
  private static final int DIALOG_ACCOUNTS = 0;
  private static final int MENU_ACCOUNTS = 0;
  public static final int REQUEST_AUTHENTICATE = 0;

  private final HttpTransport transport = AndroidHttp.newCompatibleTransport();

  final Tasks service = new Tasks("Google-TasksSample/1.0", transport, new JacksonFactory());

  // TODO: save auth token in preferences
  public String authToken;
  public GoogleAccountManager accountManager;

  // Follow API access Instructions in instructions.html
  // And replace INSERT_YOUR_API_KEY with your generated API Key.
  static final String API_KEY = "INSERT_YOUR_API_KEY";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    System.out.println("onCreate");
    super.onCreate(savedInstanceState);
    service.accessKey = API_KEY;
    accountManager = new GoogleAccountManager(this);
    Logger.getLogger("com.google.api.client").setLevel(LOGGING_LEVEL);
    gotAccount(false);
  }

  @Override
  protected Dialog onCreateDialog(int id) {
    switch (id) {
      case DIALOG_ACCOUNTS:
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select a Google account");
        final Account[] accounts = accountManager.getAccounts();
        final int size = accounts.length;
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
          names[i] = accounts[i].name;
        }
        builder.setItems(names, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            gotAccount(accounts[which]);
          }
        });
        return builder.create();
    }
    return null;
  }

  void gotAccount(boolean tokenExpired) {
    SharedPreferences settings = getSharedPreferences(PREF, 0);
    String accountName = settings.getString("accountName", null);
    Account account = accountManager.getAccountByName(accountName);
    if (account != null) {
      if (tokenExpired) {
        accountManager.invalidateAuthToken(authToken);
        authToken = null;
      }
      gotAccount(account);
      return;
    }
    showDialog(DIALOG_ACCOUNTS);
  }

  void gotAccount(final Account account) {
    SharedPreferences settings = getSharedPreferences(PREF, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString("accountName", account.name);
    editor.commit();
    accountManager.manager.getAuthToken(
        account, AUTH_TOKEN_TYPE, true, new AccountManagerCallback<Bundle>() {

          public void run(AccountManagerFuture<Bundle> future) {
            try {
              Bundle bundle = future.getResult();
              if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_AUTHENTICATE);
              } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                onAuthToken();
              }
            } catch (Exception e) {
              handleException(e);
            }
          }
        }, null);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, MENU_ACCOUNTS, 0, "Switch Account");
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_ACCOUNTS:
        showDialog(DIALOG_ACCOUNTS);
        return true;
    }
    return false;
  }

  void handleException(Exception e) {
    e.printStackTrace();
    if (e instanceof HttpResponseException) {
      HttpResponse response = ((HttpResponseException) e).response;
      int statusCode = response.statusCode;
      try {
        response.ignore();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      // TODO: should only try this once to avoid infinite loop
      if (statusCode == 401) {
        gotAccount(true);
        return;
      }
    }
    Log.e(TAG, e.getMessage(), e);
  }

  void onAuthToken() {
    new GoogleAccessProtectedResource(authToken) {

      @Override
      protected void onAccessToken(String accessToken) {
        gotAccount(true);
      }
    };
    service.setAccessToken(authToken);
    try {
      List<String> tasks = new ArrayList<String>();
      for (Task task : service.tasks.list("@default").execute().items) {
        tasks.add(task.title);
      }
      setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, tasks));
    } catch (IOException e) {
      handleException(e);
    }
    setContentView(R.layout.main);
  }
}
