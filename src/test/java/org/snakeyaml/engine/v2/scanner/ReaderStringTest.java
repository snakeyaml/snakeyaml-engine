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
import org.snakeyaml.engine.v2.exceptions.ReaderException;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

@org.junit.jupiter.api.Tag("fast")

public class ReaderStringTest {

  @Test
  public void testCheckPrintable() {
    StreamReader reader = new StreamReader(LoadSettings.builder().build(), "test");
    assertEquals('\0', reader.peek(4));
    assertTrue(StreamReader.isPrintable("test"));
  }

  @Test
  public void testCheckNonPrintable() {
    assertFalse(StreamReader.isPrintable("test\u0005 fail"));
    try {
      StreamReader reader = new StreamReader(LoadSettings.builder().build(), "test\u0005 fail");
      while (reader.peek() != '\0') {
        reader.forward();
      }
      fail("Non printable Unicode code points must not be accepted.");
    } catch (ReaderException e) {
      assertEquals(
          "unacceptable code point '' (0x5) special characters are not allowed\nin \"reader\", position 4",
          e.toString());
    }
  }

  /**
   * test reading all the chars
   */
  @Test
  public void testCheckAll() {
    int counterSurrogates = 0;
    for (char i = 0; i < 256 * 256 - 1; i++) {
      if (Character.isHighSurrogate(i)) {
        counterSurrogates++;
      } else {
        char[] chars = new char[1];
        chars[0] = i;
        String str = new String(chars);
        boolean regularExpressionResult = StreamReader.isPrintable(str);

        boolean charsArrayResult = true;
        try {
          new StreamReader(LoadSettings.builder().build(), new StringReader(str)).peek();
        } catch (Exception e) {
          String error = e.getMessage();
          assertTrue(error.startsWith("unacceptable character")
              || error.equals("special characters are not allowed"), error);
          charsArrayResult = false;
        }
        assertEquals(regularExpressionResult, charsArrayResult, "Failed for #" + i);
      }
    }
    // https://en.wikipedia.org/wiki/Universal_Character_Set_characters
    assertEquals(1024, counterSurrogates, "There are 1024 high surrogates (D800–DBFF)");
  }

  @Test
  public void testHighSurrogateAlone() {
    StreamReader reader = new StreamReader(LoadSettings.builder().build(), "test\uD800");
    try {
      while (reader.peek() > 0) {
        reader.forward(1);
      }
    } catch (ReaderException e) {
      assertTrue(e.toString().contains(
          "(0xD800) The last char is HighSurrogate (no LowSurrogate detected)"), e.toString());
      assertEquals(5, e.getPosition());
    }
  }

  @Test
  public void testForward() {
    StreamReader reader = new StreamReader(LoadSettings.builder().build(), "test");
    while (reader.peek() != '\u0000') {
      reader.forward(1);
    }
    reader = new StreamReader(LoadSettings.builder().build(), "test");
    assertEquals('t', reader.peek());
    reader.forward(1);
    assertEquals('e', reader.peek());
    reader.forward(1);
    assertEquals('s', reader.peek());
    reader.forward(1);
    assertEquals('t', reader.peek());
    reader.forward(1);
    assertEquals('\u0000', reader.peek());
  }

  @Test
  public void testPeekInt() {
    StreamReader reader = new StreamReader(LoadSettings.builder().build(), "test");
    assertEquals('t', reader.peek(0));
    assertEquals('e', reader.peek(1));
    assertEquals('s', reader.peek(2));
    assertEquals('t', reader.peek(3));
    reader.forward(1);
    assertEquals('e', reader.peek(0));
    assertEquals('s', reader.peek(1));
    assertEquals('t', reader.peek(2));
  }
}
