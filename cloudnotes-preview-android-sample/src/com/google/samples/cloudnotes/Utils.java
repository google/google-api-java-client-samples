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

package com.google.samples.cloudnotes;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.services.GoogleClient;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

/**
 * Common utilities.
 * 
 * @author Yaniv Inbar
 */
public class Utils {

  /**
   * Updates the Google client builder to connect the appropriate server based on the URL specified
   * in {@code assets/debugging_prefs.properties}.
   * 
   * @param builder Google client builder
   * @return same Google client builder
   */
  public static <B extends GoogleClient.Builder> B updateBuilder(Resources resources, B builder) {
    String url = resources.getString(R.string.url);
    // only enable GZip when connecting to remote server
    final boolean enableGZip = url.startsWith("https:");
    builder.setRootUrl(url);
    builder.setJsonHttpRequestInitializer(new JsonHttpRequestInitializer() {
      public void initialize(JsonHttpRequest jsonHttpRequest) {
        jsonHttpRequest.setEnableGZipContent(enableGZip);
      }
    });
    return builder;
  }

  /**
   * Logs the given message and shows an error alert dialog with it.
   * 
   * @param activity activity
   * @param tag log tag to use
   * @param message message to log and show or {@code null} for none
   */
  public static void logAndShow(Activity activity, String tag, String message) {
    Log.e(tag, message);
    showError(activity, message);
  }

  /**
   * Logs the given throwable and shows an error alert dialog with its message.
   * 
   * @param activity activity
   * @param tag log tag to use
   * @param t throwable to log and show
   */
  public static void logAndShow(Activity activity, String tag, Throwable t) {
    Log.e(tag, "Error", t);
    String message = t.getMessage();
    if (t instanceof GoogleJsonResponseException) {
      GoogleJsonError details = ((GoogleJsonResponseException) t).getDetails();
      if (details != null) {
        message = details.getMessage();
      }
    } else if (t.getCause() instanceof GoogleAuthException) {
      message = ((GoogleAuthException) t.getCause()).getMessage();
    }
    showError(activity, message);
  }

  /**
   * Shows an error alert dialog with the given message.
   * 
   * @param activity activity
   * @param message message to show or {@code null} for none
   */
  public static void showError(final Activity activity, final String errorMessage) {
    final String message = errorMessage == null ? "Error" : "[Error]:" + errorMessage;
    activity.runOnUiThread(new Runnable() {
      public void run() {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
      }
    });
  }
}
