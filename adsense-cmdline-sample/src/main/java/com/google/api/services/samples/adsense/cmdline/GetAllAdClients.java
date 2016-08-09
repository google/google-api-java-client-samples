/*
 * Copyright (c) 2011 Google Inc.
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

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.AdClient;
import com.google.api.services.adsense.model.AdClients;

/**
*
* This example gets all ad clients for the logged in user's default account.
*
* Tags: adclients.list
*
* @author sgomes@google.com (Sérgio Gomes)
*
*/
public class GetAllAdClients {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the last page of retrieved ad clients.
   * @throws Exception
   */
  public static AdClients run(AdSense adsense, int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all ad clients for default account");
    System.out.println("=================================================================");

    // Retrieve ad client list in pages and display data as we receive it.
    String pageToken = null;
    AdClients adClients = null;
    do {
      adClients = adsense.adclients().list()
          .setMaxResults(maxPageSize)
          .setPageToken(pageToken)
          .execute();

      if (adClients.getItems() != null && !adClients.getItems().isEmpty()) {
        for (AdClient adClient : adClients.getItems()) {
          System.out.printf("Ad client for product \"%s\" with ID \"%s\" was found.\n",
              adClient.getProductCode(), adClient.getId());
          System.out.printf("\tSupports reporting: %s\n",
              adClient.getSupportsReporting() ? "Yes" : "No");
        }
      } else {
        System.out.println("No ad clients found.");
      }

      pageToken = adClients.getNextPageToken();
    } while (pageToken != null);

    System.out.println();

    // Return the last page of ad clients, so that the main sample has something to run.
    return adClients;
  }
}
