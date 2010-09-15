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

package com.google.api.client.sample.discovery.appengine.client;

import com.google.api.client.sample.discovery.appengine.shared.KnownGoogleApis;
import com.google.api.client.sample.discovery.appengine.shared.MethodDetails;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.ArrayList;

/**
 * @author Yaniv Inbar
 */
public class DiscoveryAppEngineSample implements EntryPoint {

  ListBox apiListBox = new ListBox();
  DecoratorPanel decoratorPanel = new DecoratorPanel();
  FlexTable methodsTable = new FlexTable();

  static final DiscoveryServiceAsync service =
      GWT.create(DiscoveryService.class);

  public void onModuleLoad() {
    VerticalPanel mainPanel = new VerticalPanel();
    mainPanel.setSpacing(5);
    Label title = new Label("Google Discovery API Web Client");
    title.addStyleName("header");
    HorizontalPanel addPanel = new HorizontalPanel();
    Label label = new Label("Select a Google API:");
    addPanel.add(label);
    for (KnownGoogleApis api : KnownGoogleApis.values()) {
      apiListBox.addItem(api.displayName, api.name());
    }
    addPanel.add(apiListBox);
    mainPanel.add(title);
    mainPanel.add(addPanel);
    methodsTable.setCellPadding(3);
    methodsTable.setStyleName("methodsTable");
    decoratorPanel.add(methodsTable);
    mainPanel.add(decoratorPanel);
    mainPanel
        .add(
            new Anchor(
                "See Source Code",
                "http://code.google.com/p/google-api-java-client/source/browse?repo=samples#hg/discovery-appengine-sample"));
    RootPanel.get("main").add(mainPanel);

    apiListBox.setFocus(true);
    discover();
    apiListBox.addChangeHandler(new ChangeHandler() {

      @Override
      public void onChange(ChangeEvent event) {
        discover();
      }
    });
  }

  void discover() {
    String apiName = apiListBox.getValue(apiListBox.getSelectedIndex());
    apiListBox.setFocus(true);
    service.getMethods(apiName, new AsyncCallback<ArrayList<MethodDetails>>() {

      @Override
      public void onFailure(Throwable caught) {
        caught.printStackTrace();
        Window.alert("ERROR: " + caught.getMessage());
      }

      @Override
      public void onSuccess(ArrayList<MethodDetails> result) {
        methodsTable.removeAllRows();
        methodsTable.setText(0, 0, "method");
        methodsTable.setText(0, 1, "required parameters");
        methodsTable.setText(0, 2, "optional parameters");
        methodsTable.getCellFormatter().addStyleName(0, 0, "methodsHeaderRow");
        methodsTable.getCellFormatter().addStyleName(
            0, 1, "methodParametersHeaderRow");
        methodsTable.getCellFormatter().addStyleName(
            0, 2, "methodParametersHeaderRow");
        for (int i = 0; i < result.size(); i++) {
          MethodDetails methodDetails = result.get(i);
          methodsTable.setText(i + 1, 0, methodDetails.name);
          methodsTable.setText(
              i + 1, 1, showParams(methodDetails.requiredParameters));
          methodsTable.getCellFormatter().addStyleName(
              i + 1, 1, "parametersColumn");
          methodsTable.setText(
              i + 1, 2, showParams(methodDetails.optionalParameters));
          methodsTable.getCellFormatter().addStyleName(
              i + 1, 2, "parametersColumn");
        }
      }
    });
  }

  static String showParams(ArrayList<String> params) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < params.size(); i++) {
      if (i != 0) {
        buf.append(", ");
      }
      buf.append(params.get(i));
    }
    return buf.toString();
  }
}
