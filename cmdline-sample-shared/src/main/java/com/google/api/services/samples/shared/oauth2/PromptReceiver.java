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

import com.google.api.client.auth.oauth2.draft10.InstalledApp;

import java.util.Scanner;

/**
 * Verification code receiver that prompts user to paste the code copied from the browser.
 *
 * @author Yaniv Inbar
 */
public class PromptReceiver implements VerificationCodeReceiver {

  @Override
  public String waitForCode() {
    String code;
    do {
      System.out.print("Please enter code: ");
      code = new Scanner(System.in).nextLine();
    } while (code.isEmpty());
    return code;
  }

  @Override
  public String getRedirectUrl() {
    return InstalledApp.OOB_REDIRECT_URI;
  }

  @Override
  public void stop() {
  }
}
