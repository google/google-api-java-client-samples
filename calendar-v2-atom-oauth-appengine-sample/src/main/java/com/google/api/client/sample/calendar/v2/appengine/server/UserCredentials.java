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

import com.google.appengine.api.users.UserServiceFactory;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * OAuth per-user credentials store.
 *
 * @author Yaniv Inbar
 */
@PersistenceCapable
public final class UserCredentials {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  String userId;

  @Persistent
  String gsessionid;

  @Persistent
  boolean temporary;

  @Persistent
  String token;

  @Persistent
  String tokenSecret;

  UserCredentials makePersistent() {
    return PersistenceUtil.makePersistent(this);
  }

  static String getCurrentUserId() {
    return UserServiceFactory.getUserService().getCurrentUser().getUserId();
  }

  static UserCredentials forCurrentUser() {
    PersistenceManager manager = PersistenceUtil.getPersistenceManager();
    try {
      return forCurrentUser(manager);
    } finally {
      manager.close();
    }
  }

  static void deleteCurrentUserFromStore() {
    PersistenceManager manager = PersistenceUtil.getPersistenceManager();
    try {
      UserCredentials cred = forCurrentUser(manager);
      if (cred != null) {
        manager.deletePersistent(cred);
      }
    } finally {
      manager.close();
    }
  }

  private static UserCredentials forCurrentUser(PersistenceManager manager) {
    @SuppressWarnings("unchecked")
    List<UserCredentials> creds = (List<UserCredentials>) manager.newQuery(
        UserCredentials.class, "userId == '" + getCurrentUserId() + "'").execute();
    return creds.isEmpty() ? null : creds.get(0);
  }
}
