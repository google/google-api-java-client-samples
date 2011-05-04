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

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.util.Key;


/**
 * Prepares Request URL for TaskQueue operations.
 *
 * @author Vibhooti Verma
 */
public class TaskQueueUrl extends GoogleUrl {

  @Key("leaseSecs")
  public Integer leaseSecs;
  @Key("numTasks")
  public Integer numTasks;
  @Key("getStats")
  public boolean getStats;

  /** Constructs a new TaskQueue URL from the given encoded URL. */
  public TaskQueueUrl(String encodedUrl) {
    super(encodedUrl);
    alt = "json";
    if (Util.DEBUG) {
      prettyprint = true;
    }
  }

  public static TaskQueueUrl forTaskQueueService() {
    return new TaskQueueUrl("https://www.googleapis.com/taskqueue/v1beta1/projects");
  }

  public static TaskQueueUrl forTaskQueueServiceQueues(String projectName,
                                                       String taskQueueName,
                                                       boolean getStats) {
    TaskQueueUrl result = forTaskQueueService();
    result.pathParts.add(projectName);
    result.pathParts.add("taskqueues");
    result.pathParts.add(taskQueueName);
    result.getStats = getStats;
    return result;
  }
  public static TaskQueueUrl forTaskQueueServiceTasks(String projectName,
                                                      String taskQueueName) {
    TaskQueueUrl result = forTaskQueueService();
    result.pathParts.add(projectName);
    result.pathParts.add("taskqueues");
    result.pathParts.add(taskQueueName);
    result.pathParts.add("tasks");
    return result;
  }
  public static TaskQueueUrl forTaskQueueServiceTasks(String projectName,
                                                      String taskQueueName,
                                                      int leaseSecs,
                                                      int numTasks) {
    TaskQueueUrl result = forTaskQueueServiceTasks(projectName, taskQueueName);
    result.leaseSecs = leaseSecs;
    result.numTasks = numTasks;
    return result;
 }
}
