/*
 * Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.calendar.v2.model;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;

import java.io.IOException;


/**
 * @author Yaniv Inbar
 */
public class EventEntry extends Entry {

  @Key("gd:when")
  public When when;

  @Key("batch:id")
  public String batchId;
  
  @Key("batch:status")
  public BatchStatus batchStatus;
  
  @Key("batch:operation")
  public BatchOperation batchOperation;
  
  @Override
  public EventEntry clone() {
    return (EventEntry) super.clone();
  }

  @Override
  public EventEntry executeInsert(HttpTransport transport, CalendarUrl url)
      throws IOException {
    return (EventEntry) super.executeInsert(transport, url);
  }
}
