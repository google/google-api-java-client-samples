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

package com.google.api.services.samples.fusiontables.cmdline;

import com.google.api.services.fusiontables.model.Table;

/**
 * Utility methods to print to the command line.
 * 
 * @author Christian Junk
 */
public class View {

  static void header(String name) {
    System.out.println();
    System.out.println("================== " + name + " ==================");
    System.out.println();
  }

  static void show(Table table) {
    System.out.println("id: " + table.getTableId());
    System.out.println("name: " + table.getName());
    System.out.println("description: " + table.getDescription());
    System.out.println("attribution: " + table.getAttribution());
    System.out.println("attribution link: " + table.getAttributionLink());
    System.out.println("kind: " + table.getKind());

  }

  static void separator() {
    System.out.println();
    System.out.println("------------------------------------------------------");
    System.out.println();
  }
}
