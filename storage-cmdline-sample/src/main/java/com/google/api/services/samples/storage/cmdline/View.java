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

package com.google.api.services.samples.storage.cmdline;

import com.google.api.services.storage.model.Bucket;
import com.google.api.services.storage.model.StorageObject;

/** Provides simple cmdline UI. */
public class View {

  static void header1(String name) {
    System.out.println();
    System.out.println("================== " + name + " ==================");
    System.out.println();
  }

  static void header2(String name) {
    System.out.println();
    System.out.println("~~~~~~~~~~~~~~~~~~ " + name + " ~~~~~~~~~~~~~~~~~~");
    System.out.println();
  }

  static void show(Bucket bucket) {
    System.out.println("name: " + bucket.getName());
    System.out.println("location: " + bucket.getLocation());
    System.out.println("timeCreated: " + bucket.getTimeCreated());
    System.out.println("owner: " + bucket.getOwner());
    System.out.println("acl: " + bucket.getAcl());
  }

  static void show(StorageObject object) {
    System.out.println("name: " + object.getName());
    System.out.println("size: " + object.getSize());
    System.out.println("contentType: " + object.getContentType());
    System.out.println("updated: " + object.getUpdated());
    System.out.println("owner: " + object.getOwner());
    // should only show up if projection is full.
    // System.out.println("acl: " + object.getAcl());
  }

  static void separator() {
    System.out.println();
    System.out.println("------------------------------------------------------");
    System.out.println();
  }
}

