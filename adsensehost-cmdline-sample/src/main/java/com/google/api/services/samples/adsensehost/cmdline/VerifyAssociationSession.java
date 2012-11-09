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

/**
 *
 * This example verifies an association session callback token.
 *
 * Tags: associationsessions.verify
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class VerifyAssociationSession {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param callbackToken the token returned in the association callback.
   * @throws Exception
   */
  public static void run(AdSenseHost service, String callbackToken) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Verifying association session");
    System.out.println("=================================================================");

    // Verify the association session token.
    AssociationSession associationSession = service.associationsessions().verify(
        callbackToken).execute();

    System.out.printf("Association for account \"%s\" has status \"%s\" and ID \"%s\".\n",
        associationSession.getAccountId(), associationSession.getStatus(),
        associationSession.getId());

    System.out.println();
  }
}
