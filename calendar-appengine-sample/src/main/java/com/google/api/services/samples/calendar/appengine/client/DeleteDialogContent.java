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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * Delete dialog content.
 * 
 * @author Yaniv Inbar
 */
public class DeleteDialogContent extends Composite {
  interface MyUiBinder extends UiBinder<HorizontalPanel, DeleteDialogContent> {
  }

  private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

  @UiField
  Button deleteButton;

  @UiField
  Button cancelButton;

  private final DialogBox dialogBox;

  private final GwtCalendar calendar;

  final CalendarGwtSample main;

  final int calendarIndex;

  DeleteDialogContent(CalendarGwtSample main, DialogBox dialogBox, GwtCalendar calendar,
      int calendarIndex) {
    this.main = main;
    this.dialogBox = dialogBox;
    this.calendar = calendar;
    this.calendarIndex = calendarIndex;
    initWidget(uiBinder.createAndBindUi(this));
  }

  @SuppressWarnings("unchecked")
  @UiHandler("deleteButton")
  void handleDelete(ClickEvent e) {
    dialogBox.hide();
    CalendarGwtSample.SERVICE.delete(calendar, new AsyncCallback<Void>() {

      @Override
      public void onFailure(Throwable caught) {
        CalendarGwtSample.handleFailure(caught);
      }

      @Override
      public void onSuccess(Void result) {
        main.calendars.remove(calendarIndex);
        main.refreshTable();
      }
    });
  }

  @UiHandler("cancelButton")
  void handleCancel(ClickEvent e) {
    dialogBox.hide();
  }
}
