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
import com.google.api.services.adsensehost.model.AssociationSession;

import java.util.List;

/**
 *
 * This example starts an association session.
 *
 * Tags: associationsessions.start
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class StartAssociationSession {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param productCodes the list of products to associate with.
   * @param websiteUrl the URL of the publisher website.
   * @throws Exception
   */
  public static void run(AdSenseHost service, List<String> productCodes, String websiteUrl)
      throws Exception {
    System.out.println("=================================================================");
    System.out.println("Creating new association session");
    System.out.println("=================================================================");

    // Request a new association session.
    AssociationSession associationSession = service.associationsessions().start(
        productCodes, websiteUrl).execute();

    System.out.printf("Association with ID \"%s\" and redirect URL \"%s\" was started.\n",
          associationSession.getId(), associationSession.getRedirectUrl());

    System.out.println();
  }
}
