// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lparkinson@google.com (Laura Parkinson)
 *
 */
public class MainServlet extends AbstractAppEngineAuthorizationCodeServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(MainServlet.class.getName());

  /**
   * This servlet responds to a GET request with a stencil page that will be filled with a chart and
   * a message by client-side javascript. Also, if no data exists in the datastore for the current
   * user, it sends a query to retrieve it.
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String userId = getUserId(request);
    DatastoreUtils datastoreUtils = new DatastoreUtils(userId);

    printPage(response, datastoreUtils.getUserLastRunMessage());

    // Try to get data if this user is unknown, or if their last try failed.
    if (!datastoreUtils.hasUserEntity() || datastoreUtils.hasUserQueryFailed()) {
      runQuery(request, response, userId, datastoreUtils);
    }
  }

  private void runQuery(HttpServletRequest request, HttpServletResponse response, String userId,
      DatastoreUtils datastoreUtils) throws IOException {
    // Clear the information from the last run for this user
    datastoreUtils.putUserInformation("Beginning query...", null);

    String message;
    String status = DatastoreUtils.FAILED;

    try {
      // Begin a query. A task is begun to wait for the results of the query,
      // and when the query finishes, that task (see TaskServlet) takes care
      // of copying the results to the datastore.
      BigqueryUtils bigqueryUtils = new BigqueryUtils(userId);
      bigqueryUtils.beginQuery();
      message = "Began running your query";
      status = bigqueryUtils.getJobStatus();

    } catch (SampleDashboardException ex) {
      if (ex.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
        ServiceUtils.deleteCredentials(userId);
        message = "There was a problem running the query with your credentials. Refresh, please!";
      }
      else {
        message = "Encountered an exception (" + ex.getStatusCode() + "): " + ex.getMessage();
        log.severe(message);
      }
    }

    datastoreUtils.putUserInformation(message, status);
  }

  /**
   * A post to this servlet reruns the query for the logged-in user.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String userId = getUserId(request);
    DatastoreUtils datastoreUtils = new DatastoreUtils(userId);
    runQuery(request, response, userId, datastoreUtils);
  }

  private void printPage(HttpServletResponse response, String lastRun) throws IOException {
    response.setContentType("text/html");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().print("<!doctype html><html><head>"
        + "<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>"
        + "<script type=\"text/javascript\" src=\"drawGraph.js\"></script>"
        + "<title>Bigquery sample dashboard</title></head><body><div style=\"width:800px;\">"
        + "<input type=\"button\" id=\"refresh\" value=\"Run query\" style=\"float:right;\"/>"
        + "Query last run: <span id=\"lastRun\">" + lastRun + "</span></div><br/>"
        + "<div id=\"message\">Checking the datastore for cached results...</div>"
        + "<div id=\"visualization\"></div><br/><a href=\"#\" id=\"toggle\">"
        + "Show query that generated these results</a><br/><div id=\"query\">"
        + htmlify(BigqueryUtils.buildExampleQuery()) + "</div></body></html>");
  }

  private String htmlify(String s) {
    s = s.replace("\n", "<br/>");
    s = s.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
    s = s.replace(" ", "&nbsp;");
    return s;
  }

  @Override
  protected AuthorizationCodeFlow initializeFlow() throws IOException {
    return ServiceUtils.newFlow();
  }

  @Override
  protected String getRedirectUri(HttpServletRequest req) {
    return ServiceUtils.getRedirectUri(req);
  }
}
