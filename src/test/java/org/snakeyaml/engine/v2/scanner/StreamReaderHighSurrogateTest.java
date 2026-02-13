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
package org.snakeyaml.engine.v2.scanner;

import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.LoadSettings;

import static org.junit.jupiter.api.Assertions.*;

@org.junit.jupiter.api.Tag("fast")
public class StreamReaderHighSurrogateTest {

  /**
   * Test for IndexOutOfBoundsException when buffer is exactly filled with 1025 characters and the
   * last character is a high surrogate. This reproduces the bug where buffer[1025] is accessed on a
   * buffer with length 1025.
   */
  @Test
  public void testHighSurrogateAtBufferBoundary() {
    // Create a string that is exactly 1024 regular chars + 1 high surrogate
    // The buffer size is 1024, so buffer is created with size 1025
    // When we read 1025 chars and the last is high surrogate, it tries to read buffer[1025]
    StringBuilder sb = new StringBuilder();

    // Fill with 1024 regular ASCII characters
    for (int i = 0; i < 1024; i++) {
      sb.append('a');
    }

    // Add a high surrogate as the 1025th character
    // This should be followed by a low surrogate to form a valid surrogate pair
    sb.append('\uD800'); // High surrogate
    sb.append('\uDC00'); // Low surrogate to make it valid

    // This should not throw IndexOutOfBoundsException
    LoadSettings settings = LoadSettings.builder().setBufferSize(1024).build();
    StreamReader reader = new StreamReader(settings, sb.toString());

    // Read all characters - this should trigger the bug before the fix
    int count = 0;
    while (reader.peek() != '\0') {
      reader.forward(1);
      count++;
    }

    // We should successfully read all 1025 characters (1024 regular + 1 surrogate pair)
    assertEquals(1025, count);
  }

  /**
   * Test with multiple buffer fills where the boundary character is a high surrogate
   */
  @Test
  public void testHighSurrogateAtMultipleBufferBoundaries() {
    LoadSettings settings = LoadSettings.builder().setBufferSize(10).build();

    // Create a string where the 10th character is a high surrogate
    // followed by a low surrogate, then more regular characters
    String input = "123456789\uD800\uDC00abcdefghij\uD801\uDC01xyz";

    StreamReader reader = new StreamReader(settings, input);

    int count = 0;
    while (reader.peek() != '\0') {
      reader.forward(1);
      count++;
    }

    // Count should match the number of code points
    // 9 regular + 1 surrogate pair + 10 regular + 1 surrogate pair + 3 regular = 24 code points
    assertEquals(24, count);
  }
}
