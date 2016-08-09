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
import com.google.api.client.http.json.JsonHttpClient.Builder;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Common utilities.
 * 
 * @author Sriram Saroop
 */
public class CloudEndpointUtils {

  /**
   * Updates the Google client builder to connect the appropriate server based on the URL specified
   * in {@code assets/debugging_prefs.properties}.
   * 
   * @param builder Google client builder
   * @return same Google client builder
   */
  public static <B extends GoogleClient.Builder> B updateBuilder(B builder) {
    String url = getUrl(builder);
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
  public static void showError(final Activity activity, final String message) {
    activity.runOnUiThread(new Runnable() {
      public void run() {
        new AlertDialog.Builder(activity).setTitle("Error").setMessage(message)
            .setNeutralButton("ok", null).create().show();
      }
    });
  }

  /**
   * Reads debugging_prefs.properties to check if debug URL is specified and returns the root url
   * with it. Else returns the default base URL to be used in remote run.
   */
  private static String getUrl(Builder builder) {
    String url = null;
    InputStream is = null;
    try {
      is =
          GCMIntentService.class.getClassLoader().getResourceAsStream("debugging_prefs.properties");
      if (is == null) {
        return builder.getRootUrl();
      }
      Properties props = new Properties();
      props.load(is);
      url = props.getProperty("url");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (url == null) {
      return builder.getRootUrl();
    }
    return url + "/_ah/api/";
  }
}
