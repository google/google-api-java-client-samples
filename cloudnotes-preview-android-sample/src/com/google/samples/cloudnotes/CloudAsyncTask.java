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

import com.appspot.api.services.noteendpoint.Noteendpoint;
import com.google.android.gms.auth.UserRecoverableAuthException;

import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;

/**
 * Asynchronous task that also takes care of common needs, such as showing a progress dialog,
 * authorization, and exception handling.
 * 
 * @author Yaniv Inbar
 */
abstract class CloudAsyncTask extends AsyncTask<Void, Void, Boolean> {

  final CloudNotesActivity activity;
  final Noteendpoint endpoint;
  final NoteAdapter adapter;
  private final View progressBar;

  CloudAsyncTask(CloudNotesActivity activity) {
    this.activity = activity;
    endpoint = activity.noteendpoint;
    adapter = activity.adapter;
    progressBar = activity.findViewById(R.id.title_refresh_progress);
  }

  @Override
  protected void onPreExecute() {
    activity.numAsyncTasks++;
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  protected final Boolean doInBackground(Void... ignored) {
    try {
      doInBackground();
      return true;
    } catch (IOException e) {
      if (e.getCause() instanceof UserRecoverableAuthException) {
        UserRecoverableAuthException re = (UserRecoverableAuthException) e.getCause();
        activity.startActivityForResult(re.getIntent(), CloudNotesActivity.REQUEST_AUTHORIZATION);
      } else {
        Utils.logAndShow(activity, CloudNotesActivity.TAG, e);
      }
    }
    return false;
  }

  abstract protected void doInBackground() throws IOException;

  @Override
  protected void onPostExecute(Boolean success) {
    if (0 == --activity.numAsyncTasks) {
      progressBar.setVisibility(View.GONE);
    }
    if (success) {
      activity.adapter.notifyDataSetChanged();
    }
  }
}
