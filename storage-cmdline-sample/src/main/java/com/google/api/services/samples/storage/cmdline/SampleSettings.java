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

package com.google.api.services.samples.storage.cmdline;

import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.Key;

import java.io.IOException;

/** Samples settings JSON Model. */
public final class SampleSettings extends GenericJson {
  
  @Key("project")
  private String project;

  @Key("bucket")
  private String bucket;

  @Key("prefix")
  private String prefix;

  public String getProject() {
    return project;
  }

  public String getBucket() {
    return bucket;
  }

  public String getPrefix() {
    return prefix;
  }

  public static SampleSettings load(JsonFactory jsonFactory) throws IOException {
    try {
      return jsonFactory.fromInputStream(
          StorageSample.class.getResourceAsStream("/sample_settings.json"), SampleSettings.class);
    } catch (IOException e) {
      IOException e2 = new IOException("Unable to read sample_settings.json: " + e.getMessage(), e);
      throw e2;
    }
  }
}

