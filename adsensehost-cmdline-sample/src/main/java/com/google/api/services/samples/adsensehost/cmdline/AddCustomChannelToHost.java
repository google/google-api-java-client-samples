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
 * This example adds a custom channel to a host ad client.
 *
 * To get ad clients, see GetAllAdClientsForHost.java.
 *
 * Tags: customchannels.insert
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class AddCustomChannelToHost {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @return the created custom channel.
   * @throws Exception
   */
  public static CustomChannel run(AdSenseHost service, String adClientId)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Adding custom channel to ad client %s\n", adClientId);
    System.out.println("=================================================================");

    CustomChannel newCustomChannel = new CustomChannel()
        .setName("Sample Channel #" + AdSenseHostSample.getUniqueName());

    // Create custom channel.
    CustomChannel customChannel = service.customchannels().insert(adClientId, newCustomChannel)
        .execute();

    System.out.printf("Custom channel with id \"%s\", code \"%s\" and name \"%s\" was created.\n",
        customChannel.getId(), customChannel.getCode(), customChannel.getName());

    System.out.println();

    // Return the created custom channel.
    return customChannel;
  }
}
