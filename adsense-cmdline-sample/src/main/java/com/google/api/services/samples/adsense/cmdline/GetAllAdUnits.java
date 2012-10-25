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
import com.google.api.services.adsense.model.AdUnit;
import com.google.api.services.adsense.model.AdUnits;

/**
*
* This example gets all ad units in an ad client.
*
* Tags: adunits.list
*
* @author sergio.gomes@google.com (Sérgio Gomes)
*
*/
public class GetAllAdUnits {

  /**
   * Runs this sample.
   *
   * @param adsense Adsense service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the last page of ad units.
   * @throws Exception
   */
  public static AdUnits run(AdSense adsense, String adClientId, int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all ad units for ad client %s\n", adClientId);
    System.out.println("=================================================================");

    // Retrieve ad unit list in pages and display data as we receive it.
    String pageToken = null;
    AdUnits adUnits = null;
    do {
      adUnits = adsense.adunits().list(adClientId)
          .setMaxResults(maxPageSize)
          .setPageToken(pageToken)
          .execute();

      if ((adUnits.getItems() != null) && !adUnits.getItems().isEmpty()) {
        for (AdUnit unit : adUnits.getItems()) {
          System.out.printf("Ad unit with code \"%s\", name \"%s\" and status \"%s\" was found.\n",
              unit.getCode(), unit.getName(), unit.getStatus());
        }
      } else {
        System.out.println("No ad units found.");
      }

      pageToken = adUnits.getNextPageToken();
    } while (pageToken != null);

    System.out.println();

    // Return the last page of ad units, so that the main sample has something to run.
    return adUnits;
  }
}
