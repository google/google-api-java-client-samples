/*
 * Copyright (c) 2010 Google Inc.
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

package com.google.cloud.taskqueue.client.sample.model;

import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.io.IOException;
import java.util.List;

/**
 * Class to encapsulate TaskQueueService's task object
 *
 * @author Vibhooti Verma
 */
public class Task extends GenericJson {

  @Key
  public String id;
  @Key
  public  String kind;
  @Key
  public long leaseTimestamp;
  @Key
  public String payloadBase64;
  // queueName is of format "projects/<project-name>/taskqueues/<taskqueue-name>"
  @Key
  public String queueName;

  public Task() {
  }
/**
 * Method to extract project name from full queue name.
 * queueName is of format "projects/<project-name>/taskqueues/<taskqueue-name>"
 *
 */
  public String getProjectFromQueueName() {
    try {
      String arr[] =  queueName.split("/");
      return arr[1];
    } catch (ArrayIndexOutOfBoundsException ae) {
      System.out.println("Error: Project name could not be parsed from" +
          "queueName: " + queueName);
      return new String("");
   }
  }

  /**
   * Method to extract queue name from full queue name.
   * queueName is of format "projects/<project-name>/taskqueues/<taskqueue-name>"
   *
   */
  public String getQueueFromQueueName() {
    try {
      String arr[] =  queueName.split("/");
      return arr[3];
    } catch (ArrayIndexOutOfBoundsException ae) {
      System.out.println("Error: Queue name could not be parsed from " +
          "queueName: " + queueName);
      return new String("");
    }
   }

  /**
   * Method to delete task from taskqueue service.
   * This sends the delete request for this task object to taskqueue service.
   *
   * @throws: HttpResponseException if the http request call fails due to some
   * reason.
   */
  public void delete() throws HttpResponseException, IOException {
    HttpRequest request = Util.TRANSPORT.buildDeleteRequest();
    String projectName = getProjectFromQueueName();
    String queueName = getQueueFromQueueName();
    if (projectName.isEmpty() || queueName.isEmpty()) {
      System.out.println("Error parsing full queue name:" + this.queueName +
          " Hence unable to delete task" + this.id);
      return;
    }
    request.url = TaskQueueUrl.forTaskQueueServiceTasks(projectName, queueName);
    request.url.pathParts.add(this.id);
    try {
      request.execute();
    } catch (HttpResponseException hre) {
      System.out.println("Error deleting task: " + this.id);
      throw hre;
    }
  }

  /**
   * Method to get details of as task from taskqueue service given project name,
   * queue name and task id.
   *
   * @returns: Task object with all the details such as payload, leasetimestamp.
   */
  public Task get(String projectName, String taskQueueName, String id)
  throws IOException {
    HttpRequest request = Util.TRANSPORT.buildGetRequest();
    request.url = TaskQueueUrl.forTaskQueueServiceTasks(projectName,
                                                        taskQueueName);
    request.url.pathParts.add(id);
    return request.execute().parseAs(Task.class);
  }

  /**
   * Method to lease multiple tasks from taskqueue service.
   * @params:
   * projectName: Name of the project
   * taskqueueName: name of the queue whose task need to be leased.
   * leaseSecs: Number of seconds for which the task should be leased. This
   * should be approximately equal to time to execute the task.
   * numTasks: Number of tasks to be leased in one lease request. This usually
   * helps optimizing the time taken for the application to execute the tasks.
   *
   */
  public List<Task> lease(String projectName,
                          String taskQueueName,
                          int leaseSecs,
                          int numTasks) throws IOException {
    HttpRequest request = Util.TRANSPORT.buildPostRequest();
    request.url = TaskQueueUrl.forTaskQueueServiceTasks(
        projectName, taskQueueName, leaseSecs, numTasks);
    request.url.pathParts.add("lease");
    request.content = toContent();
    return request.execute().parseAs(TaskList.class).items;
  }

  private JsonCContent toContent() {
    JsonCContent result = new JsonCContent();
    result.data = this;
    result.jsonFactory = Util.JSON_FACTORY;
    return result;
  }

/**
 * This method actually performs the desired work on tasks. It can make use of
 * payload of the task. By default, we are just printing the payload of the
 * leased task."
 */

  public void executeTask() {
    System.out.println("Payload for the task:");
    System.out.println(this.payloadBase64);
  }
}
