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
import com.google.api.services.dfareporting.model.Report;
import com.google.api.services.dfareporting.model.ReportList;

/**
 * This example gets all reports available to the given user profile.
 *
 * Tags: reports.list
 *
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class GetAllReports {

  /**
   * Lists all available reports for the given user profile.
   *
   * @param reporting Dfareporting service object on which to run the requests.
   * @param userProfileId The ID number of the DFA user profile to run this
   *     request as.
   * @param maxPageSize The maximum page size to retrieve.
   * @throws Exception
   */
  public static void list(Dfareporting reporting, Long userProfileId, int maxPageSize)
      throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all reports");
    System.out.println("=================================================================");


    // Retrieve account list in pages and display data as we receive it.
    String pageToken = null;
    ReportList reports = null;
    do {
      reports = reporting.reports().list(userProfileId)
          .setMaxResults(maxPageSize)
          .setPageToken(pageToken)
          .execute();

      for (Report report : reports.getItems()) {
        System.out.printf("Report with ID \"%s\" and display name \"%s\" was found.%n",
            report.getId(), report.getName());
      }

      pageToken = reports.getNextPageToken();
    } while ((reports.getItems() != null) && !reports.getItems().isEmpty());

    System.out.println();
  }
}
