/*
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
/*
 * This file was generated.
 *  with google-apis-code-generator 1.1.1 (build: 2012-06-25 20:14:04 UTC)
 *  on 2012-06-28 at 06:42:16 UTC 
 */

package com.appspot.api.services.noteendpoint;


import com.google.api.client.googleapis.services.GoogleClient;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpMethod;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.common.base.Preconditions;

import java.io.IOException;


/**
 * Service definition for Noteendpoint (v1).
 *
 * <p>
 * 
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link JsonHttpRequestInitializer} to initialize global parameters via its
 * {@link Builder}. Sample usage:
 * </p>
 *
 * <pre>
  public class NoteendpointRequestInitializer implements JsonHttpRequestInitializer {
      public void initialize(JsonHttpRequest request) {
        NoteendpointRequest noteendpointRequest = (NoteendpointRequest)request;
        noteendpointRequest.setPrettyPrint(true);
        noteendpointRequest.setKey(ClientCredentials.KEY);
    }
  }
 * </pre>
 *
 * @since 1.3.0
 * @author Google, Inc.
 */
public class Noteendpoint extends GoogleClient {

  /**
   * The default encoded base path of the service. This is determined when the library is generated
   * and normally should not be changed.
   * @deprecated (scheduled to be removed in 1.8) Use "/" + {@link #DEFAULT_SERVICE_PATH}.
   */
  @Deprecated
  public static final String DEFAULT_BASE_PATH = "/_ah/api/noteendpoint/v1/";

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://myapp.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "noteendpoint/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Construct a Noteendpoint instance to connect to the Noteendpoint service.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport The transport to use for requests
   * @param jsonFactory A factory for creating JSON parsers and serializers
   * @deprecated (scheduled to be removed in 1.8) Use
   *             {@link #Noteendpoint(HttpTransport, JsonFactory, HttpRequestInitializer)}.
   */
  @Deprecated
  public Noteendpoint(HttpTransport transport, JsonFactory jsonFactory) {
    super(transport, jsonFactory, DEFAULT_BASE_URL);
  }

  /**
   * Construct a Noteendpoint instance to connect to the Noteendpoint service.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport The transport to use for requests
   * @param jsonFactory A factory for creating JSON parsers and serializers
   * @param httpRequestInitializer The HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Noteendpoint(HttpTransport transport, JsonFactory jsonFactory,
      HttpRequestInitializer httpRequestInitializer) {
    super(transport, jsonFactory, DEFAULT_ROOT_URL, DEFAULT_SERVICE_PATH, httpRequestInitializer);
  }

  /**
   * Construct a Noteendpoint instance to connect to the Noteendpoint service.
   *
   * @param transport The transport to use for requests
   * @param jsonHttpRequestInitializer The initializer to use when creating an JSON HTTP request
   * @param httpRequestInitializer The initializer to use when creating an {@link HttpRequest}
   * @param jsonFactory A factory for creating JSON parsers and serializers
   * @param jsonObjectParser JSON parser to use or {@code null} if unused
   * @param baseUrl The base URL of the service on the server
   * @param applicationName The application name to be sent in the User-Agent header of requests
   */
  @Deprecated
  Noteendpoint(
      HttpTransport transport,
      JsonHttpRequestInitializer jsonHttpRequestInitializer,
      HttpRequestInitializer httpRequestInitializer,
      JsonFactory jsonFactory,
      JsonObjectParser jsonObjectParser,
      String baseUrl,
      String applicationName) {
      super(transport,
          jsonHttpRequestInitializer,
          httpRequestInitializer,
          jsonFactory,
          jsonObjectParser,
          baseUrl,
          applicationName);
  }

  /**
   * Construct a Noteendpoint instance to connect to the Noteendpoint service.
   *
   * @param transport The transport to use for requests
   * @param jsonHttpRequestInitializer The initializer to use when creating an JSON HTTP request
   * @param httpRequestInitializer The initializer to use when creating an {@link HttpRequest}
   * @param jsonFactory A factory for creating JSON parsers and serializers
   * @param jsonObjectParser JSON parser to use or {@code null} if unused
   * @param rootUrl The root URL of the service on the server
   * @param servicePath The service path of the service on the server
   * @param applicationName The application name to be sent in the User-Agent header of requests
   */
  Noteendpoint(
      HttpTransport transport,
      JsonHttpRequestInitializer jsonHttpRequestInitializer,
      HttpRequestInitializer httpRequestInitializer,
      JsonFactory jsonFactory,
      JsonObjectParser jsonObjectParser,
      String rootUrl,
      String servicePath,
      String applicationName) {
      super(transport,
          jsonHttpRequestInitializer,
          httpRequestInitializer,
          jsonFactory,
          jsonObjectParser,
          rootUrl,
          servicePath,
          applicationName);
  }

  @Override
  protected void initialize(JsonHttpRequest jsonHttpRequest) throws IOException {
    super.initialize(jsonHttpRequest);
  }


  /**
   * Returns an instance of a new builder.
   *
   * @param transport The transport to use for requests
   * @param jsonFactory A factory for creating JSON parsers and serializers
   * @deprecated (scheduled to removed in 1.8) Use
   *             {@link Builder#Builder(HttpTransport, JsonFactory, HttpRequestInitializer)}.
   */
   @Deprecated
   public static Builder builder(HttpTransport transport, JsonFactory jsonFactory) {
     return new Builder(transport, jsonFactory, new GenericUrl(DEFAULT_BASE_URL));
   }


  /**
   * Create a request for the method "updateNote".
   *
   * This request holds the parameters needed by the the noteendpoint server.  After setting any
   * optional parameters, call the {@link UpdateNote#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.appspot.api.services.noteendpoint.model.Note}
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public UpdateNote updateNote(com.appspot.api.services.noteendpoint.model.Note content) throws IOException {
    UpdateNote result = new UpdateNote(content);
    initialize(result);
    return result;
  }


  public class UpdateNote extends NoteendpointRequest {

    private static final String REST_PATH = "note";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    UpdateNote(com.appspot.api.services.noteendpoint.model.Note content) {
      super(Noteendpoint.this, HttpMethod.PUT, REST_PATH, content);
      Preconditions.checkNotNull(content);
    }



    /**
     * Sends the "updateNote" request to the Noteendpoint server.
     *
     * @return the {@link com.appspot.api.services.noteendpoint.model.Note} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.noteendpoint.model.Note execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.noteendpoint.model.Note result = response.parseAs(
          com.appspot.api.services.noteendpoint.model.Note.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "updateNote" request to the Noteendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;Note&gt;() {

         public void onSuccess(Note content, GoogleHeaders responseHeaders) {
           log("Success");
         }

         public void onFailure(GoogleJsonError e, GoogleHeaders responseHeaders) {
           log(e.getMessage());
         }
       });
     * </pre>
     *
     * @param batch a single batch of requests
     * @param callback batch callback
     * @since 1.6
     */
    public void queue(com.google.api.client.googleapis.batch.BatchRequest batch,
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.noteendpoint.model.Note> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.noteendpoint.model.Note.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public UpdateNote setFields(String fields) {
      super.setFields(fields);
      return this;
    }



  }

  /**
   * Create a request for the method "listNote".
   *
   * This request holds the parameters needed by the the noteendpoint server.  After setting any
   * optional parameters, call the {@link ListNote#execute()} method to invoke the remote operation.
   *
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public ListNote listNote() throws IOException {
    ListNote result = new ListNote();
    initialize(result);
    return result;
  }


  public class ListNote extends NoteendpointRequest {

    private static final String REST_PATH = "note";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    ListNote() {
      super(Noteendpoint.this, HttpMethod.GET, REST_PATH, null);
    }



    /**
     * Sends the "listNote" request to the Noteendpoint server.
     *
     * @return the {@link com.appspot.api.services.noteendpoint.model.Notes} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.noteendpoint.model.Notes execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.noteendpoint.model.Notes result = response.parseAs(
          com.appspot.api.services.noteendpoint.model.Notes.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "listNote" request to the Noteendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;Notes&gt;() {

         public void onSuccess(Notes content, GoogleHeaders responseHeaders) {
           log("Success");
         }

         public void onFailure(GoogleJsonError e, GoogleHeaders responseHeaders) {
           log(e.getMessage());
         }
       });
     * </pre>
     *
     * @param batch a single batch of requests
     * @param callback batch callback
     * @since 1.6
     */
    public void queue(com.google.api.client.googleapis.batch.BatchRequest batch,
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.noteendpoint.model.Notes> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.noteendpoint.model.Notes.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public ListNote setFields(String fields) {
      super.setFields(fields);
      return this;
    }



  }

  /**
   * Create a request for the method "insertNote".
   *
   * This request holds the parameters needed by the the noteendpoint server.  After setting any
   * optional parameters, call the {@link InsertNote#execute()} method to invoke the remote operation.
   *
   * @param content the {@link com.appspot.api.services.noteendpoint.model.Note}
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public InsertNote insertNote(com.appspot.api.services.noteendpoint.model.Note content) throws IOException {
    InsertNote result = new InsertNote(content);
    initialize(result);
    return result;
  }


  public class InsertNote extends NoteendpointRequest {

    private static final String REST_PATH = "note";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    InsertNote(com.appspot.api.services.noteendpoint.model.Note content) {
      super(Noteendpoint.this, HttpMethod.POST, REST_PATH, content);
      Preconditions.checkNotNull(content);
    }



    /**
     * Sends the "insertNote" request to the Noteendpoint server.
     *
     * @return the {@link com.appspot.api.services.noteendpoint.model.Note} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.noteendpoint.model.Note execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.noteendpoint.model.Note result = response.parseAs(
          com.appspot.api.services.noteendpoint.model.Note.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "insertNote" request to the Noteendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;Note&gt;() {

         public void onSuccess(Note content, GoogleHeaders responseHeaders) {
           log("Success");
         }

         public void onFailure(GoogleJsonError e, GoogleHeaders responseHeaders) {
           log(e.getMessage());
         }
       });
     * </pre>
     *
     * @param batch a single batch of requests
     * @param callback batch callback
     * @since 1.6
     */
    public void queue(com.google.api.client.googleapis.batch.BatchRequest batch,
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.noteendpoint.model.Note> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.noteendpoint.model.Note.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public InsertNote setFields(String fields) {
      super.setFields(fields);
      return this;
    }



  }

  /**
   * Create a request for the method "removeNote".
   *
   * This request holds the parameters needed by the the noteendpoint server.  After setting any
   * optional parameters, call the {@link RemoveNote#execute()} method to invoke the remote operation.
   *
   * @param id
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public RemoveNote removeNote(String id) throws IOException {
    RemoveNote result = new RemoveNote(id);
    initialize(result);
    return result;
  }


  public class RemoveNote extends NoteendpointRequest {

    private static final String REST_PATH = "note/{id}";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    RemoveNote(String id) {
      super(Noteendpoint.this, HttpMethod.DELETE, REST_PATH, null);
      this.id = Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }



    /**
     * Sends the "removeNote" request to the Noteendpoint server.
     *
     * @return the {@link com.appspot.api.services.noteendpoint.model.Note} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.noteendpoint.model.Note execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.noteendpoint.model.Note result = response.parseAs(
          com.appspot.api.services.noteendpoint.model.Note.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "removeNote" request to the Noteendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;Note&gt;() {

         public void onSuccess(Note content, GoogleHeaders responseHeaders) {
           log("Success");
         }

         public void onFailure(GoogleJsonError e, GoogleHeaders responseHeaders) {
           log(e.getMessage());
         }
       });
     * </pre>
     *
     * @param batch a single batch of requests
     * @param callback batch callback
     * @since 1.6
     */
    public void queue(com.google.api.client.googleapis.batch.BatchRequest batch,
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.noteendpoint.model.Note> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.noteendpoint.model.Note.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public RemoveNote setFields(String fields) {
      super.setFields(fields);
      return this;
    }


    @com.google.api.client.util.Key
    private String id;

    /**

     */
    public String getId() {
      return id;
    }


    public RemoveNote setId(String id) {
      this.id = id;
      return this;
    }



  }

  /**
   * Create a request for the method "getNote".
   *
   * This request holds the parameters needed by the the noteendpoint server.  After setting any
   * optional parameters, call the {@link GetNote#execute()} method to invoke the remote operation.
   *
   * @param id
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public GetNote getNote(String id) throws IOException {
    GetNote result = new GetNote(id);
    initialize(result);
    return result;
  }


  public class GetNote extends NoteendpointRequest {

    private static final String REST_PATH = "note/{id}";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    GetNote(String id) {
      super(Noteendpoint.this, HttpMethod.GET, REST_PATH, null);
      this.id = Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }



    /**
     * Sends the "getNote" request to the Noteendpoint server.
     *
     * @return the {@link com.appspot.api.services.noteendpoint.model.Note} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.noteendpoint.model.Note execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.noteendpoint.model.Note result = response.parseAs(
          com.appspot.api.services.noteendpoint.model.Note.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "getNote" request to the Noteendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;Note&gt;() {

         public void onSuccess(Note content, GoogleHeaders responseHeaders) {
           log("Success");
         }

         public void onFailure(GoogleJsonError e, GoogleHeaders responseHeaders) {
           log(e.getMessage());
         }
       });
     * </pre>
     *
     * @param batch a single batch of requests
     * @param callback batch callback
     * @since 1.6
     */
    public void queue(com.google.api.client.googleapis.batch.BatchRequest batch,
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.noteendpoint.model.Note> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.noteendpoint.model.Note.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public GetNote setFields(String fields) {
      super.setFields(fields);
      return this;
    }


    @com.google.api.client.util.Key
    private String id;

    /**

     */
    public String getId() {
      return id;
    }


    public GetNote setId(String id) {
      this.id = id;
      return this;
    }



  }


  /**
   * Builder for {@link Noteendpoint}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends GoogleClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport The transport to use for requests
     * @param jsonFactory A factory for creating JSON parsers and serializers
     * @param baseUrl The base URL of the service. Must end with a "/"
     */
    @Deprecated
    Builder(HttpTransport transport, JsonFactory jsonFactory, GenericUrl baseUrl) {
      super(transport, jsonFactory, baseUrl);
    }

    /**
     * Returns an instance of a new builder.
     *
     * @param transport The transport to use for requests
     * @param jsonFactory A factory for creating JSON parsers and serializers
     * @param httpRequestInitializer The HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(HttpTransport transport, JsonFactory jsonFactory,
        HttpRequestInitializer httpRequestInitializer) {
      super(transport, jsonFactory, DEFAULT_ROOT_URL, DEFAULT_SERVICE_PATH, httpRequestInitializer);
    }

    /** Builds a new instance of {@link Noteendpoint}. */
    @SuppressWarnings("deprecation")
    @Override
    public Noteendpoint build() {
      if (isBaseUrlUsed()) {
        return new Noteendpoint(
            getTransport(),
            getJsonHttpRequestInitializer(),
            getHttpRequestInitializer(),
            getJsonFactory(),
            getObjectParser(),
            getBaseUrl().build(),
            getApplicationName());
      }
      return new Noteendpoint(
          getTransport(),
          getJsonHttpRequestInitializer(),
          getHttpRequestInitializer(),
          getJsonFactory(),
          getObjectParser(),
          getRootUrl(),
          getServicePath(),
          getApplicationName());
    }

    @Override
    @Deprecated
    public Builder setBaseUrl(GenericUrl baseUrl) {
      super.setBaseUrl(baseUrl);
      return this;
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      super.setRootUrl(rootUrl);
      return this;
    }

    @Override
    public Builder setServicePath(String servicePath) {
      super.setServicePath(servicePath);
      return this;
    }

    @Override
    public Builder setJsonHttpRequestInitializer(
        JsonHttpRequestInitializer jsonHttpRequestInitializer) {
      super.setJsonHttpRequestInitializer(jsonHttpRequestInitializer);
      return this;
    }

    @Override
    public Builder setHttpRequestInitializer(HttpRequestInitializer httpRequestInitializer) {
      super.setHttpRequestInitializer(httpRequestInitializer);
      return this;
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      super.setApplicationName(applicationName);
      return this;
    }

    @Override
    public Builder setObjectParser(JsonObjectParser parser) {
      super.setObjectParser(parser);
      return this;
    }
  }
}
