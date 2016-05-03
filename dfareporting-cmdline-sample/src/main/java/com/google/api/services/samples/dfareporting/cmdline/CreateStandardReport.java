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
import com.google.api.services.dfareporting.model.DateRange;
import com.google.api.services.dfareporting.model.DimensionValue;
import com.google.api.services.dfareporting.model.Report;
import com.google.api.services.dfareporting.model.SortedDimension;
import com.google.common.collect.ImmutableList;

/**
 * This example creates a simple standard report for the given advertiser.
 *
 * Tags: reports.insert
 *
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class CreateStandardReport {

  /**
   * Inserts (creates) a simple standard report for a given advertiser.
   *
   * @param reporting Dfareporting service object on which to run the requests.
   * @param userProfileId The ID number of the DFA user profile to run this request as.
   * @param advertiser The advertiser who the report is about.
   * @param startDate The starting date of the report.
   * @param endDate The ending date of the report.
   * @return the newly created report.
   * @throws Exception
   */
  public static Report insert(Dfareporting reporting, Long userProfileId, DimensionValue advertiser,
      String startDate, String endDate) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Creating a new standard report for advertiser %s%n", advertiser.getValue());
    System.out.println("=================================================================");

    // Create a report.
    Report report = new Report();
    report.setName(String.format("API Report: Advertiser %s", advertiser.getValue()));
    report.setFileName("api_report_files");
    report.setType("STANDARD");

    // Create criteria.
    Report.Criteria criteria = new Report.Criteria();
    criteria.setDateRange(new DateRange().setStartDate(new DateTime(startDate))
        .setEndDate(new DateTime(endDate)));
    criteria.setDimensions(ImmutableList.of(new SortedDimension().setName("dfa:advertiser")));
    criteria.setMetricNames(ImmutableList.of(
        "dfa:clicks",
        "dfa:impressions"));
    criteria.setDimensionFilters(ImmutableList.of(advertiser));

    report.setCriteria(criteria);
    Report result = reporting.reports().insert(userProfileId, report).execute();
    System.out.printf("Created report with ID \"%s\" and display name \"%s\"%n", result.getId(),
        result.getName());
    System.out.println();
    return result;
  }
}
