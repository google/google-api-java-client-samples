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
import com.google.api.services.adsensehost.model.AdStyle;
import com.google.api.services.adsensehost.model.AdStyle.Colors;
import com.google.api.services.adsensehost.model.AdUnit;

/**
 *
 * This example updates an ad unit on a publisher ad client.
 *
 * To get ad clients, see GetAllAdClientsForPublisher.java.
 * To get ad units, see GetAllAdUnitsForPublisher.java.
 *
 * Tags: accounts.adunits.patch
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class UpdateAdUnitOnPublisher {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param accountId the ID of the pub account on which the ad unit exists.
   * @param adClientId the ID of the ad client on which the ad unit exists.
   * @param adUnitId the ID of the ad unit to be updated.
   * @return the updated ad unit.
   * @throws Exception
   */
  public static AdUnit run(AdSenseHost service, String accountId, String adClientId,
      String adUnitId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Updating ad unit %s\n", adUnitId);
    System.out.println("=================================================================");

    AdUnit patchAdUnit = new AdUnit()
        .setCustomStyle(new AdStyle()
            .setColors(new Colors()
                .setText("ff0000")));

    // Create ad unit.
    AdUnit adUnit = service.accounts().adunits().patch(accountId, adClientId, adUnitId,
        patchAdUnit).execute();

    System.out.printf("Ad unit with ID \"%s\" was updated with text color \"%s\".\n",
        adUnit.getId(), adUnit.getCustomStyle().getColors().getText());

    System.out.println();

    // Return the created ad unit.
    return adUnit;
  }
}
