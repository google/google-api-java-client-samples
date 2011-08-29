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

import com.google.api.services.samples.calendar.appengine.shared.AuthenticationException;
import com.google.api.services.samples.calendar.appengine.shared.GwtCalendar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.List;

/**
 * Main entry-point for the Calendar GWT sample.
 *
 * @author Yaniv Inbar
 */
public class CalendarGwtSample implements EntryPoint {

  CalendarsFrame calendarsFrame;
  List<GwtCalendar> calendars;

  static final CalendarServiceAsync SERVICE = GWT.create(CalendarService.class);

  public void onModuleLoad() {
    calendarsFrame = new CalendarsFrame(this);
    RootPanel.get("main").add(calendarsFrame);
    // "loading calendars..."
    FlexTable calendarsTable = calendarsFrame.calendarsTable;
    calendarsTable.setText(0, 0, "Loading Calendars...");
    calendarsTable.getCellFormatter().addStyleName(0, 0, "methodsHeaderRow");
    // import calendars
    SERVICE.getCalendars(new AsyncCallback<List<GwtCalendar>>() {

      @Override
      public void onFailure(Throwable caught) {
        handleFailure(caught);
      }

      @Override
      public void onSuccess(List<GwtCalendar> result) {
        calendars = result;
        calendarsFrame.addButton.setEnabled(true);
        refreshTable();
      }
    });
  }

  void refreshTable() {
    FlexTable calendarsTable = calendarsFrame.calendarsTable;
    calendarsTable.removeAllRows();
    calendarsTable.setText(0, 1, "Calendar Title");
    calendarsTable.setText(0, 2, "Updated");
    calendarsTable.getCellFormatter().addStyleName(0, 1, "methodsHeaderRow");
    calendarsTable.getCellFormatter().addStyleName(0, 2, "methodsHeaderRow");
    for (int i = 0; i < calendars.size(); i++) {
      GwtCalendar calendar = calendars.get(i);
      calendarsTable.setWidget(i + 1, 0, new CalendarButtons(this, calendar, i));
      calendarsTable.setText(i + 1, 1, calendar.title);
      calendarsTable.setText(i + 1, 2, calendar.updated);
    }
  }

  static void handleFailure(Throwable caught) {
    if (caught instanceof AuthenticationException) {
      Window.Location.reload();
    } else {
      caught.printStackTrace();
      Window.alert("ERROR: " + caught.getMessage());
    }
  }
}
