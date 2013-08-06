/*
 * Copyright (c) 2012 Google Inc.
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

package com.google.api.client.sample.urlshortener.appengine.robots;

import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.UrlshortenerScopes;
import com.google.api.services.urlshortener.model.Url;
import com.google.api.services.urlshortener.model.UrlHistory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Google UrlShortener API App Engine sample.
 *
 * @author Yaniv Inbar
 */
public class UrlShortenerSample extends HttpServlet {

  private static final long serialVersionUID = 1L;

  static Urlshortener newUrlshortener() {
    AppIdentityCredential credential =
        new AppIdentityCredential(Arrays.asList(UrlshortenerScopes.URLSHORTENER));
    return new Urlshortener.Builder(new UrlFetchTransport(), new JacksonFactory(), credential)
        .build();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Urlshortener shortener = newUrlshortener();
    UrlHistory history = shortener.url().list().execute();
    resp.setContentType("text/html");
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()));
    writer.append("<html><body><form action=\".\" method=\"post\">Long Url: "
        + "<input name=\"longUrl\" type=\"text\" /><input type=\"submit\" /></form><table><tr>"
        + "<th>Original</th><th>Shortened</th></tr>");
    if (history.getItems() != null) {
      for (Url oneShortened : history.getItems()) {
        writer.append("<tr><td>");
        writer.append(oneShortened.getLongUrl()).append("</td><td><a href=\"").append(
            oneShortened.getId());
        writer.append("\">").append(oneShortened.getId());
        writer.append("</a></td></tr>");
      }
    }
    writer.append("</table></body></html>");
    writer.flush();
    resp.setStatus(200);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String longUrl = req.getParameter("longUrl");
    Urlshortener shortener = newUrlshortener();
    Url toInsert = new Url().setLongUrl(longUrl);
    try {
      shortener.url().insert(toInsert).execute();
    } catch (GoogleJsonResponseException e) {
      resp.sendError(404, e.getMessage());
    }
    resp.sendRedirect("/");
  }
}
