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

package com.google.api.services.samples.calendar.appengine.client;

import com.google.api.services.samples.calendar.appengine.shared.GwtCalendar;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.io.IOException;
import java.util.List;

/**
 * GWT RPC service for calendars.
 * 
 * @author Yaniv Inbar
 */
@RemoteServiceRelativePath("calendarService")
public interface CalendarService extends RemoteService {

  List<GwtCalendar> getCalendars() throws IOException;

  void delete(GwtCalendar calendar) throws IOException;

  GwtCalendar insert(GwtCalendar calendar) throws IOException;

  GwtCalendar update(GwtCalendar updated) throws IOException;
}
