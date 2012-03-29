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

package com.google.api.services.samples.adexchangebuyer.cmdline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class implements utility methods used across the Ad Exchange Buyer
 * samples.
 *
 * @author david.t@google.com (David Torres)
 *
 */
public class Utils {
  /**
   * Reads an input line from standard input.
   *
   * @return The line read
   * @throws IOException If an I/O error has occurred
   */
  public static String readInputLine() throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    return in.readLine();
  }
}
