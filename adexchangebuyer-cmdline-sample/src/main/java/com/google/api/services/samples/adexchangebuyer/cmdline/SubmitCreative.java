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
import com.google.api.services.adexchangebuyer.model.Creative;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This sample illustrates how to submit a new creative to the Google's verification pipeline.
 *
 * Tags: creatives.insert
 *
 * @author david.t@google.com (David Torres)
 */
public class SubmitCreative extends BaseSample {
  /*
   * (non-Javadoc)
   *
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#execute()
   */
  @Override
  public void execute(Adexchangebuyer client) throws IOException {
    int accountId = getIntInput("AccountId", "Enter the creative account id");
    String buyerCreativeId = getStringInput("BuyerCreativeId", "Enter the buyer creative id");
    String advertiserName = getStringInput("AdvertiserName", "Enter the advertiser name");
    String clickThroughUrlsStr = getStringInput(
        "ClickThroughUrls", "Enter a comma separated list of clickthrough urls",
        "http://www.google.com");
    String htmlSnippet = getStringInput("HtmlSnippet", "Enter the Ad HTML code snippet",
        "<html><body><a href='http://www.google.com'>Hi there!</a></body></html>");
    long width = getLongInput("AdWidth", "Enter the creative width", 300L);
    long height = getLongInput("AdHeight", "Enter the creative height", 250L);
    Long agencyId = getOptionalLongInput("AgencyId", "Enter the agency id (optional)");

    Creative testCreative = new Creative();
    testCreative.setAccountId(accountId);
    testCreative.setAdvertiserName(advertiserName);
    testCreative.setBuyerCreativeId(buyerCreativeId);
    List<String> clickThroughUrls = new ArrayList<String>();
    Collections.addAll(clickThroughUrls, clickThroughUrlsStr.split("\\s*,\\s*"));
    testCreative.setClickThroughUrl(clickThroughUrls);
    testCreative.setHTMLSnippet(htmlSnippet);
    testCreative.setHeight((int) height);
    testCreative.setWidth((int) width);
    testCreative.setAgencyId(agencyId);

    Creative response = client.creatives().insert(testCreative).execute();

    System.out.println("========================================");
    System.out.println("Submitted creative");
    System.out.println("========================================");
    System.out.println("Account id: " + response.getAccountId());
    System.out.println("Buyer Creative id: " + response.getBuyerCreativeId());
    System.out.println("Advertiser id: " + response.getAdvertiserId());
    System.out.println("Agency id: " + response.getAgencyId());
    System.out.println("Status: " + response.getStatus());
    System.out.println("Product categories: " + response.getProductCategories());
    System.out.println("Sensitive categories: " + response.getSensitiveCategories());
    System.out.println("Width: " + response.getWidth());
    System.out.println("Height: " + response.getHeight());
    System.out.println("HTML Snippet: " + response.getHTMLSnippet());
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#getName()
   */
  @Override
  public String getName() {
    return "Submit Creative";
  }

  /*
   * (non-Javadoc)
   *
   * @see com.google.api.services.samples.adexchangebuyer.cmdline.BaseSample#getDescription()
   */
  @Override
  public String getDescription() {
    return "Submits a new creative to Google's creative verification pipeline";
  }
}
