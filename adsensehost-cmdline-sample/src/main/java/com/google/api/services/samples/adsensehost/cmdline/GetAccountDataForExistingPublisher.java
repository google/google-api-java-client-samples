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
import com.google.api.services.adsensehost.model.Account;
import com.google.api.services.adsensehost.model.Accounts;

import java.util.Arrays;

/**
 *
 * This example finds the account data for a publisher from their ad client ID.
 *
 * Tags: accounts.list
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class GetAccountDataForExistingPublisher {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param adClientId the publisher ad client ID for which to get account data.
   * @return the retrieved account info.
   * @throws Exception
   */
  public static Accounts run(AdSenseHost service, String adClientId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing publisher account for \"%s\"\n", adClientId);
    System.out.println("=================================================================");

    // Retrieve account data.
    Accounts accounts = service.accounts().list(Arrays.asList(adClientId)).execute();

    if ((accounts.getItems() != null) && !accounts.getItems().isEmpty()) {
      for (Account account : accounts.getItems()) {
        System.out.printf("Account with ID \"%s\", name \"%s\" and status \"%s\" was found.\n",
            account.getId(), account.getName(), account.getStatus());
      }
    } else {
      System.out.println("No accounts found.");
    }

    System.out.println();
    return accounts;
  }
}
