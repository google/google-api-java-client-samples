// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import com.google.api.client.extensions.appengine.http.urlfetch.UrlFetchTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet receives a post request when the task that was waiting for the
 * query to finish comes out of the App Engine task queue.  It gets the status
 * of the query from Bigquery and:
 *   - copies the results to the datastore if the query has finished successfully
 *   - enqueues another task to wait if the query is running/pending
 *   - handles query failure
 *   - clears the users's credentials and asks them to refresh if it catches a 401
 *
 * Note that because of the auth-constraint defined in web.xml, this can only be
 * called by App Engine, and not by users.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */
public class TaskServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(TaskServlet.class.getName());

  private final HttpTransport transport = new UrlFetchTransport();
  private final JsonFactory jsonFactory = new JacksonFactory();

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    String userId = request.getParameter("userId");
    String jobId = request.getParameter("jobId");

    DatastoreUtils datastoreUtils = new DatastoreUtils(userId);
    String message;
    String status = DatastoreUtils.FAILED;

    try {
      BigqueryUtils bigqueryUtils = new BigqueryUtils(userId, transport, jsonFactory, jobId);

      // If the job is done, handle it; otherwise, enqueue another task to wait for it.
      if (bigqueryUtils.jobIsDone()) {
        // Delete any previous results for this user.
        datastoreUtils.deleteExistingResults();

        // If the job succeeded, copy the results to the datastore.
        if (bigqueryUtils.jobSucceeded()) {
          datastoreUtils.copyQueryResultsToDatastore(bigqueryUtils.getSchemaFieldNames(),
              bigqueryUtils.getTableData());

          message = "Here are your results!";
          status = bigqueryUtils.getJobStatus();

          datastoreUtils.updateSuccessfulQueryTimestamp();
        } else {
          message = bigqueryUtils.getJobErrorMessage();
        }
      } else {
        // If it's not done, keep waiting for it.
        String jobStatus = bigqueryUtils.getJobStatus();
        bigqueryUtils.enqueueWaitingTask();
        message = "Waiting for the results of the query (" + jobStatus.toLowerCase() + ")";
        status = jobStatus;
      }
    } catch (SampleDashboardException ex) {
      // If bad credentials were the problem, clear them and ask the user to refresh.
      AuthUtils authUtils = new AuthUtils(userId, transport, jsonFactory);
      if (authUtils.handleUnauthorizedException(ex)) {
        message = "Couldn't check on the query with your credentials. Refresh, please!";
      } else {
        message = "Encountered an exception (" + ex.getStatusCode() + "): " + ex.getMessage();
        log.severe(message);
      }
    }

    // Update the datastore with the new message and status.
    datastoreUtils.putUserInformation(message, status);
  }
}
