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

package com.google.api.services.samples.dfareporting.cmdline;

import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.Dfareporting.DimensionValues.Query;
import com.google.api.services.dfareporting.model.DimensionValue;
import com.google.api.services.dfareporting.model.DimensionValueList;
import com.google.api.services.dfareporting.model.DimensionValueRequest;

/**
 * This example gets the first page of advertisers available for reporting in
 * the given date range. Advertisers are just one of the dimensions you can
 * query against. You can use a similar workflow to retrieve the values for
 * other dimensions.
 *
 * Tags: dimensionValues.query
 *
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class GetAdvertisers {

  /**
   * Lists the first page of advertisers.
   *
   * @param reporting Dfareporting service object on which to run the requests.
   * @return the list of user profiles received.
   * @throws Exception
   */
  public static DimensionValueList query(Dfareporting reporting, Long userProfileId,
      String startDate, String endDate, int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all available advertisers");
    System.out.println("=================================================================");

    // Create a dimension value query which selects all available advertisers.
    DimensionValueRequest request = new DimensionValueRequest();
    request.setDimensionName("dfa:advertiser");
    request.setStartDate(startDate);
    request.setEndDate(endDate);
    Query dimensionQuery = reporting.dimensionValues().query(userProfileId, request);
    dimensionQuery.setMaxResults(maxPageSize);

    // Retrieve advertisers and display them.
    DimensionValueList advertisers = dimensionQuery.execute();

    if ((advertisers.getItems() != null) && !advertisers.getItems().isEmpty()) {
      for (DimensionValue advertiser : advertisers.getItems()) {
        System.out.printf("Advertiser with name \"%s\" was found.%n",
            advertiser.getValue());
      }
    } else {
      System.out.println("No advertisers found.");
    }

    System.out.println();
    return advertisers;
  }
}
