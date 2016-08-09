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
import com.google.api.services.adsensehost.model.UrlChannel;

/**
 *
 * This example deletes a URL channel on a host ad client.
 *
 * To get ad clients, see GetAllAdClientsForPublisher.java.
 * To get custom channels, see GetAllUrlChannelsForHost.java.
 *
 * Tags: urlchannels.delete
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class DeleteUrlChannelOnHost {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @param urlChannelId the ID of the URL channel to be deleted.
   * @throws Exception
   */
  public static void run(AdSenseHost service, String adClientId,
      String urlChannelId) throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Deleting URL channel %s\n", urlChannelId);
    System.out.println("=================================================================");

    // Delete URL channel.
    UrlChannel urlChannel = service.urlchannels().delete(adClientId, urlChannelId).execute();

    System.out.printf("URL channel with ID \"%s\" was deleted.\n", urlChannel.getId());

    System.out.println();
  }
}
