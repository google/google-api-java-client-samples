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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Buttons for a calendar.
 * 
 * @author Yaniv Inbar
 */
public class CalendarButtons extends Composite {
  interface MyUiBinder extends UiBinder<HorizontalPanel, CalendarButtons> {
  }

  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField
  Button deleteButton;

  @UiField
  Button updateButton;

  private final int calendarIndex;

  private final CalendarGwtSample main;

  private final GwtCalendar calendar;

  public CalendarButtons(CalendarGwtSample main, GwtCalendar calendar, int calendarIndex) {
    this.main = main;
    this.calendar = calendar;
    this.calendarIndex = calendarIndex;
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("deleteButton")
  void handleDelete(ClickEvent e) {
    DialogBox dialogBox = new DialogBox();
    dialogBox.setAnimationEnabled(true);
    dialogBox.setText("Are you sure you want to permanently delete the calendar?");
    DeleteDialogContent content = new DeleteDialogContent(main, dialogBox, calendar, calendarIndex);
    dialogBox.add(content);
    dialogBox.show();
  }

  @UiHandler("updateButton")
  void handleUpdate(ClickEvent e) {
    DialogBox dialogBox = new DialogBox();
    dialogBox.setAnimationEnabled(true);
    dialogBox.setText("Update Calendar Title:");
    UpdateDialogContent content = new UpdateDialogContent(main, dialogBox, calendar, calendarIndex);
    dialogBox.add(content);
    dialogBox.show();
  }
}
