// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.buzz.appengine.oauth2;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author moshenko@google.com (Jacob Moshenko)
 *
 */
public class DeniedAuth extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User loggedIn = userService.getCurrentUser();

    resp.getWriter().print("<h3>" + loggedIn.getNickname()
                           + ", why don't you want to play with me?</h1>");
    resp.setStatus(200);
    resp.addHeader("Content-Type", "text/html");
  }
}
