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
 * This example updates a custom channel on a host ad client.
 *
 * To get ad clients, see GetAllAdClientsForHost.java.
 * To get custom channels, see GetAllCustomChannelsForHost.java.
 *
 * Tags: customchannels.patch
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class UpdateCustomChannelOnHost {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param adClientId the ad client ID that contains the custom channel.
   * @param customChannelId the ID of the custom channel to be updated.
   * @return the updated custom channel.
   * @throws Exception
   */
  public static CustomChannel run(AdSenseHost service, String adClientId, String customChannelId)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Updating custom channel %s\n", customChannelId);
    System.out.println("=================================================================");

    CustomChannel patchCustomChannel = new CustomChannel()
        .setName("Updated Custom Channel #" + AdSenseHostSample.getUniqueName());

    // Update custom channel.
    CustomChannel customChannel = service.customchannels().patch(adClientId, customChannelId,
        patchCustomChannel).execute();

    System.out.printf(
        "Custom channel with id \"%s\", code \"%s\" and name \"%s\" was updated.\n",
        customChannel.getId(), customChannel.getCode(), customChannel.getName());

    System.out.println();

    // Return the updated custom channel.
    return customChannel;
  }
}
