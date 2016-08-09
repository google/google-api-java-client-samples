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

import com.appspot.api.services.deviceinfoendpoint.Deviceinfoendpoint;
import com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

/**
 * Receive a push message from the Cloud to Device Messaging (C2DM) service. This class should be
 * modified to include functionality specific to your application. This class must have a no-arg
 * constructor and pass the sender id to the superclass constructor.
 * 
 * @author Sriram Saroop
 */
public class GCMIntentService extends GCMBaseIntentService {

  private Deviceinfoendpoint endpoint;
  private static final String PROJECT_ID = "816816158367";

  /**
   * Register the device for GCM.
   * 
   * @param mContext the activity's context.
   */
  public static void register(Context mContext) {
    GCMRegistrar.checkDevice(mContext);
    GCMRegistrar.checkManifest(mContext);
    GCMRegistrar.register(mContext, PROJECT_ID);
  }

  public GCMIntentService() {
    super(PROJECT_ID);
  }

  private Deviceinfoendpoint getDeviceinfoendpoint() {
    if (endpoint == null) {
      Deviceinfoendpoint.Builder builder =
          new Deviceinfoendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(),
              null);
      endpoint = Utils.updateBuilder(getResources(), builder).build();
    }
    return endpoint;
  }

  /**
   * Called on registration error. This is called in the context of a Service - no dialog or UI.
   * 
   * @param context the Context
   * @param errorId an error message
   */
  @Override
  public void onError(Context context, String errorId) {
  }

  /**
   * Called when a cloud message has been received.
   */
  @Override
  public void onMessage(Context context, Intent intent) {
    String id = intent.getStringExtra("id");
    String operation = intent.getStringExtra("operation");
    String emailAddress = intent.getStringExtra("emailAddress");
    Log.i(CloudNotesActivity.TAG, "id=" + id + ", operation=" + operation + ", emailAddress="
        + emailAddress);
    NoteApplication app = (NoteApplication) getApplication();
    if (emailAddress.equals(app.getEmailAddress())) {
      app.notifyListener(id, operation);
    }
  }

  /**
   * Called when a registration token has been received.
   * 
   * @param context the Context
   */
  @Override
  public void onRegistered(Context context, String registration) {
    try {
      Log.i(CloudNotesActivity.TAG, "Registered Device Start:" + registration);
      getDeviceinfoendpoint().insertDeviceInfo(
          new DeviceInfo().setDeviceRegistrationID(registration)).execute();
      Log.i(CloudNotesActivity.TAG, "Registered Device End:" + registration);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Called when the device has been unregistered.
   * 
   * @param context the Context
   */
  @Override
  protected void onUnregistered(Context context, String registrationId) {
  }
}
