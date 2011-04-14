// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.api.client.sample.verification.model;

import com.google.api.client.util.Key;

/**
 * @author Kevin Marshall
 *
 */
public class TokenResponse {
  @Key
  public String method;
  
  @Key
  public String token;
}
