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

import com.google.api.services.adexchangebuyer.Adexchangebuyer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements the base the basic input method used by all samples, as well as defines the
 * interface that all samples should implement.
 *
 * @author david.t@google.com (David Torres)
 *
 */
public abstract class BaseSample {
  // Stores previously entered input values.
  private static Map<String, String> cachedValues = new HashMap<String, String>();

  /**
   * Returns the name of the sample, for display purposes.
   *
   * @return The sample name
   */
  public abstract String getName();

  /**
   * Returns the description of the sample, for display purposes.
   *
   * @return The description of the sample
   */
  public abstract String getDescription();

  /**
   * Starts the execution of the sample.
   *
   * @param client The Ad Exchange Buyer API client class
   * @throws IOException In case an error has occurred
   */
  public abstract void execute(Adexchangebuyer client) throws IOException;

  /**
   * Prompts the user to enter a numeric value.
   *
   * @param propertyKey Key of the value, used to store it for future use
   * @param message Message to print out to the user in request of input
   * @return The parsed numeric value entered by the user
   * @throws IOException
   */
  protected long getLongInput(String propertyKey, String message) throws IOException {
    return getLongInput(propertyKey, message, null);
  }

  /**
   * Prompts the user to enter a numeric value.
   *
   * @param propertyKey Key of the value, used to store it for future use
   * @param message Message to print out to the user in request of input
   * @param defaultValue Default value to return in case the user does not provide one
   * @return The parsed numeric value entered by the user
   * @throws IOException
   */
  protected long getLongInput(String propertyKey, String message, Long defaultValue)
      throws IOException {
    Long input = null;
    while (input == null) {
      try {
        String sDefaultValue = defaultValue != null ? Long.toString(defaultValue) : null;
        input = Long.parseLong(getStringInput(propertyKey, message, sDefaultValue));
      } catch (NumberFormatException e) {
        System.out.printf("Invalid numeric input provided, please try again\n");
      }
    }
    return input;
  }

  /**
   * Prompts the user to enter an optional numeric value.
   *
   * @param propertyKey Key of the value, used to store it for future use
   * @param message Message to print out to the user in request of input
   * @return The parsed numeric value entered by the user
   * @throws IOException
   */
  protected Long getOptionalLongInput(String propertyKey, String message)
      throws IOException {
    while (true) {
      try {
        String userInput = getOptionalStringInput(propertyKey, message);
        if (userInput == null) {
          return null;
        }
        return Long.parseLong(userInput);
      } catch (NumberFormatException e) {
        System.out.printf("Invalid numeric input provided, please try again\n");
      }
    }
  }

  /**
   * Prompts the user to enter a numeric value.
   *
   * @param propertyKey Key of the value, used to store it for future use
   * @param message Message to print out to the user in request of input
   * @return The parsed numeric value entered by the user
   * @throws IOException
   */
  protected int getIntInput(String propertyKey, String message) throws IOException {
    return getIntInput(propertyKey, message, null);
  }

  /**
   * Prompts the user to enter a numeric value.
   *
   * @param propertyKey Key of the value, used to store it for future use
   * @param message Message to print out to the user in request of input
   * @param defaultValue Default value to return in case the user does not provide one
   * @return The parsed numeric value entered by the user
   * @throws IOException
   */
  protected int getIntInput(String propertyKey, String message, Integer defaultValue)
      throws IOException {
    Integer input = null;
    while (input == null) {
      try {
        String sDefaultValue = defaultValue != null ? Integer.toString(defaultValue) : null;
        input = Integer.parseInt(getStringInput(propertyKey, message, sDefaultValue));
      } catch (NumberFormatException e) {
        System.out.printf("Invalid numeric input provided, please try again\n");
      }
    }
    return input;
  }

  /**
   * Prompts the user to enter a value.
   *
   * @param propertyKey Key of the value, used to store it for future use
   * @param message Message to print out to the user in request of input
   * @return The captured value entered by the user
   * @throws IOException
   */
  protected String getStringInput(String propertyKey, String message) throws IOException {
    return getStringInput(propertyKey, message, null);
  }

  /**
   * Prompts the user to enter a value.
   *
   * @param propertyKey Key of the value, used to store it for future use
   * @param message Message to print out to the user in request of input
   * @param defaultValue Default value to return in case the user does not provide one
   * @return The captured value entered by the user
   * @throws IOException
   */
  protected String getStringInput(String propertyKey, String message, String defaultValue)
      throws IOException {
    String lastValue = cachedValues.get(propertyKey);
    if (lastValue != null) {
      System.out.printf("%s (press enter to use last value %s):\n", message, lastValue);
    } else if (defaultValue != null) {
      System.out.printf("%s (press enter to use default value %s):\n", message, defaultValue);
    } else {
      System.out.printf("%s:\n", message);
    }
    String input = Utils.readInputLine();
    if (input != null && !input.isEmpty()) {
      cachedValues.put(propertyKey, input);
      return input;
    }
    if (lastValue != null) {
      return lastValue;
    }
    if (defaultValue != null) {
      return defaultValue;
    }
    return null;
  }

  /**
   * Prompts the user to enter an optional value.
   *
   * @param propertyKey Key of the value, used to store it for future use
   * @param message Message to print out to the user in request of input
   * @return The captured value entered by the user
   * @throws IOException
   */
  protected String getOptionalStringInput(String propertyKey, String message)
      throws IOException {
    String lastValue = cachedValues.get(propertyKey);
    if (lastValue != null) {
      System.out.printf("%s (last value was %s):\n", message, lastValue);
    } else {
      System.out.printf("%s:\n", message);
    }
    String input = Utils.readInputLine();
    if (input != null && !input.isEmpty()) {
      cachedValues.put(propertyKey, input);
      return input;
    }
    return null;
  }
}
