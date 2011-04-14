// Copyright 2010 Google Inc. All Rights Reserved.

package com.google.api.client.sample.verification.model;

import com.google.api.client.util.Key;

/**
 * @author Kevin Marshall
 *
 */
public class TokenRequest {  
  @Key
  public String verificationMethod;
  
  @Key
  public WebResourceSite site = new WebResourceSite();
}
