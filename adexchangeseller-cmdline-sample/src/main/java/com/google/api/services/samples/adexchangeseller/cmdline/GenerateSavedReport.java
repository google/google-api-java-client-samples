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

package com.google.api.services.samples.adexchangeseller.cmdline;

import com.google.api.services.adexchangeseller.AdExchangeSeller;
import com.google.api.services.adexchangeseller.AdExchangeSeller.Reports.Saved.Generate;
import com.google.api.services.adexchangeseller.model.Report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This example retrieves a saved report for the default account.
 *
 * <p>Tags: reports.saved.generate
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class GenerateSavedReport {

  static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Runs this sample.
   *
   * @param adExchangeSeller AdExchangeSeller service object on which to run the requests.
   * @param savedReportId the saved report ID on which to run the report.
   * @throws Exception
   */
  public static void run(AdExchangeSeller adExchangeSeller, String savedReportId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Running saved report %s\n", savedReportId);
    System.out.println("=================================================================");

    // Prepare report.
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);
    calendar.add(Calendar.DATE, -7);
    Generate request = adExchangeSeller.reports().saved().generate(savedReportId);

    // Run saved report.
    Report response = request.execute();

    if (response.getRows() != null && !response.getRows().isEmpty()) {
      // Display headers.
      for (Report.Headers header : response.getHeaders()) {
        System.out.printf("%25s", header.getName());
      }
      System.out.println();

      // Display results.
      for (List<String> row : response.getRows()) {
        for (String column : row) {
          System.out.printf("%25s", column);
        }
        System.out.println();
      }

      System.out.println();
    } else {
      System.out.println("No rows returned.");
    }

    System.out.println();
  }

  /**
   * Escape special characters for a parameter being used in a filter.new.
   *
   * @param parameter the parameter to be escaped.
   * @return the escaped parameter.
   */
  public static String escapeFilterParameter(String parameter) {
    return parameter.replace("\\", "\\\\").replace(",", "\\,");
  }
}
