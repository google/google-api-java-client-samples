/*
 * Copyright (c) 2012 Google Inc.
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

package com.google.api.services.samples.adexchangebuyer.cmdline;

import com.google.api.services.adexchangebuyer.Adexchangebuyer;
import com.google.api.services.adexchangebuyer.model.Account;
import com.google.api.services.adexchangebuyer.model.Account.BidderLocation;

import java.io.IOException;
import java.util.List;

/**
 * This sample illustrates how to update an account.
 *
 * Tags: accounts.list
 *
 * @author david.t@google.com (David Torres)
 *
 */
public class UpdateAccount extends BaseSample {
  /*
   * (non-Javadoc)
   *
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#getName()
   */
  @Override
  public String getName() {
    return "Update account";
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#getDescription()
   */
  @Override
  public String getDescription() {
    return "Updates an account";
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#execute(com.google.api.
   * services.adexchangebuyer.Adexchangebuyer)
   */
  @Override
  public void execute(Adexchangebuyer client) throws IOException {
    int accountId = getIntInput("AccountId", "Enter the account id");
    String cookieMatchingUrl =
        getStringInput("CookieMatchingUrl", "Enter new cookie matching URL for the account");
    Account account = new Account();
    account.setId(accountId);
    account.setCookieMatchingUrl(cookieMatchingUrl);
    account = client.accounts().patch(accountId, account).execute();

    System.out.printf("Account updated!");
    System.out.printf("Account id: %d\n", account.getId());
    System.out.printf("- Max. total Qps: %d\n", account.getMaximumTotalQps());
    System.out.printf("- Cookie matching Nid: %s\n", account.getCookieMatchingNid());
    System.out.printf("- Cookie Matching Url: %s\n", account.getCookieMatchingUrl());
    List<BidderLocation> bidderLocations = account.getBidderLocation();
    if (bidderLocations != null && bidderLocations.size() > 0) {
      System.out.printf("- Bidder locations:\n");
      for (BidderLocation bidderLocation : bidderLocations) {
        System.out.printf("  - Bidder location Url: %s\n", bidderLocation.getUrl());
        System.out.printf("  - Bidder location Max. Qps: %d\n", bidderLocation.getMaximumQps());
      }
    } else {
      System.out.printf("- With no configured bidder locations\n");
    }
  }
}
