/*
 * Copyright (c) 2011 Google Inc.
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
 */.

package com.google.api.client.sample.buzz.appengine.oauth;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * Singleton to manage an instance of a {@link PersistenceManagerFactory}.
 *
 *  One must use a singleton or other dependency injection to manage a single
 * instance of a {@link PersistenceManagerFactory} because creation of the
 * {@link PersistenceManagerFactory} is very expensive.
 *
 * @author moshenko@google.com (Jacob Moshenko)
 *
 */
public final class PMF {
  private static final PersistenceManagerFactory pmfInstance =
      JDOHelper.getPersistenceManagerFactory("transactions-optional");

  private PMF() {
  }

  public static PersistenceManagerFactory get() {
    return pmfInstance;
  }
}
