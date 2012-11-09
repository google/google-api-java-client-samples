/*
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

package com.google.api.services.samples.adsensehost.cmdline;

import com.google.api.services.adsensehost.AdSenseHost;
import com.google.api.services.adsensehost.model.AdClient;
import com.google.api.services.adsensehost.model.AdClients;

/**
 *
 * This example gets all ad clients for the host.
 *
 * Tags: adclients.list
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class GetAllAdClientsForHost {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the last page of retrieved host ad clients.
   * @throws Exception
   */
  public static AdClients run(AdSenseHost service, long maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all ad clients for host account");
    System.out.println("=================================================================");

    // Retrieve ad client list in pages and display data as we receive it.
    String pageToken = null;
    AdClients adClients = null;
    do {
      adClients = service.adclients().list()
          .setMaxResults(maxPageSize)
          .setPageToken(pageToken)
          .execute();

      if ((adClients.getItems() != null) && !adClients.getItems().isEmpty()) {
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
