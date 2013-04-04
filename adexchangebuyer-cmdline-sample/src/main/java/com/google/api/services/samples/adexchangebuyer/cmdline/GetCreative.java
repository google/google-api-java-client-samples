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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.adexchangebuyer.Adexchangebuyer;
import com.google.api.services.adexchangebuyer.model.Creative;
import com.google.api.services.adexchangebuyer.model.Creative.DisapprovalReasons;

import java.io.IOException;

/**
 * This sample illustrates how to retrieve a creative out of the system, including its status.
 *
 * Tags: creatives.get
 *
 * @author david.t@google.com (David Torres)
 */
public class GetCreative extends BaseSample {
  @Override
  public void execute(Adexchangebuyer client) throws IOException {
    int accountId = getIntInput("AccountId", "Enter the creative account id");
    String buyerCreativeId = getStringInput("BuyerCreativeId", "Enter the buyer creative id");

    try {
      Creative creative = client.creatives().get(accountId, buyerCreativeId).execute();

      System.out.println("========================================");
      System.out.println("Found creative");
      System.out.println("========================================");
      System.out.println("Account id: " + creative.getAccountId());
      System.out.println("Buyer Creative id: " + creative.getBuyerCreativeId());
      System.out.println("Advertiser id: " + creative.getAdvertiserId());
      System.out.println("Agency id: " + creative.getAgencyId());
      System.out.println("Status: " + creative.getStatus());
      if (creative.getDisapprovalReasons() != null) {
        for (DisapprovalReasons disapprovalReason : creative.getDisapprovalReasons()) {
          System.out.println("\tDisapproval Reason: " + disapprovalReason.getReason());
          for (String disapprovalReasonDetail : disapprovalReason.getDetails()) {
            System.out.println("\t\tDetail: " + disapprovalReasonDetail);
          }
        }
      }
      System.out.println("Product categories: " + creative.getProductCategories());
      System.out.println("Sensitive categories: " + creative.getSensitiveCategories());
      System.out.println("Width: " + creative.getWidth());
      System.out.println("Height: " + creative.getHeight());
      System.out.println("HTML Snippet: " + creative.getHTMLSnippet());
    } catch (GoogleJsonResponseException e) {
      if (e.getDetails().getCode() == 404) {
        System.out.println("Can't find this creative, it can take up to 20 minutes after "
            + "submitting a new creative for the status to be available. Check your input "
            + "parameters");
      } else {
        throw e;
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#getDescription()
   */
  @Override
  public String getDescription() {
    return "Gets the data for a single creative, including its status";
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#getName()
   */
  @Override
  public String getName() {
    return "Get Creative Data";
  }
}
