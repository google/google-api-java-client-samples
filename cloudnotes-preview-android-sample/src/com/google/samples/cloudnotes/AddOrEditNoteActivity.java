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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * @author Sriram Saroop
 */
public class AddOrEditNoteActivity extends Activity {

  private TextView titleTextView;
  private String id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.addtask);
    titleTextView = (TextView) findViewById(R.id.titleText);
    id = getIntent().getStringExtra("id");
    if (id != null) {
      titleTextView.setText(getIntent().getStringExtra("task"));
    }
  }

  public void onSave(View view) {
    String taskName = titleTextView.getText().toString();
    if (taskName.length() > 0) {
      Intent t = new Intent();
      if (id != null) {
        t.putExtra("id", id);
      }
      t.putExtra("task", taskName);
      setResult(Activity.RESULT_OK, t);
    } else {
      setResult(Activity.RESULT_CANCELED);
    }
    finish();
  }

  public void onCancel(View view) {
    setResult(Activity.RESULT_CANCELED);
    finish();
  }
}
