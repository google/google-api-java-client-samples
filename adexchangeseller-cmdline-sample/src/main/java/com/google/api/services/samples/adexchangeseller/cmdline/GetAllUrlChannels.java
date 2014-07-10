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

package com.google.api.services.samples.adexchangeseller.cmdline;

import com.google.api.services.adexchangeseller.AdExchangeSeller;
import com.google.api.services.adexchangeseller.model.UrlChannel;
import com.google.api.services.adexchangeseller.model.UrlChannels;

/**
*
* This example gets all URL channels in an ad client.
*
* <p>Tags: urlchannels.list
*
* @author sgomes@google.com (Sérgio Gomes)
*
*/
public class GetAllUrlChannels {

  /**
   * Runs this sample.
   *
   * @param adExchangeSeller AdExchangeSeller service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @param maxPageSize the maximum page size to retrieve.
   * @throws Exception
   */
  public static void run(AdExchangeSeller adExchangeSeller, String adClientId, long maxPageSize)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all URL channels for ad client %s\n", adClientId);
    System.out.println("=================================================================");

    // Retrieve URL channel list in pages and display the data as we receive it.
    String pageToken = null;
    do {
      UrlChannels urlChannels = adExchangeSeller.urlchannels().list(adClientId)
          .setMaxResults(maxPageSize)
          .setPageToken(pageToken)
          .execute();

      if (urlChannels.getItems() != null && !urlChannels.getItems().isEmpty()) {
        for (UrlChannel channel : urlChannels.getItems()) {
          System.out.printf("URL channel with URL pattern \"%s\" was found.\n",
              channel.getUrlPattern());
        }
      } else {
        System.out.println("No URL channels found.");
      }

      pageToken = urlChannels.getNextPageToken();
    } while (pageToken != null);

    System.out.println();
  }
}
