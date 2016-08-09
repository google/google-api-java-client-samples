/*
 * Copyright (c) 2013 Google Inc.
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
import com.google.api.services.dfareporting.model.CompatibleFields;
import com.google.api.services.dfareporting.model.Dimension;
import com.google.api.services.dfareporting.model.Metric;
import com.google.api.services.dfareporting.model.Report;
import com.google.api.services.dfareporting.model.ReportCompatibleFields;

/**
 * This example fetches and displays the compatible fields for the given standard report. There
 * are two reasons why metrics and/or dimensions may be incompatible with your report:
 *
 * <ul><li>Your user profile doesn't have permission to view those fields</li>
 * <li>Your report contains a field which is incompatible with certain other fields</li></ul>
 *
 * Tags: compatibleFields.query
 *
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class GetCompatibleFields {

  /**
   * Fetches and displays the compatible fields for the given standard report.
   *
   * @param reporting Dfareporting service object on which to run the requests.
   * @param userProfileId The ID number of the DFA user profile to run this request as.
   * @param report Displays additional fields compatible with this report.
   */
  public static void run(Dfareporting reporting, Long userProfileId, Report report)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Getting compatible fields for standard report with ID %s%n",
        report.getId());
    System.out.println("=================================================================");

    CompatibleFields compatibleFields = reporting.reports().compatibleFields()
        .query(userProfileId, report).execute();

    // Since this is a standard report, we check the "reportCompatibleFields" property.
    // For other reports, we would check that report type's specific property.
    ReportCompatibleFields standardReportCompatibleFields =
        compatibleFields.getReportCompatibleFields();

    for (Dimension compatibleDimension : standardReportCompatibleFields.getDimensions()) {
      System.out.printf("Dimension \"%s\" is compatible.%n", compatibleDimension.getName());
    }

    for (Metric compatibleMetric : standardReportCompatibleFields.getMetrics()) {
      System.out.printf("Metric \"%s\" is compatible.%n", compatibleMetric.getName());
    }

    for (Dimension compatibleDimension : standardReportCompatibleFields.getDimensionFilters()) {
      System.out.printf("Dimension Filter \"%s\" is compatible.%n", compatibleDimension.getName());
    }

    for (Metric compatibleMetric : standardReportCompatibleFields.getPivotedActivityMetrics()) {
      System.out.printf("Pivoted Activity Metric \"%s\" is compatible.%n",
          compatibleMetric.getName());
    }

    System.out.println();
  }
}
