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

package com.google.cloud.taskqueue.client.sample;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.cloud.taskqueue.client.sample.model.Task;
import com.google.cloud.taskqueue.client.sample.model.TaskQueue;
import com.google.cloud.taskqueue.client.sample.model.Util;

import java.io.IOException;
import java.util.List;

/**
 * Sample which leases task from TaskQueueService, performs work on the payload
 * of the task and then  deletes the task.
 * @author Vibhooti Verma
 */
public class TaskQueueSample {

  static final String APP_DESCRIPTION = "TaskQueue API Java Client Sample";
  static String projectName;
  static String taskQueueName;
  static int leaseSecs;
  static int numTasks;

  public static boolean parseParams(String[] args) {
    try {
      projectName = args[0];
      taskQueueName = args[1];
      leaseSecs = Integer.parseInt(args[2]);
      numTasks = Integer.parseInt(args[3]);
      return true;
   } catch (ArrayIndexOutOfBoundsException ae) {
     System.out.println("Insufficient Arguments");
     return false;
   } catch (NumberFormatException ae) {
     System.out.println("Please specify lease seconds and Number of tasks to"
         + "lease, in number format");
     return false;
   }
 }

  public static void printUsage() {
    System.out.println("mvn -q exec:java -Dexec.args=\""
                       + "/TaskQueueApiSample <ProjectName> <TaskQueueName> "
                       + "<LeaseSeconds> <NumberOfTasksToLease>\"");
  }

  /**
   * You can perform following operations using TaskQueueService.
   * 1. leasetasks
   * 2. gettask
   * 3. delete task
   * 4. getqueue
   * For illustration purpose, we are first getting the stats of the specified
   * queue followed by leasing tasks and deleting them. Users can change the
   * flow according to their needs.
   */

  public static void main(String[] args) {
    Util.enableLogging();
    try {
      if (args.length != 4) {
        System.out.println("Insuficient arguments");
        printUsage();
        System.exit(1);
      } else if (!parseParams(args)) {
        printUsage();
        System.exit(1);
      }
      try {
        setUpTransport();
        authorize();
        getTaskQueue();
        leaseAndDeleteTask();
      } catch (HttpResponseException e) {
        System.err.println(e.response.parseAsString());
        throw e;
      }
    } catch (Throwable t) {
      t.printStackTrace();
      Auth.revoke();
      System.exit(1);
    }
  }

  private static void setUpTransport() {
    JsonHttpParser parser = new JsonHttpParser();
    parser.jsonFactory = Util.JSON_FACTORY;
    Util.TRANSPORT.addParser(parser);
  }

  /**
   * Method to perform authorization using oauth1.
   * @throws Exception
   */

  private static void authorize() throws Exception {
    Auth.authorize();
  }

  /**
   * Method to get details of the queue using the args specified by user.
   * @throws IOException
   */
  private static void getTaskQueue() throws IOException {
    TaskQueue queue = new TaskQueue();
    System.out.println(queue.get(projectName, taskQueueName, true).toString());
  }

  /**
   * Method to get details of the task.
   *
   */
  private static void getTask() throws IOException {
    Task task = new Task();
    String id = "id";
    task.get(projectName, taskQueueName, id);
  }

  /**
   * Method to lease multiple tasks, perform the tasks and one by one along with
   * deleting them from the taskqueue.
   * @throws IOException
   */
  private static void leaseAndDeleteTask() throws IOException {
   Task task = new Task();
    List<Task> leasedTasks = task.lease(projectName, taskQueueName, leaseSecs,
        numTasks);
    if (leasedTasks.size() == 0) {
      System.out.println("No tasks to lease and hence exiting");
    }
    for (Task leasedTask : leasedTasks) {
      leasedTask.executeTask();
      leasedTask.delete();
    }
  }
}
