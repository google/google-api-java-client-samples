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
import com.google.api.services.adexchangeseller.model.Metadata;
import com.google.api.services.adexchangeseller.model.ReportingMetadataEntry;

/**
*
* Gets all metrics available for the logged in user's account.
*
* <p>Tags: metadata.metrics.list
*
* @author sgomes@google.com (Sérgio Gomes)
*
*/
public class GetAllMetrics {

  /**
   * Runs this sample.
   *
   * @param adExchangeSeller AdExchangeSeller service object on which to run the requests.
   * @throws Exception
   */
  public static void run(AdExchangeSeller adExchangeSeller) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all metrics for account");
    System.out.println("=================================================================");

    // Retrieve and display metrics.
    Metadata metrics = adExchangeSeller.metadata().metrics().list().execute();

    if (metrics.getItems() != null && !metrics.getItems().isEmpty()) {
      for (ReportingMetadataEntry metric : metrics.getItems()) {
        boolean firstProduct = true;
        StringBuilder products = new StringBuilder();
        for (String product : metric.getSupportedProducts()) {
          if (!firstProduct) {
            products.append(", ");
          }
          products.append(product);
          firstProduct = false;
        }
        System.out.printf("Metric id \"%s\" for product(s): [%s] was found.\n",
            metric.getId(), products.toString());
      }
    } else {
      System.out.println("No metrics found.");
    }

    System.out.println();
  }
}
