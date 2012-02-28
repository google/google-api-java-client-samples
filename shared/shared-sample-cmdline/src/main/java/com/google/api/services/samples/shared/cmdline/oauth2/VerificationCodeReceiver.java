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

package com.google.api.services.samples.shared.cmdline.oauth2;

/**
 * Verification code receiver.
 *
 * @author Yaniv Inbar
 */
public interface VerificationCodeReceiver {

  /** Returns the redirect URI. */
  String getRedirectUri() throws Exception;

  /** Waits for a verification code. */
  String waitForCode();

  /** Releases any resources and stops any processes started. */
  void stop() throws Exception;
}
