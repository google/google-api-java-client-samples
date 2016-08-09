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

import com.google.api.client.util.DateTime;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.Dfareporting.DimensionValues.Query;
import com.google.api.services.dfareporting.model.DimensionValue;
import com.google.api.services.dfareporting.model.DimensionValueList;
import com.google.api.services.dfareporting.model.DimensionValueRequest;

/**
 * This example gets the first page of a particular type of dimension available for reporting in
 * the given date range. You can use a similar workflow to retrieve the values for any dimension.
 *
 * Tags: dimensionValues.query
 *
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class GetDimensionValues {

  /**
   * Lists the first page of results for a dimension value.
   *
   * @param reporting Dfareporting service object on which to run the requests.
   * @param dimensionName The name of the dimension to retrieve values for.
   * @param userProfileId The ID number of the DFA user profile to run this request as.
   * @param startDate Values which existed after this start date will be returned.
   * @param endDate Values which existed before this end date will be returned.
   * @param maxPageSize The maximum page size to retrieve.
   * @return the list of values received.
   * @throws Exception
   */
  public static DimensionValueList query(Dfareporting reporting, String dimensionName,
      Long userProfileId, String startDate, String endDate, int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing available %s values%n", dimensionName);
    System.out.println("=================================================================");

    // Create a dimension value query which selects available dimension values.
    DimensionValueRequest request = new DimensionValueRequest();
    request.setDimensionName(dimensionName);
    request.setStartDate(new DateTime(startDate));
    request.setEndDate(new DateTime(endDate));
    Query dimensionQuery = reporting.dimensionValues().query(userProfileId, request);
    dimensionQuery.setMaxResults(maxPageSize);

    // Retrieve values and display them.
    DimensionValueList values = dimensionQuery.execute();

    if ((values.getItems() != null) && !values.getItems().isEmpty()) {
      for (DimensionValue value : values.getItems()) {
        System.out.printf("%s with value \"%s\" was found.%n", dimensionName,
            value.getValue());
      }
    } else {
      System.out.println("No values found.");
    }

    System.out.println();
    return values;
  }
}
