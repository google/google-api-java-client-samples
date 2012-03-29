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
import com.google.api.services.adexchangebuyer.model.DirectDeal;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * This sample illustrates how to retrieve all direct deals associated to the user.
 *
 * Tags: directDeals.list
 *
 * @author david.t@google.com (David Torres)
 */
public class GetAllDirectDeals extends BaseSample {
  // Date formatter.
  private static DateFormat dateFormat = DateFormat.getDateTimeInstance();

  /* (non-Javadoc)
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#execute()
   */
  @Override
  public void execute(Adexchangebuyer client) throws IOException {
    List<DirectDeal> allDirectDeals = client.directDeals().list().execute().getDirectDeals();

    if (allDirectDeals != null && allDirectDeals.size() > 0) {
      System.out.println("========================================");
      System.out.println("Listing of user associated direct deals");
      System.out.println("========================================");
      for (DirectDeal directDeal : allDirectDeals) {
        System.out.println("Deal id: " + directDeal.getId());
        System.out.println("- Advertiser: " + directDeal.getAdvertiser());
        System.out.println("- Account id: " + directDeal.getAccountId());
        System.out.println("- Fixed Cpm: " + directDeal.getFixedCpm());
        System.out.println("- Seller network: " + directDeal.getSellerNetwork());
        if (directDeal.getStartTime() != null) {
          Date startTime = new Date(directDeal.getStartTime());
          System.out.println("- Start time: " + dateFormat.format(startTime));
        }
        if (directDeal.getEndTime() != null) {
          Date endTime = new Date(directDeal.getEndTime());
          System.out.println("- End time: " + dateFormat.format(endTime));
        }
      }
    } else {
      System.out.println("No direct deals were found associated to this user");
    }
  }

  /* (non-Javadoc)
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#getName()
   */
  @Override
  public String getName() {
    return "Get All Direct Deals";
  }

  /* (non-Javadoc)
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#getDescription()
   */
  @Override
  public String getDescription() {
    return "Lists all direct deals associated to the user";
  }
}
