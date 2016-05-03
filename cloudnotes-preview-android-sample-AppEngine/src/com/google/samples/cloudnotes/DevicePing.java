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

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Sriram Saroop
 */
public class DevicePing {

  private static final DeviceInfoEndpoint endpoint = new DeviceInfoEndpoint();
  private static final Logger LOG = Logger.getLogger(DevicePing.class.getName());

  /**
   * Sends the message using the Sender object to the registered device.
   * 
   * @param message the message to be sent in the GCM ping to the device.
   * @param sender the Sender object to be used for ping,
   * @param deviceInfo the registration id of the device.
   * @return Result the result of the ping.
   */
  private static Result doSendViaGcm(String message, Sender sender, DeviceInfo deviceInfo)
      throws IOException {
    // Trim message if needed.
    if (message.length() > 1000) {
      message = message.substring(0, 1000) + "[...]";
    }

    Message msg =
        new Message.Builder().addData("message", message).delayWhileIdle(false).timeToLive(0)
            .build();
    Result result = sender.send(msg, deviceInfo.getDeviceRegistrationID(), 5);
    if (result.getMessageId() != null) {
      String canonicalRegId = result.getCanonicalRegistrationId();
      if (canonicalRegId != null) {
        endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
        deviceInfo.setDeviceRegistrationID(canonicalRegId);
        endpoint.insertDeviceInfo(deviceInfo);
      }
    } else {
      String error = result.getErrorCodeName();
      if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
        endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
      }
    }

    return result;
  }

  /**
   * Ping all registered devices with the message.
   * 
   * @param message the message to be sent in the GCM ping to all registered devices.
   */
  public static void pingAllDevices(String message) throws IOException {
    Sender sender = new Sender(Ids.API_KEY);
    for (DeviceInfo deviceInfo : endpoint.listDeviceInfo()) {
      doSendViaGcm(message, sender, deviceInfo);
    }
  }

  /** Ping all registered devices with the message. */
  public static void pingAllDevices(String emailAddress, String id, String operation) {
    Sender sender = new Sender(Ids.API_KEY);
    for (DeviceInfo deviceInfo : endpoint.listDeviceInfo()) {
      Message msg =
          new Message.Builder().addData("id", id).addData("emailAddress", emailAddress)
              .addData("operation", operation).build();

      Result result;
      try {
        result = sender.send(msg, deviceInfo.getDeviceRegistrationID(), 5);
      } catch (IOException e1) {
        LOG.log(Level.WARNING, "gcm", e1);
        continue;
      }
      LOG.log(Level.INFO, "Message ID:" + result.getMessageId());
      LOG.log(Level.INFO, "Error code:" + result.getErrorCodeName());
      String error = result.getErrorCodeName();
      if (result.getMessageId() != null) {
        String canonicalRegId = result.getCanonicalRegistrationId();
        if (canonicalRegId != null) {
          endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
          deviceInfo.setDeviceRegistrationID(canonicalRegId);
          endpoint.insertDeviceInfo(deviceInfo);
        }
      } else if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
        endpoint.removeDeviceInfo(deviceInfo.getDeviceRegistrationID());
      }
    }
  }

}
