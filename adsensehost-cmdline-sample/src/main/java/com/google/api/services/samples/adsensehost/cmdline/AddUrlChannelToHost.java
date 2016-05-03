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
 * This example adds a URL channel to a host ad client.
 *
 * To get ad clients, see GetAllAdClientsForHost.java.
 *
 * Tags: urlchannels.insert
 *
 * @author sgomes@google.com (Sérgio Gomes)
 *
 */
public class AddUrlChannelToHost {

  /**
   * Runs this sample.
   *
   * @param service AdSenseHost service object on which to run the requests.
   * @param adClientId the ID for the ad client to be used.
   * @return the created URL channel.
   * @throws Exception
   */
  public static UrlChannel run(AdSenseHost service, String adClientId)
      throws Exception {
    System.out.println("=================================================================");
    System.out.printf("Adding URL channel to ad client %s\n", adClientId);
    System.out.println("=================================================================");

    UrlChannel newUrlChannel = new UrlChannel()
        .setUrlPattern("www.example.com/" + AdSenseHostSample.getUniqueName());

    // Create URL channel.
    UrlChannel urlChannel = service.urlchannels().insert(adClientId, newUrlChannel)
        .execute();

    System.out.printf("URL channel with id \"%s\" and URL pattern \"%s\" was created.\n",
        urlChannel.getId(), urlChannel.getUrlPattern());

    System.out.println();

    // Return the created URL channel.
    return urlChannel;
  }
}
