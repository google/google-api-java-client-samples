/*
 * Copyright (c) 2012 Google Inc.
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

package com.google.api.services.samples.dfareporting.cmdline;

import com.google.api.client.util.Preconditions;
import com.google.api.services.dfareporting.Dfareporting;
import com.google.api.services.dfareporting.model.UserProfile;
import com.google.api.services.dfareporting.model.UserProfileList;

/**
 * This example gets all DFA user profiles associated with the user's Google Account.
 * 
 * Tags: profiles.list
 * 
 * @author jdilallo@google.com (Joseph DiLallo)
 */
public class GetAllUserProfiles {

  /**
   * Lists all DFA user profiles associated with your Google Account.
   * 
   * @param reporting Dfareporting service object on which to run the requests.
   * @return the list of user profiles received.
   * @throws Exception
   */
  public static UserProfileList list(Dfareporting reporting) throws Exception {
    System.out.println("=================================================================");
    System.out.println("Listing all DFA user profiles");
    System.out.println("=================================================================");

    // Retrieve DFA user profiles and display them. User profiles do not support
    // paging.
    UserProfileList profiles = reporting.userProfiles().list().execute();
    Preconditions.checkArgument(
        profiles.getItems() != null && !profiles.getItems().isEmpty(), "No profiles found");
    for (UserProfile userProfile : profiles.getItems()) {
      System.out.printf("User profile with ID \"%s\" and name \"%s\" was found.%n",
          userProfile.getProfileId(), userProfile.getUserName());
    }

    System.out.println();
    return profiles;
  }
}
