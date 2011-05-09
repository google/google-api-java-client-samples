// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.buzz.appengine.oauth2;

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
