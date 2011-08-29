/*
 * Copyright (c) 2010 Google Inc.
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

package com.google.api.services.calendar.model;

import com.google.api.client.util.Key;

/**
 * @author Yaniv Inbar
 */
public class BatchOperation {

  public static final BatchOperation INSERT = BatchOperation.of("insert");
  public static final BatchOperation QUERY = BatchOperation.of("query");
  public static final BatchOperation UPDATE = BatchOperation.of("update");
  public static final BatchOperation DELETE = BatchOperation.of("delete");

  @Key("@type")
  public String type;

  public BatchOperation() {
  }

  public static BatchOperation of(String type) {
    BatchOperation result = new BatchOperation();
    result.type = type;
    return result;
  }
}
