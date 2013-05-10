// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet responds to a post request with the data in the datastore for the
 * user in the form of json parseable by a DataTable constructor.  Also returns
 * the stored message and whether their query failed.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */
public class DataServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  
  // It's important that the first column be a string and the second a number.
  // Also, it is expected that these are the same length.
  private final String[] labels = new String[]
      {"State", "Year", "Average Mother Age", "Average Father Age", "U.S. Census Region"};
  private final String[] properties = new String[]
      {"state", "year", "average_mother_age", "average_father_age", "region"};
  private final String[] types = new String[] {"string", "number", "number", "number", "string"};

  /**
   * Attempts to retrieve results for the logged-in user.  If the datastore contains
   * results, they are written into the response as JSON.
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    JsonWriter jsonWriter = new JsonWriter(response.getWriter()).beginObject();
    String userId = UserServiceFactory.getUserService().getCurrentUser().getUserId();
    DatastoreUtils datastoreUtils = new DatastoreUtils(userId);

    String jobStatus = datastoreUtils.getUserJobStatus();

    if (("DONE").equalsIgnoreCase(jobStatus)) {
      List<Entity> results = datastoreUtils.getResults();
      if (!results.isEmpty()) {
        writeResultsToMotionChartJson(jsonWriter, results);
      }
    }

    jsonWriter.name("failed").value(datastoreUtils.hasUserQueryFailed());
    jsonWriter.name("message").value(datastoreUtils.getUserMessage());
    jsonWriter.name("lastRun").value(datastoreUtils.getUserLastRunMessage());

    jsonWriter.endObject().close();
  }

  /**
   * Converts the query results retrieved from the datastore to json parsable by javascript
   * into a DataTable object for use with a motion chart.
   */
  private void writeResultsToMotionChartJson(JsonWriter jsonWriter, Iterable<Entity> results)
      throws IOException {
    jsonWriter.name("data").beginObject();

    // Write the header.
    jsonWriter.name("cols").beginArray();
    for (int i = 0; i < properties.length; i++) {
      jsonWriter.beginObject()
          .name("id").value(properties[i])
          .name("label").value(labels[i])
          .name("type").value(types[i])
          .endObject();
    }
    jsonWriter.endArray();

    // Write the data.
    jsonWriter.name("rows").beginArray();
    for (Entity entity : results) {
      jsonWriter.beginObject().name("c").beginArray();
      for (int i = 0; i < properties.length; i++) {
        String value = "";
        if (entity.getProperty(properties[i]) != null) {
          value = String.valueOf(entity.getProperty(properties[i]));
        }

        jsonWriter.beginObject().name("v").value(value).endObject();
      }
      jsonWriter.endArray().endObject();
    }
    jsonWriter.endArray();

    jsonWriter.endObject();
  }
}
