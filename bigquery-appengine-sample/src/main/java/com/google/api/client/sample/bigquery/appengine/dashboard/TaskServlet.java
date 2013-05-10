// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet receives a post request when the task that was waiting for the query to finish comes
 * out of the App Engine task queue. It gets the status of the query from Bigquery and:
 * <ul>
 * <li>copies the results to the datastore if the query has finished successfully</li>
 * <li>enqueues another task to wait if the query is running/pending</li>
 * <li>handles query failure</li>
 * </ul>
 *
 * <b>Note:</b> Because of the auth-constraint defined in web.xml, this can only be called by App
 * Engine, and not by users.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */
public class TaskServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(TaskServlet.class.getName());

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String userId = request.getParameter("userId");
    String jobId = request.getParameter("jobId");

    DatastoreUtils datastoreUtils = new DatastoreUtils(userId);
    String message;
    String status = DatastoreUtils.FAILED;

    try {
      BigqueryUtils bigqueryUtils = new BigqueryUtils(userId, jobId);

      // If the job is done, handle it; otherwise, enqueue another task to wait for it.
      if (bigqueryUtils.jobIsDone()) {
        // Delete any previous results for this user.
        datastoreUtils.deleteExistingResults();

        // If the job succeeded, copy the results to the datastore.
        if (bigqueryUtils.jobSucceeded()) {
          datastoreUtils.copyQueryResultsToDatastore(
              bigqueryUtils.getSchemaFieldNames(), bigqueryUtils.getTableData());

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
      if (ex.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
        ServiceUtils.deleteCredentials(userId);
        message = "There was a problem running the query with your credentials. Refresh, please!";
      } else {
        message = "Encountered an exception (" + ex.getStatusCode() + "): " + ex.getMessage();
        log.severe(message);
      }
    }

    // Update the datastore with the new message and status.
    datastoreUtils.putUserInformation(message, status);
  }
}
