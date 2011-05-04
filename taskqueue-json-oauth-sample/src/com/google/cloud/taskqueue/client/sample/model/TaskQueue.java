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
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.io.IOException;

/**
 * * Class to encapsulate TaskQueueService's TaskQueue object
 *
 * @author Vibhooti Verma
 */
public class TaskQueue extends GenericJson {

  /** TaskQueue identifier. */
  @Key
  public String id;
  @Key
  public  String kind;

  QueueStats stats;

  /**
   * Method to get details of a taskqueue from taskqueue service.
   * @params:
   * projectName: Name of the taskqueue project
   * queueName: Name of the taskqueue whose details are requested
   * getStats: Flag to get more stats of the queue
   * @returns A TaskQueue object with all the details.
   */
  public TaskQueue get(String projectName, String taskQueueName, boolean getStats)
  throws IOException {
    HttpRequest request = Util.TRANSPORT.buildGetRequest();
    request.url = TaskQueueUrl.forTaskQueueServiceQueues(projectName,
                                                         taskQueueName,
                                                         getStats);
    return request.execute().parseAs(TaskQueue.class);
  }

  /** Returns a new JSON-C content serializer for TaskQueue. */
  private JsonCContent toContent() {
    JsonCContent result = new JsonCContent();
    result.data = this;
    result.jsonFactory = Util.JSON_FACTORY;
    return result;
  }

  public final String toString(Object item) {
    return jsonFactory.toString();
 }
}
