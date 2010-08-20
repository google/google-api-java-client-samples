/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.api.client.sample.picasa.model;

import com.google.api.client.xml.XmlNamespaceDictionary;

import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Yaniv Inbar
 */
public class Util {
  public static final boolean DEBUG = false;

  public static final XmlNamespaceDictionary NAMESPACE_DICTIONARY =
      new XmlNamespaceDictionary();
  static {
    Map<String, String> map = NAMESPACE_DICTIONARY.namespaceAliasToUriMap;
    map.put("", "http://www.w3.org/2005/Atom");
    map.put("atom", "http://www.w3.org/2005/Atom");
    map.put("exif", "http://schemas.google.com/photos/exif/2007");
    map.put("gd", "http://schemas.google.com/g/2005");
    map.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
    map.put("georss", "http://www.georss.org/georss");
    map.put("gml", "http://www.opengis.net/gml");
    map.put("gphoto", "http://schemas.google.com/photos/2007");
    map.put("media", "http://search.yahoo.com/mrss/");
    map.put("openSearch", "http://a9.com/-/spec/opensearch/1.1/");
    map.put("xml", "http://www.w3.org/XML/1998/namespace");
  }

  public static void enableLogging() {
    if (DEBUG) {
      Logger logger = Logger.getLogger("com.google.api.client");
      logger.setLevel(Level.CONFIG);
      logger.addHandler(new Handler() {

        @Override
        public void close() throws SecurityException {
        }

        @Override
        public void flush() {
        }

        @Override
        public void publish(LogRecord record) {
          // default ConsoleHandler will take care of >= INFO
          if (record.getLevel().intValue() < Level.INFO.intValue()) {
            System.out.println(record.getMessage());
          }
        }
      });
    }
  }
}
