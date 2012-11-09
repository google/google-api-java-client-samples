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
import com.google.api.services.adsensehost.model.CustomChannel;

/**
 *
 * This example deletes a custom channel on a host ad client.
 *
 * To get ad clients, see GetAllAdClientsForPublisher.java.
 * To get custom channels, see GetAllCustomChannelsForHost.java.
 *
 * Tags: customchannels.delete
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class DeleteCustomChannelOnHost {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @param customChannelId the ID of the custom channel to be deleted.
   * @throws Exception
   */
  public static void run(AdSenseHost service, String adClientId,
      String customChannelId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Deleting custom channel %s\n", customChannelId);
    System.out.println("=================================================================");

    // Delete custom channel.
    CustomChannel customChannel =
        service.customchannels().delete(adClientId, customChannelId).execute();

    System.out.printf("Custom channel with ID \"%s\" was deleted.\n", customChannel.getId());

    System.out.println();
  }
}
