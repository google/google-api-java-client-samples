// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.api.client.sample.bigquery.appengine.dashboard;

import com.google.api.client.util.Preconditions;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Utility methods for inserting, accessing, and deleting data in the datastore.
 *
 * @author lparkinson@google.com (Laura Parkinson)
 */
public class DatastoreUtils {

  public static final String FAILED = "FAILED";

  private final Key userEntityKey;
  private final String resultKind;
  private final DatastoreService service;
  private Entity userEntity;

  public DatastoreUtils(String userId) {
    userEntityKey = KeyFactory.createKey("User", userId);
    service = DatastoreServiceFactory.getDatastoreService();
    resultKind = userId + "Result";

    try {
      userEntity = service.get(userEntityKey);
    } catch (EntityNotFoundException e) {
      userEntity = null;
    }
  }

  public boolean hasUserEntity() {
    return userEntity != null;
  }

  private void createUserIfNull() {
    if (userEntity == null) {
      userEntity = new Entity(userEntityKey);
    }
  }

  /**
   * Updates the user entity with the message and status, creating it if necessary.
   */
  public void putUserInformation(String message, String status) {
    createUserIfNull();
    userEntity.setProperty("jobStatus", status);
    userEntity.setProperty("message", message);
    service.put(userEntity);
  }

  /**
   * Updates the user entity with the current time, creating it if necessary.
   */
  public void updateSuccessfulQueryTimestamp() {
    createUserIfNull();
    userEntity.setProperty("timestamp", System.currentTimeMillis());
    service.put(userEntity);
  }

  public String getUserJobStatus() {
    return getUserEntityProperty("jobStatus");
  }

  public Boolean hasUserQueryFailed() {
    return (FAILED).equalsIgnoreCase(getUserJobStatus());
  }

  public String getUserMessage() {
    return getUserEntityProperty("message");
  }

  public String getUserLastRunMessage() {
    String timestamp = getUserEntityProperty("timestamp");
    if (timestamp == null) {
      return "never";
    }
    SimpleDateFormat format = new SimpleDateFormat("k:mm:ss 'on' MMMM d, yyyy zzz");
    Date date = new Date(Long.valueOf(timestamp));
    return format.format(date);
  }

  private String getUserEntityProperty(String propertyName) {
    if (userEntity != null && userEntity.hasProperty(propertyName)) {
      return String.valueOf(userEntity.getProperty(propertyName));
    }
    return null;
  }

  public List<Entity> getResults() {
    Query query = new Query(resultKind, userEntityKey);
    FetchOptions options = FetchOptions.Builder.withChunkSize(2000);
    return service.prepare(query).asList(options);
  }

  /**
   * Removes any existing results for the user from the datastore.
   */
  public void deleteExistingResults() {
    ArrayList<Key> keys = new ArrayList<Key>();
    List<Entity> results = getResults();
    for (Entity entity : results) {
      keys.add(entity.getKey());
    }
    service.delete(keys);
  }

  /**
   * Copies each row of the given data into an entity, then puts all the entities
   * to the datastore with the user's entity as their ancestor.
   */
  public void copyQueryResultsToDatastore(List<TableFieldSchema> fields,
      List<TableRow> rows) {
    ArrayList<Entity> entities = new ArrayList<Entity>();
    Iterator<TableRow> rowsIterator = rows.iterator();
    while (rowsIterator.hasNext()) {
      Entity entity = new Entity(resultKind, userEntityKey);

      // Copy the row into the entity -- fields become properties.
      Iterator<TableFieldSchema> fieldsIterator = fields.iterator();
      Iterator<TableCell> dataIterator = rowsIterator.next().getF().iterator();

      Preconditions.checkState(fieldsIterator.hasNext() == dataIterator.hasNext());
      while (fieldsIterator.hasNext() && dataIterator.hasNext()) {
        Object value = dataIterator.next().getV();
        String strValue = (value != null) ? String.valueOf(value) : null;
        entity.setProperty(fieldsIterator.next().getName(), strValue);
        Preconditions.checkState(fieldsIterator.hasNext() == dataIterator.hasNext());
      }
      entities.add(entity);
    }
    service.put(entities);
  }
}
