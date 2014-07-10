/*
 * Copyright (c) 2013 Google Inc.
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

package com.google.api.services.samples.adexchangeseller.cmdline;

import com.google.api.services.adexchangeseller.AdExchangeSeller;
import com.google.api.services.adexchangeseller.model.Alert;
import com.google.api.services.adexchangeseller.model.Alerts;

/**
*
* Gets all alerts available for the logged in user's account.
*
* <p>Tags: alerts.list
*
* @author sgomes@google.com (Sérgio Gomes)
*
*/
public class GetAllAlerts {

  /**
   * Runs this sample.
   *
   * @param adExchangeSeller AdExchangeSeller service object on which to run the requests.
   * @throws Exception
   */
  public static void run(AdExchangeSeller adExchangeSeller) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all alerts for account");
    System.out.println("=================================================================");

    // Retrieve and display alerts.
    Alerts alerts = adExchangeSeller.alerts().list().execute();

    if (alerts.getItems() != null && !alerts.getItems().isEmpty()) {
      for (Alert alert : alerts.getItems()) {
        System.out.printf("Alert id \"%s\" with severity \"%s\" and type \"%s\" was found.\n",
            alert.getId(), alert.getSeverity(), alert.getType());
      }
    } else {
      System.out.println("No alerts found.");
    }

    System.out.println();
  }
}
