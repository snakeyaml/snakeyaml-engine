/*
 * Copyright (c) 2018, SnakeYAML
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
package org.snakeyaml.engine.v2.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

/**
 * Provide an implementation of StreamDataWriter interface which does not throw {@link IOException}
 * but wraps those into {@link UncheckedIOException}s.
 */
public class YamlOutputStreamWriter extends OutputStreamWriter implements StreamDataWriter {

  /**
   * Create
   *
   * @param out - the output
   * @param cs - encoding to use to translate String to bytes
   */
  public YamlOutputStreamWriter(OutputStream out, Charset cs) {
    super(out, cs);
  }

  /**
   * Default implementation wraps the given {@code IOException} into an
   * {@link UncheckedIOException}.
   *
   * @param e - the reason
   */
  public void processIOException(IOException e) {
    throw new UncheckedIOException(e);
  }

  @Override
  public void flush() {
    try {
      super.flush();
    } catch (IOException e) {
      processIOException(e);
    }
  }

  @Override
  public void write(String str, int off, int len) {
    try {
      super.write(str, off, len);
    } catch (IOException e) {
      processIOException(e);
    }
  }

  @Override
  public void write(String str) {
    try {
      super.write(str);
    } catch (IOException e) {
      processIOException(e);
    }
  }
}
