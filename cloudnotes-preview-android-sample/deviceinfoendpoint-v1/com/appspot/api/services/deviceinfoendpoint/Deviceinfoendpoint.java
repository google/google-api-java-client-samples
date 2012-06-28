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
 *  on 2012-06-28 at 06:41:20 UTC 
 */

package com.appspot.api.services.deviceinfoendpoint;


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
 * Service definition for Deviceinfoendpoint (v1).
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
  public class DeviceinfoendpointRequestInitializer implements JsonHttpRequestInitializer {
      public void initialize(JsonHttpRequest request) {
        DeviceinfoendpointRequest deviceinfoendpointRequest = (DeviceinfoendpointRequest)request;
        deviceinfoendpointRequest.setPrettyPrint(true);
        deviceinfoendpointRequest.setKey(ClientCredentials.KEY);
    }
  }
 * </pre>
 *
 * @since 1.3.0
 * @author Google, Inc.
 */
public class Deviceinfoendpoint extends GoogleClient {

  /**
   * The default encoded base path of the service. This is determined when the library is generated
   * and normally should not be changed.
   * @deprecated (scheduled to be removed in 1.8) Use "/" + {@link #DEFAULT_SERVICE_PATH}.
   */
  @Deprecated
  public static final String DEFAULT_BASE_PATH = "/_ah/api/deviceinfoendpoint/v1/";

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
  public static final String DEFAULT_SERVICE_PATH = "deviceinfoendpoint/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Construct a Deviceinfoendpoint instance to connect to the Deviceinfoendpoint service.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport The transport to use for requests
   * @param jsonFactory A factory for creating JSON parsers and serializers
   * @deprecated (scheduled to be removed in 1.8) Use
   *             {@link #Deviceinfoendpoint(HttpTransport, JsonFactory, HttpRequestInitializer)}.
   */
  @Deprecated
  public Deviceinfoendpoint(HttpTransport transport, JsonFactory jsonFactory) {
    super(transport, jsonFactory, DEFAULT_BASE_URL);
  }

  /**
   * Construct a Deviceinfoendpoint instance to connect to the Deviceinfoendpoint service.
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
  public Deviceinfoendpoint(HttpTransport transport, JsonFactory jsonFactory,
      HttpRequestInitializer httpRequestInitializer) {
    super(transport, jsonFactory, DEFAULT_ROOT_URL, DEFAULT_SERVICE_PATH, httpRequestInitializer);
  }

  /**
   * Construct a Deviceinfoendpoint instance to connect to the Deviceinfoendpoint service.
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
  Deviceinfoendpoint(
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
   * Construct a Deviceinfoendpoint instance to connect to the Deviceinfoendpoint service.
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
  Deviceinfoendpoint(
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
   * Create a request for the method "listDeviceInfo".
   *
   * This request holds the parameters needed by the the deviceinfoendpoint server.  After setting any
   * optional parameters, call the {@link ListDeviceInfo#execute()} method to invoke the remote
   * operation.
   *
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public ListDeviceInfo listDeviceInfo() throws IOException {
    ListDeviceInfo result = new ListDeviceInfo();
    initialize(result);
    return result;
  }


  public class ListDeviceInfo extends DeviceinfoendpointRequest {

    private static final String REST_PATH = "deviceinfo";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    ListDeviceInfo() {
      super(Deviceinfoendpoint.this, HttpMethod.GET, REST_PATH, null);
    }



    /**
     * Sends the "listDeviceInfo" request to the Deviceinfoendpoint server.
     *
     * @return the {@link com.appspot.api.services.deviceinfoendpoint.model.DeviceInfos} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.deviceinfoendpoint.model.DeviceInfos execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.deviceinfoendpoint.model.DeviceInfos result = response.parseAs(
          com.appspot.api.services.deviceinfoendpoint.model.DeviceInfos.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "listDeviceInfo" request to the Deviceinfoendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;DeviceInfos&gt;() {

         public void onSuccess(DeviceInfos content, GoogleHeaders responseHeaders) {
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
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.deviceinfoendpoint.model.DeviceInfos> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.deviceinfoendpoint.model.DeviceInfos.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public ListDeviceInfo setFields(String fields) {
      super.setFields(fields);
      return this;
    }



  }

  /**
   * Create a request for the method "updateDeviceInfo".
   *
   * This request holds the parameters needed by the the deviceinfoendpoint server.  After setting any
   * optional parameters, call the {@link UpdateDeviceInfo#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo}
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public UpdateDeviceInfo updateDeviceInfo(com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo content) throws IOException {
    UpdateDeviceInfo result = new UpdateDeviceInfo(content);
    initialize(result);
    return result;
  }


  public class UpdateDeviceInfo extends DeviceinfoendpointRequest {

    private static final String REST_PATH = "deviceinfo";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    UpdateDeviceInfo(com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo content) {
      super(Deviceinfoendpoint.this, HttpMethod.PUT, REST_PATH, content);
      Preconditions.checkNotNull(content);
    }



    /**
     * Sends the "updateDeviceInfo" request to the Deviceinfoendpoint server.
     *
     * @return the {@link com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo result = response.parseAs(
          com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "updateDeviceInfo" request to the Deviceinfoendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;DeviceInfo&gt;() {

         public void onSuccess(DeviceInfo content, GoogleHeaders responseHeaders) {
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
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public UpdateDeviceInfo setFields(String fields) {
      super.setFields(fields);
      return this;
    }



  }

  /**
   * Create a request for the method "insertDeviceInfo".
   *
   * This request holds the parameters needed by the the deviceinfoendpoint server.  After setting any
   * optional parameters, call the {@link InsertDeviceInfo#execute()} method to invoke the remote
   * operation.
   *
   * @param content the {@link com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo}
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public InsertDeviceInfo insertDeviceInfo(com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo content) throws IOException {
    InsertDeviceInfo result = new InsertDeviceInfo(content);
    initialize(result);
    return result;
  }


  public class InsertDeviceInfo extends DeviceinfoendpointRequest {

    private static final String REST_PATH = "deviceinfo";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    InsertDeviceInfo(com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo content) {
      super(Deviceinfoendpoint.this, HttpMethod.POST, REST_PATH, content);
      Preconditions.checkNotNull(content);
    }



    /**
     * Sends the "insertDeviceInfo" request to the Deviceinfoendpoint server.
     *
     * @return the {@link com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo result = response.parseAs(
          com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "insertDeviceInfo" request to the Deviceinfoendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;DeviceInfo&gt;() {

         public void onSuccess(DeviceInfo content, GoogleHeaders responseHeaders) {
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
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public InsertDeviceInfo setFields(String fields) {
      super.setFields(fields);
      return this;
    }



  }

  /**
   * Create a request for the method "removeDeviceInfo".
   *
   * This request holds the parameters needed by the the deviceinfoendpoint server.  After setting any
   * optional parameters, call the {@link RemoveDeviceInfo#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public RemoveDeviceInfo removeDeviceInfo(String id) throws IOException {
    RemoveDeviceInfo result = new RemoveDeviceInfo(id);
    initialize(result);
    return result;
  }


  public class RemoveDeviceInfo extends DeviceinfoendpointRequest {

    private static final String REST_PATH = "deviceinfo/{id}";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    RemoveDeviceInfo(String id) {
      super(Deviceinfoendpoint.this, HttpMethod.DELETE, REST_PATH, null);
      this.id = Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }



    /**
     * Sends the "removeDeviceInfo" request to the Deviceinfoendpoint server.
     *
     * @return the {@link com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo result = response.parseAs(
          com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "removeDeviceInfo" request to the Deviceinfoendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;DeviceInfo&gt;() {

         public void onSuccess(DeviceInfo content, GoogleHeaders responseHeaders) {
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
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public RemoveDeviceInfo setFields(String fields) {
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


    public RemoveDeviceInfo setId(String id) {
      this.id = id;
      return this;
    }



  }

  /**
   * Create a request for the method "getDeviceInfo".
   *
   * This request holds the parameters needed by the the deviceinfoendpoint server.  After setting any
   * optional parameters, call the {@link GetDeviceInfo#execute()} method to invoke the remote
   * operation.
   *
   * @param id
   * @return the request
   * @throws IOException if the initialization of the request fails
   */
  public GetDeviceInfo getDeviceInfo(String id) throws IOException {
    GetDeviceInfo result = new GetDeviceInfo(id);
    initialize(result);
    return result;
  }


  public class GetDeviceInfo extends DeviceinfoendpointRequest {

    private static final String REST_PATH = "deviceinfo/{id}";

    /**
     * Internal constructor.  Use the convenience method instead.
     */
    GetDeviceInfo(String id) {
      super(Deviceinfoendpoint.this, HttpMethod.GET, REST_PATH, null);
      this.id = Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }



    /**
     * Sends the "getDeviceInfo" request to the Deviceinfoendpoint server.
     *
     * @return the {@link com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo} response
     * @throws IOException if the request fails
     */
    public com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo execute() throws IOException {
      HttpResponse response = executeUnparsed();
      com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo result = response.parseAs(
          com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo.class);
      result.setResponseHeaders(response.getHeaders());
      return result;
    }

    /**
     * Queues the "getDeviceInfo" request to the Deviceinfoendpoint server into the given batch request.
     *
     * <p>
     * Example usage:
     * </p>
     *
     * <pre>
       request.queue(batchRequest, new JsonBatchCallback&lt;DeviceInfo&gt;() {

         public void onSuccess(DeviceInfo content, GoogleHeaders responseHeaders) {
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
        com.google.api.client.googleapis.batch.json.JsonBatchCallback<com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo> callback)
        throws IOException {
      batch.queue(buildHttpRequest(), com.appspot.api.services.deviceinfoendpoint.model.DeviceInfo.class,
          com.google.api.client.googleapis.json.GoogleJsonErrorContainer.class, callback);
    }

    /**
     * @since 1.7
     */
    @Override
    public GetDeviceInfo setFields(String fields) {
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


    public GetDeviceInfo setId(String id) {
      this.id = id;
      return this;
    }



  }


  /**
   * Builder for {@link Deviceinfoendpoint}.
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

    /** Builds a new instance of {@link Deviceinfoendpoint}. */
    @SuppressWarnings("deprecation")
    @Override
    public Deviceinfoendpoint build() {
      if (isBaseUrlUsed()) {
        return new Deviceinfoendpoint(
            getTransport(),
            getJsonHttpRequestInitializer(),
            getHttpRequestInitializer(),
            getJsonFactory(),
            getObjectParser(),
            getBaseUrl().build(),
            getApplicationName());
      }
      return new Deviceinfoendpoint(
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
