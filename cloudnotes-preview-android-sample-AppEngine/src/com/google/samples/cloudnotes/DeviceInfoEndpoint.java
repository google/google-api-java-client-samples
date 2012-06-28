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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * @author Sriram Saroop
 */
@Api(name = "deviceinfoendpoint")
public class DeviceInfoEndpoint {

  /**
   * This method lists all the entities inserted in datastore. It uses HTTP GET method.
   * 
   * @return List of all entities persisted.
   */
  @SuppressWarnings({"cast", "unchecked"})
  public List<DeviceInfo> listDeviceInfo() {
    EntityManager mgr = getEntityManager();
    List<DeviceInfo> result = new ArrayList<DeviceInfo>();
    try {
      Query query = mgr.createQuery("select from DeviceInfo");
      for (Object obj : (List<Object>) query.getResultList()) {
        result.add(((DeviceInfo) obj));
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
  public DeviceInfo getDeviceInfo(@Named("id") String id) {
    EntityManager mgr = getEntityManager();
    DeviceInfo deviceinfo = null;
    try {
      deviceinfo = mgr.find(DeviceInfo.class, id);
    } finally {
      mgr.close();
    }
    return deviceinfo;
  }

  /**
   * This inserts the entity into App Engine datastore. It uses HTTP POST method.
   * 
   * @param deviceinfo the entity to be inserted.
   * @return The inserted entity.
   */
  public DeviceInfo insertDeviceInfo(DeviceInfo deviceinfo) {
    EntityManager mgr = getEntityManager();
    try {
      mgr.persist(deviceinfo);
    } finally {
      mgr.close();
    }
    return deviceinfo;
  }

  /**
   * This method is used for updating a entity. It uses HTTP PUT method.
   * 
   * @param deviceinfo the entity to be updated.
   * @return The updated entity.
   */
  public DeviceInfo updateDeviceInfo(DeviceInfo deviceinfo) {
    EntityManager mgr = getEntityManager();
    try {
      mgr.persist(deviceinfo);
    } finally {
      mgr.close();
    }
    return deviceinfo;
  }

  /**
   * This method removes the entity with primary key id. It uses HTTP DELETE method.
   * 
   * @param id the primary key of the entity to be deleted.
   * @return The deleted entity.
   */
  public DeviceInfo removeDeviceInfo(@Named("id") String id) {
    EntityManager mgr = getEntityManager();
    DeviceInfo deviceinfo = null;
    try {
      deviceinfo = mgr.find(DeviceInfo.class, id);
      mgr.remove(deviceinfo);
    } finally {
      mgr.close();
    }
    return deviceinfo;
  }

  private static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
  }

}
