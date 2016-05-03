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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main calendars UI frame.
 * 
 * @author Yaniv Inbar
 */
public class CalendarsFrame extends Composite {
  interface MyUiBinder extends UiBinder<VerticalPanel, CalendarsFrame> {
  }

  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField
  TextBox addTextBox;

  @UiField
  Button addButton;

  @UiField
  FlexTable calendarsTable;

  final CalendarGwtSample main;

  public CalendarsFrame(CalendarGwtSample main) {
    this.main = main;
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("addButton")
  void handleAdd(ClickEvent e) {
    GwtCalendar calendar = new GwtCalendar();
    calendar.title = addTextBox.getText();
    if (calendar.title != null) {
      addTextBox.setText("");
      CalendarGwtSample.SERVICE.insert(calendar, new AsyncCallback<GwtCalendar>() {

        @Override
        public void onFailure(Throwable caught) {
          CalendarGwtSample.handleFailure(caught);
        }

        @Override
        public void onSuccess(GwtCalendar result) {
          main.calendars.add(result);
          main.refreshTable();
        }
      });
    }
  }
}
