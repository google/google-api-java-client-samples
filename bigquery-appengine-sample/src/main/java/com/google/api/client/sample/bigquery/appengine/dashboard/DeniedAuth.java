// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that shows a page when the user doesn't allow the dashboard to access
 * his/her Bigquery data.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */
public class DeniedAuth extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.addHeader("Content-Type", "text/html");
    response.getWriter().print("You don't want to try the sample?");
    response.setStatus(200);
  }
}
