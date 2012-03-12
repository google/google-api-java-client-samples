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

package com.google.api.services.samples.calendar.appengine.shared;

import java.io.Serializable;

/**
 * Calendar class used for GWT RPC.
 * 
 * @author Yaniv Inbar
 */
@SuppressWarnings("serial")
public class GwtCalendar implements Serializable {
  public GwtCalendar() {
  }

  public GwtCalendar(String id, String title) {
    this.id = id;
    this.title = title;
  }

  public String id;
  public String title;
}
