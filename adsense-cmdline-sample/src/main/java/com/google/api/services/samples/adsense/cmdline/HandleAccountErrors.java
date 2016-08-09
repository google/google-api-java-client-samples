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

package com.google.api.services.samples.adsense.cmdline;

import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.adsense.AdSense;

import java.util.List;

/**
 *
 * This example shows how to handle different AdSense account errors.
 *
 * Tags: adclients.list
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class HandleAccountErrors {

  /**
   * Runs this sample.
   *
   * @param service AdSense service object on which to run the requests.
   * @param maxPageSize the maximum page size to retrieve.
   * @throws Exception
   */
  public static void run(AdSense service, int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Testing error handling");
    System.out.println("=================================================================");

    try {
      // Attempt API call.
      service.adclients().list().setMaxResults(maxPageSize).execute();

      System.out.println("The call succeeded. Please use an invalid, disapproved or " +
          "approval-pending AdSense account to test error handling.");

      System.out.println();
    } catch (GoogleJsonResponseException e) {
      // Handle a few known API errors. See full list at
      // https://developers.google.com/adsense/management/v1.1/reference/#errors
      List<ErrorInfo> errors = e.getDetails().getErrors();
      for (ErrorInfo error : errors) {
        if (error.getReason().equals("noAdSenseAccount")) {
          System.out.println("Error handled! No AdSense account for this user.");
        } else if (error.getReason().equals("disapprovedAccount")) {
          System.out.println("Error handled! This account is disapproved.");
        } else if (error.getReason().equals("accountPendingReview")) {
          System.out.println("Error handled! This account is pending review.");
        } else {
          // Unrecognized reason, so let's use the error message returned by the API.
          System.out.println("Unrecognized error, showing system message:");
          System.out.println(error.getMessage());
        }
      }
    }
  }
}
