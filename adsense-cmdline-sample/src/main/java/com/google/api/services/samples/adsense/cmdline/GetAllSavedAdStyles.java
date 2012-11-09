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
import com.google.api.services.adsense.model.SavedAdStyle;
import com.google.api.services.adsense.model.SavedAdStyles;

/**
 *
 * This example gets all saved ad styles for the default account.
 *
 * Tags: savedadstyles.list
 *
 * @author jalc@google.com (Jose Alcérreca)
 *
 */
public class GetAllSavedAdStyles {

  /**
   * Runs this sample.
   *
   * @param adsense Adsense service object on which to run the requests.
   * @param maxPageSize the maximum page size to retrieve.
   * @return the last page of ad styles.
   * @throws Exception
   */
  public static SavedAdStyles run(AdSense adsense, int maxPageSize) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Listing all saved ad styles for default account\n");
    System.out.println("=================================================================");

    // Retrieve saved ad style list and display the data as we receive it.
    String pageToken = null;
    SavedAdStyles savedAdStyles = null;
    do {
      savedAdStyles = adsense.savedadstyles()
          .list()
          .setMaxResults(maxPageSize)
          .setPageToken(pageToken)
          .execute();

      if ((savedAdStyles.getItems() != null) && !savedAdStyles.getItems().isEmpty()) {
        for (SavedAdStyle savedAdStyle : savedAdStyles.getItems()) {
          System.out.printf("Saved ad style with name \"%s\" was found.\n", savedAdStyle.getName());
        }
      } else {
        System.out.println("No saved ad styles found.");
      }

      pageToken = savedAdStyles.getNextPageToken();
    } while (pageToken != null);

    System.out.println();
    return savedAdStyles;
  }
}
