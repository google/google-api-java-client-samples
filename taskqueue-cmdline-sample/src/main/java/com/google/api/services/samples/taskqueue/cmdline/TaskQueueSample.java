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

package com.google.api.services.samples.taskqueue.cmdline;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.samples.shared.cmdline.oauth2.LocalServerReceiver;
import com.google.api.services.samples.shared.cmdline.oauth2.OAuth2Native;
import com.google.api.services.taskqueue.Taskqueue;
import com.google.api.services.taskqueue.TaskqueueRequest;
import com.google.api.services.taskqueue.TaskqueueScopes;
import com.google.api.services.taskqueue.model.Task;
import com.google.api.services.taskqueue.model.Tasks;

import java.io.IOException;
import java.util.Arrays;

/**
 * Sample which leases task from TaskQueueService, performs work on the payload of the task and then
 * deletes the task.
 *
 * @author Vibhooti Verma
 */
public class TaskQueueSample {

  private static String projectName;
  private static String taskQueueName;
  private static int leaseSecs;
  private static int numTasks;

  /** Global instance of the HTTP transport. */
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  /**
   * You can perform following operations using TaskQueueService. 1. leasetasks 2. gettask 3. delete
   * task 4. getqueue For illustration purpose, we are first getting the stats of the specified
   * queue followed by leasing tasks and then deleting them. Users can change the flow according to
   * their needs.
   */
  private static void run() throws Exception {
    // authorization
    Credential credential = OAuth2Native.authorize(
        HTTP_TRANSPORT, JSON_FACTORY, new LocalServerReceiver(),
        Arrays.asList(TaskqueueScopes.TASKQUEUE));

    // set up Taskqueue
    Taskqueue taskQueue = new Taskqueue.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName("Google-TaskQueueSample/1.0")
        .setJsonHttpRequestInitializer(new JsonHttpRequestInitializer() {
            @Override
          public void initialize(JsonHttpRequest request) {
            TaskqueueRequest taskQueueRequest = (TaskqueueRequest) request;
            taskQueueRequest.setPrettyPrint(true);
          }
        }).build();

    // get queue
    com.google.api.services.taskqueue.model.TaskQueue queue = getQueue(taskQueue);
    System.out.println(queue);

    // lease, execute and delete tasks
    Tasks tasks = getLeasedTasks(taskQueue);
    if (tasks.getItems() == null || tasks.getItems().size() == 0) {
      System.out.println("No tasks to lease and hence exiting");
    } else {
      for (Task leasedTask : tasks.getItems()) {
        executeTask(leasedTask);
        deleteTask(taskQueue, leasedTask);
      }
    }
  }

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
      System.out.println(
          "Please specify lease seconds and Number of tasks to" + "lease, in number format");
      return false;
    }
  }

  public static void printUsage() {
    System.out.println("mvn -q exec:java -Dexec.args=\"" + "<ProjectName> <TaskQueueName> "
        + "<LeaseSeconds> <NumberOfTasksToLease>\"");
  }

  public static void main(String[] args) {
    if (args.length != 4) {
      System.out.println("Insuficient arguments");
      printUsage();
      System.exit(1);
    } else if (!parseParams(args)) {
      printUsage();
      System.exit(1);
    }

    try {
      try {
        run();
        // success!
        return;
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  /**
   * Method that sends a get request to get the queue.
   *
   * @param taskQueue The task queue that should be used to get the queue from.
   * @return {@link com.google.api.services.taskqueue.model.TaskQueue}
   * @throws IOException if the request fails.
   */
  private static com.google.api.services.taskqueue.model.TaskQueue getQueue(Taskqueue taskQueue)
      throws IOException {
    Taskqueue.Taskqueues.Get request = taskQueue.taskqueues().get(projectName, taskQueueName);
    request.setGetStats(true);
    return request.execute();
  }

  /**
   * Method that sends a lease request to the specified task queue.
   *
   * @param taskQueue The task queue that should be used to lease tasks from.
   * @return {@link Tasks}
   * @throws IOException if the request fails.
   */
  private static Tasks getLeasedTasks(Taskqueue taskQueue) throws IOException {
    Taskqueue.Tasks.Lease leaseRequest =
        taskQueue.tasks().lease(projectName, taskQueueName, numTasks, leaseSecs);
    return leaseRequest.execute();
  }

  /**
   * This method actually performs the desired work on tasks. It can make use of payload of the
   * task. By default, we are just printing the payload of the leased task.
   *
   * @param task The task that should be executed.
   */
  private static void executeTask(Task task) {
    System.out.println("Payload for the task:");
    System.out.println(task.getPayloadBase64());
  }

  /**
   * Method that sends a delete request for the specified task object to the taskqueue service.
   *
   * @param taskQueue The task queue the specified task lies in.
   * @param task The task that should be deleted.
   * @throws IOException if the request fails
   */
  private static void deleteTask(Taskqueue taskQueue, Task task) throws IOException {
    Taskqueue.Tasks.Delete request = taskQueue.tasks().delete(
        projectName, taskQueueName, task.getId());
    request.execute();
  }
}
