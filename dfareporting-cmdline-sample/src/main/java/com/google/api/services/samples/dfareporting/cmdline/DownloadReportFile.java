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

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.util.Charsets;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.File;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This example downloads the contents of a report file.
 *
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class DownloadReportFile {

  /**
   * Fetchs the contents of a report file.
   *
   * @param reporting Dfareporting service object on which to run the requests.
   * @param reportFile The completed report file to download.
   * @throws Exception
   */
  public static void run(Dfareporting reporting, File reportFile)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Retrieving and printing a report file for report with ID %s%n",
        reportFile.getReportId());
    System.out.printf("The ID number of this report file is %s%n", reportFile.getId());
    System.out.println("=================================================================");

    String url = reportFile.getUrls().getApiUrl();
    GenericUrl genericUrl = new GenericUrl(url);
    HttpRequestFactory httpRequestFactory = reporting.getRequestFactory();
    HttpRequest httpRequest = httpRequestFactory.buildGetRequest(genericUrl);
    HttpResponse response = httpRequest.execute();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(response.getContent(), Charsets.UTF_8));
    String line;
    while ((line = reader.readLine()) != null) {
      System.out.println(line);
    }
  }
}
