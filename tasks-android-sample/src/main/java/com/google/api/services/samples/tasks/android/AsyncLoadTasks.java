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

package com.google.api.services.samples.tasks.android;

import com.google.api.services.tasks.model.Task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Asynchronously load the tasks with a progress dialog.
 * 
 * @author Yaniv Inbar
 */
class AsyncLoadTasks extends AsyncTask<Void, Void, List<String>> {

  private final TasksSample tasksSample;
  private final ProgressDialog dialog;
  private com.google.api.services.tasks.Tasks service;

  AsyncLoadTasks(TasksSample tasksSample) {
    this.tasksSample = tasksSample;
    service = tasksSample.service;
    dialog = new ProgressDialog(tasksSample);
  }

  @Override
  protected void onPreExecute() {
    dialog.setMessage("Loading tasks...");
    dialog.show();
  }

  @Override
  protected List<String> doInBackground(Void... arg0) {
    try {
      List<String> result = new ArrayList<String>();
      com.google.api.services.tasks.Tasks.TasksOperations.List listRequest =
          service.tasks().list("@default");
      listRequest.setFields("items/title");
      List<Task> tasks = listRequest.execute().getItems();
      if (tasks != null) {
        for (Task task : tasks) {
          result.add(task.getTitle());
        }
      } else {
        result.add("No tasks.");
      }
      return result;
    } catch (IOException e) {
      tasksSample.handleGoogleException(e);
      return Collections.singletonList(e.getMessage());
    } finally {
      tasksSample.onRequestCompleted();
    }
  }

  @Override
  protected void onPostExecute(List<String> result) {
    dialog.dismiss();
    tasksSample.setListAdapter(
        new ArrayAdapter<String>(tasksSample, android.R.layout.simple_list_item_1, result));
  }
}
