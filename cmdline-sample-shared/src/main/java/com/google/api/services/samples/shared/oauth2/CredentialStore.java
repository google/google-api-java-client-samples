/*
 * Copyright (c) 2011 Google Inc.
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

package com.google.api.services.samples.shared.oauth2;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;

/**
 * Stores the access and refresh token credentials in a persistent storage like a secured file, a
 * database or a web service.
 *
 * <p>
 * Ideally one should leverage the OS functionalities to store the access token and refresh token in
 * encrypted form across invocation of this application.
 * </p>
 *
 * <p>
 * Note that there are no implementations of this interface provided in this sample.
 * </p>
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/ms717803(VS.85).aspx">Windows Data
 *      Protection Techniques</a>
 * @see <a href="http://www.google.com/search?q=Keychain+Services+Guide+site%3Adeveloper.apple.com"
 *      >Keychains on Mac</a>
 * @author Yaniv Inbar
 */
public interface CredentialStore {

  /**
   * Reads the access and refresh token credentials from store.
   *
   * @return stored access and refresh token credentials or {@code null} for none
   */
  AccessTokenResponse read();

  /**
   * Writes the access and refresh token credentials into the store.
   *
   * @param response access and refresh token credentials
   */
  void write(AccessTokenResponse response);
}
