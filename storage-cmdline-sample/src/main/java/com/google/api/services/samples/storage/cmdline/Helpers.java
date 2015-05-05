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

import java.io.InputStream;
import java.util.Random;


/**
 * Support classes for the command-line sample.
 */
public class Helpers {

  /**
   * Generates a random data block and repeats it to provide the stream.
   *
   * <p>Using a buffer instead of just filling from java.util.Random because the latter causes
   * noticeable lag in stream reading, which detracts from upload speed. This class takes all that
   * cost in the constructor.
   */
  public static class RandomDataBlockInputStream extends InputStream {

    private long byteCountRemaining;
    private final byte[] buffer;

    public RandomDataBlockInputStream(long size, int blockSize) {
      byteCountRemaining = size;
      final Random random = new Random();
      buffer = new byte[blockSize];
      random.nextBytes(buffer);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() {
      throw new AssertionError("Not implemented; too slow.");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read(byte [], int, int)
     */
    @Override
    public int read(byte b[], int off, int len) {
      if (b == null) {
        throw new NullPointerException();
      } else if (off < 0 || len < 0 || len > b.length - off) {
        throw new IndexOutOfBoundsException();
      } else if (len == 0) {
        return 0;
      } else if (byteCountRemaining == 0) {
        return -1;
      }
      int actualLen = len > byteCountRemaining ? (int) byteCountRemaining : len;
      for (int i = off; i < actualLen; i++) {
        b[i] = buffer[i % buffer.length];
      }
      byteCountRemaining -= actualLen;
      return actualLen;
    }
  }
  
}
