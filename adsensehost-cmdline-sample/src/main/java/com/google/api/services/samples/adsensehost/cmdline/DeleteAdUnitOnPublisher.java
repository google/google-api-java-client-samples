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
import com.google.api.services.adsensehost.model.AdUnit;

/**
 *
 * This example deletes an ad unit on a publisher ad client.
 *
 * To get ad clients, see GetAllAdClientsForPublisher.java.
 * To get ad units, see GetAllAdUnitsForPublisher.java.
 *
 * Tags: accounts.adunits.delete
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class DeleteAdUnitOnPublisher {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param accountId the ID for the publisher account to be used.
   * @param adClientId the ID for the ad client to be used.
   * @param adUnitId the ID of the ad unit to be deleted.
   * @throws Exception
   */
  public static void run(AdSenseHost service, String accountId, String adClientId,
      String adUnitId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Deleting ad unit %s\n", adUnitId);
    System.out.println("=================================================================");

    // Delete ad unit.
    AdUnit adUnit = service.accounts().adunits().delete(accountId, adClientId, adUnitId).execute();

    System.out.printf("Ad unit with id \"%s\" was deleted.\n", adUnit.getId());

    System.out.println();
  }
}
