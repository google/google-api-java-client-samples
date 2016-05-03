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

package com.google.api.services.samples.adsense.cmdline;

import com.google.api.services.adsense.AdSense;
import com.google.api.services.adsense.model.Metadata;
import com.google.api.services.adsense.model.ReportingMetadataEntry;

/**
*
* Gets all dimensions available for the logged in user's default account.
*
* Tags: metadata.dimensions.list
*
* @author sgomes@google.com (Sérgio Gomes)
*
*/
public class GetAllDimensions {

  /**
   * Runs this sample.
   *
   * @param adsense AdSense service object on which to run the requests.
   * @throws Exception
   */
  public static void run(AdSense adsense) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all dimensions for default account");
    System.out.println("=================================================================");

    // Retrieve and display dimensions.
    Metadata dimensions = adsense.metadata().dimensions().list().execute();

    if (dimensions.getItems() != null && !dimensions.getItems().isEmpty()) {
      for (ReportingMetadataEntry dimension : dimensions.getItems()) {
        boolean firstProduct = true;
        StringBuilder products = new StringBuilder();
        for (String product : dimension.getSupportedProducts()) {
          if (!firstProduct) {
            products.append(", ");
          }
          products.append(product);
          firstProduct = false;
        }
        System.out.printf("Dimension id \"%s\" for product(s): [%s] was found.\n",
            dimension.getId(), products.toString());
      }
    } else {
      System.out.println("No dimensions found.");
    }

    System.out.println();
  }
}
