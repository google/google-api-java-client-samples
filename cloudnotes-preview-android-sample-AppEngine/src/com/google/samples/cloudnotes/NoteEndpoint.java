/*
 * Copyright (c) 2012 Google Inc.
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
package com.google.samples.cloudnotes;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author Sriram Saroop
 */
@Api(name = "noteendpoint", clientIds = {Ids.CLIENT_ID}, audiences = {Ids.AUDIENCE})
public class NoteEndpoint {

  /**
   * This method lists all the entities inserted in datastore. It uses HTTP GET method.
   * 
   * @return List of all entities persisted.
   */
  @SuppressWarnings({"cast", "unchecked"})
  public List<Note> listNote(User user) throws UnauthorizedException {
    if (user == null) {
      throw new UnauthorizedException("missing user");
    }
    EntityManager mgr = getEntityManager();
    List<Note> result = new ArrayList<Note>();
    try {
      Query query = mgr.createQuery("select n from Note n where n.emailAddress = :emailAddress");
      query.setParameter("emailAddress", user.getEmail());
      for (Object obj : (List<Object>) query.getResultList()) {
        result.add(((Note) obj));
      }
    } finally {
      mgr.close();
    }
    return result;
  }

  /**
   * This method gets the entity having primary key id. It uses HTTP GET method.
   * 
   * @param id the primary key of the java bean.
   * @return The entity with primary key id.
   */
  public Note getNote(@Named("id") String id, User user) throws UnauthorizedException,
      NotFoundException {
    if (user == null) {
      throw new UnauthorizedException("missing user");
    }
    EntityManager mgr = getEntityManager();
    Note note = null;
    try {
      note = mgr.find(Note.class, id);
      if (note == null || !note.getEmailAddress().equalsIgnoreCase(user.getEmail())) {
        throw new NotFoundException("note not found");
      }
    } finally {
      mgr.close();
    }
    return note;
  }

  /**
   * This inserts the entity into App Engine datastore. It uses HTTP POST method.
   * 
   * @param note the entity to be inserted.
   * @return The inserted entity.
   */
  public Note insertNote(Note note, User user) throws UnauthorizedException {
    if (user == null) {
      throw new UnauthorizedException("missing user");
    }
    EntityManager mgr = getEntityManager();
    try {
      note.setEmailAddress(user.getEmail());
      mgr.persist(note);
    } finally {
      mgr.close();
    }
    DevicePing.pingAllDevices(user.getEmail(), note.getId(), "insert");
    return note;
  }

  /**
   * This method is used for updating a entity. It uses HTTP PUT method.
   * 
   * @param note the entity to be updated.
   * @return The updated entity.
   */
  public Note updateNote(Note note, User user) throws UnauthorizedException {
    if (user == null) {
      throw new UnauthorizedException("missing user");
    }
    EntityManager mgr = getEntityManager();
    try {
      note.setEmailAddress(user.getEmail());
      mgr.persist(note);
    } finally {
      mgr.close();
    }
    DevicePing.pingAllDevices(user.getEmail(), note.getId(), "update");
    return note;
  }

  /**
   * This method removes the entity with primary key id. It uses HTTP DELETE method.
   * 
   * @param id the primary key of the entity to be deleted.
   * @return The deleted entity.
   */
  public Note removeNote(@Named("id") String id, User user) throws UnauthorizedException,
      NotFoundException {
    if (user == null) {
      throw new UnauthorizedException("missing user");
    }
    EntityManager mgr = getEntityManager();
    Note note = null;
    try {
      note = mgr.find(Note.class, id);
      if (note == null || !note.getEmailAddress().equalsIgnoreCase(user.getEmail())) {
        throw new NotFoundException("note not found");
      }
      mgr.remove(note);
    } finally {
      mgr.close();
    }
    DevicePing.pingAllDevices(user.getEmail(), id, "remove");
    return note;
  }

  private static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
  }

}
