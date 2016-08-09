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
import com.google.api.services.adsensehost.model.AdStyle.Font;
import com.google.api.services.adsensehost.model.AdUnit;
import com.google.api.services.adsensehost.model.AdUnit.ContentAdsSettings;
import com.google.api.services.adsensehost.model.AdUnit.ContentAdsSettings.BackupOption;

/**
 *
 * This example adds a new ad unit to a publisher ad client.
 *
 * To get ad clients, see GetAllAdClientsForPublisher.java.
 *
 * Tags: accounts.adunits.insert
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class AddAdUnitToPublisher {

  /**
   * Runs this sample.
   *
   * @param service AdSensehost service object on which to run the requests.
   * @param accountId the ID for the publisher account to be used.
   * @param adClientId the ID for the ad client to be used.
   * @return the created ad unit.
   * @throws Exception
   */
  public static AdUnit run(AdSenseHost service, String accountId, String adClientId)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Adding ad unit to ad client %s\n", adClientId);
    System.out.println("=================================================================");

    AdUnit newAdUnit = new AdUnit()
        .setName("Ad Unit #" + AdSenseHostSample.getUniqueName())
        .setContentAdsSettings(new ContentAdsSettings()
            .setBackupOption(new BackupOption()
                .setType("COLOR")
                .setColor("ffffff"))
            .setSize("SIZE_200_200")
            .setType("TEXT"))
        .setCustomStyle(new AdStyle()
            .setColors(new Colors()
                .setBackground("ffffff")
                .setBorder("000000")
                .setText("000000")
                .setTitle("000000")
                .setUrl("0000ff"))
            .setCorners("SQUARE")
            .setFont(new Font()
                .setFamily("ACCOUNT_DEFAULT_FAMILY")
                .setSize("ACCOUNT_DEFAULT_SIZE")));

    // Create ad unit.
    AdUnit adUnit = service.accounts().adunits().insert(accountId, adClientId, newAdUnit)
        .execute();

    System.out.printf("Ad unit of type \"%s\", name \"%s\" and status \"%s\" was created.\n",
        adUnit.getContentAdsSettings().getType(), adUnit.getName(), adUnit.getStatus());

    System.out.println();

    // Return the created ad unit.
    return adUnit;
  }
}
