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

import android.app.Application;
import android.content.Context;

/**
 * @author Sriram Saroop
 */
public class NoteApplication extends Application {
  interface TaskListener {
    void onTaskUpdated(String id, String operation);
  }

  private TaskListener listener;
  private NoteAdapter adapter;
  private String emailAddress;

  public void setTaskListener(TaskListener listener) {
    this.listener = listener;
  }

  public NoteAdapter getAdapter(Context context) {
    if (adapter == null) {
      adapter = new NoteAdapter(context);
    }

    return adapter;
  }

  public void notifyListener(String id, String operation) {
    if (listener != null) {
      listener.onTaskUpdated(id, operation);
    }
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getEmailAddress() {
    return emailAddress;
  }
}
