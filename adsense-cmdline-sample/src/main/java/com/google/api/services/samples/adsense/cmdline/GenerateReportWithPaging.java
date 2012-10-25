/*
 * Copyright (c) 2011 Google Inc.
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

package com.google.api.services.samples.adsense.cmdline;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.AdSense.Reports.Generate;
import com.google.api.services.adsense.model.AdsenseReportsGenerateResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This example retrieves a report for the specified ad client.
 *
 * Please only use pagination if your application requires it due to memory or storage constraints.
 * If you need to retrieve more than 5000 rows, please check GenerateReport.java, as due to current
 * limitations you will not be able to use paging for large reports.
 *
 * Tags: reports.generate
 *
 * @author sergio.gomes@google.com (Sérgio Gomes)
 *
 */
public class GenerateReportWithPaging {

  // Maximum number of obtainable rows for paged reports (API limit).
  private static final int ROW_LIMIT = 5000;

  static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Runs this sample.
   *
   * @param adsense Adsense service object on which to run the requests.
   * @param adClientId the ad client ID on which to run the report.
   * @param maxReportPageSize the maximum size page to retrieve.
   * @throws Exception
   */
  public static void run(AdSense adsense, String adClientId, int maxReportPageSize)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Running report for ad client %s\n", adClientId);
    System.out.println("=================================================================");

    // Prepare report.
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);
    calendar.add(Calendar.DATE, -7);
    Date oneWeekAgo = calendar.getTime();

    String startDate = DATE_FORMATTER.format(oneWeekAgo);
    String endDate = DATE_FORMATTER.format(today);
    Generate request = adsense.reports().generate(startDate, endDate);

    // Specify the desired ad client using a filter.
    request.setFilter(Arrays.asList("AD_CLIENT_ID==" + escapeFilterParameter(adClientId)));

    request.setMetric(Arrays.asList("PAGE_VIEWS", "AD_REQUESTS", "AD_REQUESTS_COVERAGE", "CLICKS",
        "AD_REQUESTS_CTR", "COST_PER_CLICK", "AD_REQUESTS_RPM", "EARNINGS"));
    request.setDimension(Arrays.asList("DATE"));

    // Sort by ascending date.
    request.setSort(Arrays.asList("+DATE"));

    // Run first page of report.
    request.setMaxResults(maxReportPageSize);
    AdsenseReportsGenerateResponse response = request.execute();

    if (response.getRows() == null || response.getRows().isEmpty()) {
      System.out.println("No rows returned.");
      return;
    }

    // The first page, so display headers.
    displayHeaders(response.getHeaders());

    // Display first page results.
    displayRows(response.getRows());

    int totalRows = Math.min(response.getTotalMatchedRows().intValue(), ROW_LIMIT);
    for (int startIndex = response.getRows().size(); startIndex < totalRows;
        startIndex += response.getRows().size()) {

      // Check to see if we're going to go above the limit and get as many results as we can.
      int pageSize = Math.min(maxReportPageSize, totalRows - startIndex);

      request.setStartIndex(startIndex);
      request.setMaxResults(pageSize);

      // Run next page of report.
      response = request.execute();

      // If the report size changes in between paged requests, the result may be empty.
      if (response.getRows() == null || response.getRows().isEmpty()) {
        break;
      }

      // Display results.
      displayRows(response.getRows());
    }

    System.out.println();
  }

  /**
   * Displays the headers for the report.
   * @param headers The list of headers to be displayed.
   */
  private static void displayHeaders(List<AdsenseReportsGenerateResponse.Headers> headers) {
    for (AdsenseReportsGenerateResponse.Headers header : headers) {
      System.out.printf("%25s", header.getName());
    }
    System.out.println();
  }

  /**
   * Displays a list of rows for the report.
   * @param rows The list of rows to display.
   */
  public static void displayRows(List<List<String>> rows) {
    // Display results.
    for (List<String> row : rows) {
      for (String column : row) {
        System.out.printf("%25s", column);
      }
      System.out.println();
    }
  }

  /**
   * Escape special characters for a parameter being used in a filter.
   * @param parameter the parameter to be escaped.
   * @return the escaped parameter.
   */
  public static String escapeFilterParameter(String parameter) {
    return parameter.replace("\\", "\\\\").replace(",", "\\,");
  }
}
