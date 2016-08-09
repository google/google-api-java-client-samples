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
import com.google.api.services.adsense.model.CustomChannel;
import com.google.api.services.adsense.model.CustomChannels;

/**
*
* This example gets all custom channels in an ad client.
*
* Tags: customchannels.list
*
* @author sgomes@google.com (Sérgio Gomes)
*
*/
public class GetAllCustomChannels {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the last page of custom channels.
   * @throws Exception
   */
  public static CustomChannels run(AdSense adsense, String adClientId, int maxPageSize)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all custom channels for ad client %s\n", adClientId);
    System.out.println("=================================================================");

    // Retrieve custom channel list in pages and display the data as we receive it.
    String pageToken = null;
    CustomChannels customChannels = null;
    do {
      customChannels = adsense.customchannels().list(adClientId)
          .setMaxResults(maxPageSize)
          .setPageToken(pageToken)
          .execute();

      if (customChannels.getItems() != null && !customChannels.getItems().isEmpty()) {
        for (CustomChannel channel : customChannels.getItems()) {
          System.out.printf("Custom channel with code \"%s\" and name \"%s\" was found.\n",
              channel.getCode(), channel.getName());
        }
      } else {
        System.out.println("No custom channels found.");
      }

      pageToken = customChannels.getNextPageToken();
    } while (pageToken != null);

    System.out.println();
    return customChannels;
  }
}
