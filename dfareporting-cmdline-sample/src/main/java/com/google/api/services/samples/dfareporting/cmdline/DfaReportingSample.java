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

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.DimensionValue;
import com.google.api.services.dfareporting.model.DimensionValueList;
import com.google.api.services.dfareporting.model.File;
import com.google.api.services.dfareporting.model.Report;
import com.google.api.services.dfareporting.model.UserProfileList;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;
import com.google.common.collect.ImmutableList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A sample application that runs multiple requests against the DFA Reporting API. These include:
 * <ul>
 * <li>Listing all DFA user profiles for a user</li>
 * <li>Listing the first 50 available advertisers for a user profile</li>
 * <li>Creating a new report</li>
 * <li>Listing all available reports</li>
 * <li>Generating a new report file from a report</li>
 * <li>Downloading the contents of a report file</li>
 * </ul>
 *
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class DfaReportingSample {

  private static final List<String> SCOPES = ImmutableList.of(
      "https://www.googleapis.com/auth/dfareporting",
      "https://www.googleapis.com/auth/devstorage.read_only");

  private static final HttpTransport TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
  private static final int MAX_LIST_PAGE_SIZE = 50;
  private static final int MAX_REPORT_PAGE_SIZE = 10;


  /**
   * Performs all necessary setup steps for running requests against the API.
   *
   * @return An initialized Dfareporting service object.
   * @throws Exception
   */
  private static Dfareporting initializeDfareporting() throws Exception {
    Credential credential =
        OAuth2Native.authorize(TRANSPORT, JSON_FACTORY, new LocalServerReceiver(), SCOPES);

    // Create DFA Reporting client.
    return new Dfareporting(TRANSPORT, JSON_FACTORY, credential);
  }

  /**
   * Runs all the DFA Reporting API samples.
   *
   * @param args command-line arguments.
   */
  public static void main(String[] args) {
    // Set up the date range we plan to use.
    Date today = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(today);
    calendar.add(Calendar.DATE, -7);
    Date oneWeekAgo = calendar.getTime();

    String startDate = DATE_FORMATTER.format(oneWeekAgo);
    String endDate = DATE_FORMATTER.format(today);

    try {
      try {
        Dfareporting reporting = initializeDfareporting();

        UserProfileList userProfiles = GetAllUserProfiles.list(reporting);
        // Get an example user profile ID, so we can run the following samples.
        Long userProfileId = userProfiles.getItems().get(0).getProfileId();

        DimensionValueList advertisers =
            GetAdvertisers.query(reporting, userProfileId, startDate, endDate, MAX_LIST_PAGE_SIZE);

        if ((advertisers.getItems() != null) && !advertisers.getItems().isEmpty()) {
          // Get an advertiser, so we can run the rest of the samples.
          DimensionValue advertiser = advertisers.getItems().get(0);

          Report report =
              CreateReport.insert(reporting, userProfileId, advertiser, startDate, endDate);
          GetAllReports.list(reporting, userProfileId, MAX_REPORT_PAGE_SIZE);
          File file = GenerateReportFile.run(reporting, userProfileId, report);

          if (file != null) {
            // If the report file generation did not fail, display results.
            DownloadReportFile.run(reporting, file);
          }
        }
      } catch (GoogleJsonResponseException e) {
        // Message already includes parsed response.
        System.err.println(e.getMessage());
      } catch (HttpResponseException e) {
        // Message doesn't include parsed response.
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }
}
