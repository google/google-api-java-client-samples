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
import com.google.api.services.adexchangeseller.model.PreferredDeal;
import com.google.api.services.adexchangeseller.model.PreferredDeals;

/**
*
* Gets all preferred deals available on the logged in user's account.
*
* <p>Tags: preferreddeals.list
*
* @author sgomes@google.com (Sérgio Gomes)
*
*/
public class GetAllPreferredDeals {

  /**
   * Runs this sample.
   *
   * @param adExchangeSeller AdExchangeSeller service object on which to run the requests.
   * @throws Exception
   */
  public static void run(AdExchangeSeller adExchangeSeller) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all preferred deals on account");
    System.out.println("=================================================================");

    // Retrieve and display preferred deals.
    PreferredDeals deals = adExchangeSeller.preferreddeals().list().execute();

    if (deals.getItems() != null && !deals.getItems().isEmpty()) {
      for (PreferredDeal deal : deals.getItems()) {
        System.out.printf("Deal id \"%s\" ", deal.getId());

        if (deal.getAdvertiserName() != null) {
          System.out.printf("for advertiser \"%s\" ", deal.getAdvertiserName());
        }

        if (deal.getBuyerNetworkName() != null) {
          System.out.printf("on network \"%s\" ", deal.getBuyerNetworkName());
        }

        System.out.println("was found.");
      }
    } else {
      System.out.println("No preferred deals found.");
    }

    System.out.println();
  }
}
