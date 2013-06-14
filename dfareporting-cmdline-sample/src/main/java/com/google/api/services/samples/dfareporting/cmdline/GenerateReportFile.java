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
import com.google.api.services.dfareporting.model.File;
import com.google.api.services.dfareporting.model.Report;

/**
 * This example generates a report file from a report.
 *
 * Tags: reports.run
 *
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class GenerateReportFile {

  private static final Integer MAX_POLLING_ATTEMPTS = 4;
  private static final Integer POLL_TIME_INCREMENT = 30000; // 30s

  /**
   * Requests the generation of a new report file from a given report.
   *
   * @param reporting Dfareporting service object on which to run the requests.
   * @param userProfileId The ID number of the DFA user profile to run this request as.
   * @return the generated report file. Will return {@code null} if the report fails to be generated
   *         successfully.
   * @throws Exception
   */
  public static File run(
      Dfareporting reporting, Long userProfileId, Report report, Boolean isSynchronous)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Generating a report file for report with ID %s%n", report.getId());
    System.out.println("=================================================================");

    File reportFile = reporting.reports()
        .run(userProfileId, report.getId()).setSynchronous(isSynchronous).execute();
    System.out.println("Report execution initiated. Checking for completion...");

    reportFile = waitForReportRunCompletion(reporting, userProfileId, reportFile);

    if (!reportFile.getStatus().equals("REPORT_AVAILABLE")) {
      System.out.printf(
          "Report file generation failed to finish. Final status is: %s%n", reportFile.getStatus());
      return null;
    }

    System.out.printf("Report file with ID \"%s\" generated.%n", reportFile.getId());
    System.out.println();
    return reportFile;
  }

  /**
   * Waits for a report file to generate with exponential back-off.
   *
   * @param reporting Dfareporting service object on which to run the requests.
   * @param userProfileId The ID number of the DFA user profile to run this request as.
   * @param file The report file to poll the status of.
   * @return the report file object, either once it is no longer processing or once too much time
   *         has passed.
   * @throws Exception
   */
  private static File waitForReportRunCompletion(
      Dfareporting reporting, long userProfileId, File file) throws Exception {

    Integer interval;
    for (int i = 0; i <= MAX_POLLING_ATTEMPTS; i++) {
      if (!file.getStatus().equals("PROCESSING")) {
        break;
      }

      interval = (int) (POLL_TIME_INCREMENT * (Math.pow(1.6, i)));
      System.out.printf("Polling again in %s ms.%n", interval);
      Thread.sleep(interval);
      file = reporting.reports()
          .files().get(userProfileId, file.getReportId(), file.getId()).execute();
    }
    return file;
  }
}
