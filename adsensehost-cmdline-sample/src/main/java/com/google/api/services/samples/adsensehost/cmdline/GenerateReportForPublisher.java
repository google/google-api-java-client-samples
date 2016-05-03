/*
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

package com.google.api.services.samples.adsensehost.cmdline;

import com.google.api.services.adsensehost.AdSenseHost;
import com.google.api.services.adsensehost.AdSenseHost.Accounts.Reports.Generate;
import com.google.api.services.adsensehost.model.Report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * This example retrieves a report for the specified publisher ad client.
 *
 * Note that the statistics returned in these reports only include data from ad
 * units created with the AdSense Host API v4.x.
 *
 * To create ad units, see AddAdUnitToPublisher.java.
 * To get ad clients, see GetAllAdClientsForPublisher.java.
 *
 * Tags: accounts.reports.generate
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class GenerateReportForPublisher {

  static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param accountId the ID of the publisher account on which to run the report.
   * @param adClientId the ID of the ad client on which to run the report.
   * @throws Exception
   */
  public static void run(AdSenseHost service, String accountId, String adClientId)
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
    Generate request = service.accounts().reports().generate(accountId, startDate, endDate);

    // Specify the desired ad client using a filter.
    request.setFilter(Arrays.asList("AD_CLIENT_ID==" + escapeFilterParameter(adClientId)));

    request.setMetric(Arrays.asList("PAGE_VIEWS", "AD_REQUESTS", "AD_REQUESTS_COVERAGE", "CLICKS",
        "AD_REQUESTS_CTR", "COST_PER_CLICK", "AD_REQUESTS_RPM", "EARNINGS"));
    request.setDimension(Arrays.asList("DATE"));

    // Sort by ascending date.
    request.setSort(Arrays.asList("+DATE"));

    // Run report.
    Report response = request.execute();

    if ((response.getRows() != null) && !response.getRows().isEmpty()) {
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
   * Escape special characters for a parameter being used in a filter.
   *
   * @param parameter the parameter to be escaped.
   * @return the escaped parameter.
   */
  public static String escapeFilterParameter(String parameter) {
    return parameter.replace("\\", "\\\\").replace(",", "\\,");
  }
}
