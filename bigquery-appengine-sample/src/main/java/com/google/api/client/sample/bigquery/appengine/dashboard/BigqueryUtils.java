// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.Bigqueryfield;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.Jobconfiguration;
import com.google.api.services.bigquery.model.Jobconfigurationquery;
import com.google.api.services.bigquery.model.Jobreference;
import com.google.api.services.bigquery.model.Row;
import com.google.api.services.bigquery.model.Table;
import com.google.api.services.bigquery.model.TableDataList;
import com.google.api.services.bigquery.model.Tablereference;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.RetryOptions;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

/**
 * Utility methods for beginning jobs, waiting for jobs, and instantiating Bigqueries.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */
public class BigqueryUtils {

  private static final Logger log = Logger.getLogger(BigqueryUtils.class.getName());
  private static final String projectId =
      System.getProperty("com.google.api.client.sample.bigquery.appengine.dashboard.projectId");

  private final String userId;
  private final Bigquery bigquery;
  private Job job;
  private final AuthUtils authUtils;
  private final GoogleAccessProtectedResource gapr;

  public BigqueryUtils(String userId, HttpTransport transport, JsonFactory jsonFactory)
      throws SampleDashboardException {
    this(userId, transport, jsonFactory, null);
  }

  public BigqueryUtils(String userId, HttpTransport transport, JsonFactory jsonFactory,
      final String jobId) throws SampleDashboardException {
    this.userId = userId;

    authUtils = new AuthUtils(userId, transport, jsonFactory);
    gapr = new GoogleAccessProtectedResource(authUtils.getAccessToken());
    bigquery = Bigquery.builder(transport, jsonFactory).setHttpRequestInitializer(gapr).build();

    if (jobId != null) {
      job = tryToDo(new Callable<Job>() {
        public Job call() throws Exception {
          return bigquery.jobs().get(projectId, jobId).execute();
        }
      });

      if (job == null) {
        throw new SampleDashboardException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Wasn't able to get a job for jobId " + jobId);
      }
    }
  }

  public void beginQuery() throws SampleDashboardException {
    final Job queryJob = makeJob(buildExampleQuery());

    job = tryToDo(new Callable<Job>() {
      public Job call() throws Exception {
        return bigquery.jobs().insert(queryJob).setProjectId(projectId).execute();
      }
    });

    Preconditions.checkNotNull(job);
    enqueueWaitingTask();
  }

  public boolean jobSucceeded() {
    return (job != null && job.getStatus().getErrorResult() == null);
  }

  public String getJobErrorMessage() {
    if (job != null && job.getStatus().getErrorResult() != null) {
      return job.getStatus().getErrorResult().getMessage();
    } else {
      return "";
    }
  }

  public boolean jobIsDone() {
    String status = getJobStatus();
    return (status != null && ("DONE").equalsIgnoreCase(status));
  }

  public String getJobStatus() {
    return (job != null) ? job.getStatus().getState() : null;
  }

  public List<Bigqueryfield> getSchemaFieldNames() throws SampleDashboardException {
    if (job != null) {
      final Tablereference tableReference =
          job.getConfiguration().getQuery().getDestinationTable();

      Table table = tryToDo(new Callable<Table>() {
        public Table call() throws IOException {
          return bigquery.tables().get(tableReference.getProjectId(),
              tableReference.getDatasetId(),
              tableReference.getTableId()).execute();
        }
      });

      Preconditions.checkNotNull(table);
      Preconditions.checkNotNull(table.getSchema());
      Preconditions.checkNotNull(table.getSchema().getFields());
      return table.getSchema().getFields();
    }
    return null;
  }

  public List<Row> getTableData() throws SampleDashboardException {
    if (job != null) {
      final Tablereference tableReference =
          job.getConfiguration().getQuery().getDestinationTable();

      TableDataList tableDataList = tryToDo(new Callable<TableDataList>() {
        public TableDataList call() throws IOException {
          return bigquery.tabledata().list(tableReference.getProjectId(),
              tableReference.getDatasetId(),
              tableReference.getTableId()).execute();
        }
      });

      Preconditions.checkNotNull(tableDataList);
      Preconditions.checkNotNull(tableDataList.getRows());
      return tableDataList.getRows();
    }
    return null;
  }

  /**
   * Constructs a task with necessary parameters and options and puts it in
   * App Engine's default task queue.
   */
  public void enqueueWaitingTask() {
    TaskOptions options = TaskOptions.Builder.withDefaults();
    options.param("jobId", job.getJobReference().getJobId());
    options.param("userId", userId);
    options.url("/task");
    options.countdownMillis(1000);
    options.retryOptions(RetryOptions.Builder.withTaskRetryLimit(0));

    Queue queue = QueueFactory.getDefaultQueue();
    queue.add(options);
  }

  public static String buildExampleQuery() {
    String[] west = {"WA", "OR", "CA", "AK", "HI", "ID", "MT", "WY", "NV", "UT", "CO", "AZ", "NM"};
    String[] south = {"OK", "TX", "AR", "LA", "TN", "MS", "AL", "KY", "GA", "FL", "SC", "NC", "VA",
        "WV", "MD", "DC", "DE"};
    String[] midwest = {"ND", "SD", "NE", "KS", "MN", "IA", "MO", "WI", "IL", "IN", "MI", "OH"};
    String[] northeast = {"NY", "PA", "NJ", "CT", "RI", "MA", "VT", "NH", "ME"};

    Joiner joiner = Joiner.on("', '");

    String query = "SELECT IF (state IN ('" + joiner.join(west) + "'), 'West', \n\t"
        + "IF (state IN ('" + joiner.join(south) + "'), 'South', \n\t"
        + "IF (state IN ('" + joiner.join(midwest) + "'), 'Midwest', \n\t"
        + "IF (state IN ('" + joiner.join(northeast) + "'), 'Northeast', 'None')))) "
        + "as region, \naverage_mother_age, \naverage_father_age, \nstate, \nyear \n"
        + "FROM (SELECT year, \n\t\tstate, \n\t\tSUM(mother_age)/COUNT(mother_age) as "
        + "average_mother_age, \n\t\tSUM(father_age)/COUNT(father_age) as average_father_age \n\t"
        + "FROM publicdata:samples.natality \n\tWHERE father_age < 99 \n\tGROUP BY year, state) \n"
        + "ORDER BY year, region;";

    return query;
  }

  /**
   * Instantiates an example job and sets required fields.
   */
  private Job makeJob(String query) {
    Jobconfigurationquery jobconfigurationquery = new Jobconfigurationquery();

    jobconfigurationquery.setQuery(query);
    jobconfigurationquery.setCreateDisposition("CREATE_IF_NEEDED");

    Jobconfiguration jobconfiguration = new Jobconfiguration();
    jobconfiguration.setQuery(jobconfigurationquery);

    Jobreference jobreference = new Jobreference();
    jobreference.setProjectId(projectId);

    Job newJob = new Job();
    newJob.setConfiguration(jobconfiguration);
    newJob.setJobReference(jobreference);

    return newJob;
  }

  /**
   * Attempts to run the given callback with a number of retries. If the callback
   * responds with SC_UNAUTHORIZED, the tokens are refreshed.
   * @throws SampleDashboardException
   */
  private <T> T tryToDo(Callable<T> callback) throws SampleDashboardException {
    int retries = 3;
    int currentTry = 0;
    SampleDashboardException sdex = null;
    while (currentTry < retries) {
      currentTry ++;
      try {
        return callback.call();
      } catch (Exception ex) {
        sdex = new SampleDashboardException(ex);
        log.warning("Caught exception (" + sdex.getStatusCode() + "): " + sdex.getMessage());

        if (sdex.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
          updateAccess(authUtils.refreshAccessToken());
        }
      }
    }
    throw Preconditions.checkNotNull(sdex);
  }

  private void updateAccess(String access) {
    if (access != null) {
      gapr.setAccessToken(access);
    }
  }
}
