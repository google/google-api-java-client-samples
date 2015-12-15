# prediction-cmdline-sample

## Instructions for the Prediction V1.6 Command-Line Sample

### Browse online

-   [Browse Source]
    (https://github.com/google/google-api-java-client-samples/tree/master/prediction-cmdline-sample),
    or [main file]
    (https://github.com/google/google-api-java-client-samples/blob/master/prediction-cmdline-sample/src/main/java/com/google/api/services/samples/prediction/cmdline/PredictionSample.java).

### Register your application

-   Enable the Prediction API in the [Google Developers Console]
    (https://console.developers.google.com/projectselector/apis/api/prediction/overview).
-   Create a service account from the [Permissions]
    (https://console.developers.google.com/permissions/serviceaccounts) page.
    When you create the service account, select **Furnish a new private key**
    and download the service account's private key in P12 format. Later on, after
    you check out the sample project, you will copy this downloaded file (e.g.
    `MyProject-123456.p12`) to the `src/main/resources/` directory, and then
    edit `PROJECT_ID`, `SERVICE_ACCT_EMAIL`, and `SERVICE_ACCT_KEYFILE` in
    `PredictionSample.java`.
-   [Activate Google Storage]
    (http://code.google.com/apis/storage/docs/signup.html), upload the [training
    data](http://code.google.com/apis/predict/docs/language_id.txt) required by
    the sample to Google Storage, and then edit `OBJECT_PATH` in
    `PredictionSample.java` to point to the training data. Otherwise, you will
    get a 400 error "Training data file not found".

### Check out and run the sample

**Prerequisites:** install [Java](http://java.com), [Git](https://git-scm.com/),
and [Maven](http://maven.apache.org/download.html). You might need to set your
`JAVA_HOME`.

    cd [someDirectory]
    git clone https://github.com/google/google-api-java-client-samples.git
    cd google-api-java-client-samples/prediction-cmdline-sample
    cp ~/Downloads/MyProject-12345abcd.p12 src/main/resources/
    [editor] src/main/java/com/google/api/services/samples/prediction/cmdline/PredictionSample.java
    mvn compile
    mvn -q exec:java

To enable logging of HTTP requests and responses (highly recommended when
developing), take a look at [`logging.properties`](logging.properties).
