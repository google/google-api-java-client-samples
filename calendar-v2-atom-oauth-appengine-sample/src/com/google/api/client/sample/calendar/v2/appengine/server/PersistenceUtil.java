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

package com.google.api.client.sample.calendar.v2.appengine.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

/**
 * Utility class for JDO persistence.
 *
 * @author Yaniv Inbar
 */
class PersistenceUtil {

  /** Persistence manager factory instance. */
  private static final PersistenceManagerFactory FACTORY =
      JDOHelper.getPersistenceManagerFactory("transactions-optional");

  static PersistenceManager getPersistenceManager() {
    return FACTORY.getPersistenceManager();
  }

  static <T> T makePersistent(T object) {
    PersistenceManager manager = getPersistenceManager();
    try {
      return manager.makePersistent(object);
    } finally {
      manager.close();
    }
  }

  private PersistenceUtil() {
  }
}
