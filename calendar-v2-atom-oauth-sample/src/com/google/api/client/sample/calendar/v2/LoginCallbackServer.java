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

package com.google.api.client.sample.calendar.v2;

import com.google.common.base.Preconditions;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class that runs a Jetty server on a free port, waiting for OAuth to redirect
 * to it with the one-time authorization token.
 * <p>
 * Mostly copied from oacurl by phopkins@google.com.
 *
 * @author Yaniv Inbar
 */
public class LoginCallbackServer {
  private static final String CALLBACK_PATH = "/OAuthCallback";

  private int port;
  private Server server;

  private Map<String, String> verifierMap = new HashMap<String, String>();

  public void start() {
    if (server != null) {
      throw new IllegalStateException("Server is already started");
    }

    try {
      port = getUnusedPort();
      server = new Server(port);

      for (Connector c : server.getConnectors()) {
        c.setHost("localhost");
      }

      server.addHandler(new CallbackHandler());

      server.start();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() throws Exception {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  public String getCallbackUrl() {
    Preconditions.checkArgument(port != 0, "Server is not yet started");
    return "http://localhost:" + port + CALLBACK_PATH;
  }

  private static int getUnusedPort() throws IOException {
    Socket s = new Socket();
    s.bind(null);

    try {
      return s.getLocalPort();
    } finally {
      s.close();
    }
  }

  /**
   * Call that blocks until the OAuth provider redirects back here with the
   * verifier token.
   *
   * @param requestToken request token
   * @return The verifier token, or null if there was a timeout.
   */
  public String waitForVerifier(String requestToken) {
    synchronized (verifierMap) {
      while (!verifierMap.containsKey(requestToken)) {
        try {
          verifierMap.wait(3000);
        } catch (InterruptedException e) {
          return null;
        }
      }

      return verifierMap.remove(requestToken);
    }
  }

  /**
   * Jetty handler that takes the verifier token passed over from the OAuth
   * provider and stashes it where {@link LoginCallbackServer#waitForVerifier}
   * will find it.
   */
  public class CallbackHandler extends AbstractHandler {
    public void handle(String target, HttpServletRequest request,
        HttpServletResponse response, int dispatch) throws IOException {
      if (!CALLBACK_PATH.equals(target)) {
        return;
      }

      writeLandingHtml(response);
      response.flushBuffer();
      ((Request) request).setHandled(true);

      String requestToken = request.getParameter("oauth_token");
      String verifier = request.getParameter("oauth_verifier");

      synchronized (verifierMap) {
        verifierMap.put(requestToken, verifier);
        verifierMap.notifyAll();
      }
    }

    private void writeLandingHtml(HttpServletResponse response)
        throws IOException {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("text/html");

      PrintWriter doc = response.getWriter();
      doc.println("<html>");
      doc.println(
          "<head><title>OAuth Authentication Token Recieved</title></head>");
      doc.println("<body>");
      doc.println("Received verifier token. Closing...");
      doc.println("<script type='text/javascript'>");
      // We open "" in the same window to trigger JS ownership of it, which lets
      // us then close it via JS, at least in Chrome.
      doc.println("window.setTimeout(function() {");
      doc.println(
          "    window.open('', '_self', ''); window.close(); }, 1000);");
      doc.println("if (window.opener) { window.opener.checkToken(); }");
      doc.println("</script>");
      doc.println("</body>");
      doc.println("</HTML>");
      doc.flush();
    }
  }
}
