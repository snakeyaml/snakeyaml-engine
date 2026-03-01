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
package org.snakeyaml.engine.v2.common;

import java.util.Arrays;
import java.util.Map;

import static java.util.Map.entry;

public final class CharConstants {

  private static final String ALPHA_S =
      "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-_";

  private static final String LINEBR_S = "\n";
  private final static String FULL_LINEBR_S = "\r" + LINEBR_S;
  private static final String NULL_OR_LINEBR_S = "\0" + FULL_LINEBR_S;
  private static final String NULL_BL_LINEBR_S = " " + NULL_OR_LINEBR_S;
  private static final String NULL_BL_T_LINEBR_S = "\t" + NULL_BL_LINEBR_S;
  private static final String NULL_BL_T_S = "\0 \t";

  // the suffix must not contain the “[”, “]”, “{”, “}” and “,” characters.
  // These characters would cause ambiguity with flow collection structures.
  // https://yaml.org/spec/1.2.2/#691-node-tags
  private static final String URI_CHARS_SUFFIX_S = ALPHA_S + "-;/?:@&=+$_.!~*'()%";

  public static final CharConstants LINEBR = new CharConstants(LINEBR_S);
  public static final CharConstants NULL_OR_LINEBR = new CharConstants(NULL_OR_LINEBR_S);
  public static final CharConstants NULL_BL_LINEBR = new CharConstants(NULL_BL_LINEBR_S);
  public static final CharConstants NULL_BL_T_LINEBR = new CharConstants(NULL_BL_T_LINEBR_S);
  public static final CharConstants NULL_BL_T = new CharConstants(NULL_BL_T_S);
  // prefix may contain ,[]
  public static final CharConstants URI_CHARS_FOR_TAG_PREFIX =
      new CharConstants(URI_CHARS_SUFFIX_S + ",[]");
  public static final CharConstants URI_CHARS_FOR_TAG_SUFFIX =
      new CharConstants(URI_CHARS_SUFFIX_S);

  public static final CharConstants ALPHA = new CharConstants(ALPHA_S);

  private static final int ASCII_SIZE = 128;
  boolean[] contains = new boolean[ASCII_SIZE];

  private CharConstants(String content) {
    Arrays.fill(contains, false);
    for (int i = 0; i < content.length(); i++) {
      int c = content.codePointAt(i);
      contains[c] = true;
    }
  }

  public boolean has(int c) {
    return (c < ASCII_SIZE) && contains[c];
  }

  public boolean hasNo(int c) {
    return !has(c);
  }

  public boolean has(int c, String additional) {
    return has(c) || additional.indexOf(c) != -1;
  }

  public boolean hasNo(int c, String additional) {
    return !has(c, additional);
  }

  /**
   * A mapping from an escaped character in the input stream to the character that they should be
   * replaced with.
   * <p>
   * YAML defines several common and a few uncommon escape sequences.
   */
  public static final Map<Character, String> ESCAPE_REPLACEMENTS;

  /**
   * A mapping from a character to a number of bytes to read-ahead for that escape sequence. These
   * escape sequences are used to handle unicode escaping in the following formats, where H is a
   * hexadecimal character:
   *
   * <pre>
   * &#92;xHH         : escaped 8-bit Unicode character
   * &#92;uHHHH       : escaped 16-bit Unicode character
   * &#92;UHHHHHHHH   : escaped 32-bit Unicode character
   * </pre>
   */
  public static final Map<Character, Integer> ESCAPE_CODES;

  static {

    ESCAPE_REPLACEMENTS = Map.ofEntries(entry('0', "\0"), // ASCII null
        entry('a', "\u0007"), // ASCII bell
        entry('b', "\u0008"), // ASCII backspace
        entry('t', "\u0009"), // ASCII horizontal tab
        entry('n', "\n"), // ASCII newline (line feed; &#92;n maps to 0x0A)
        entry('v', "\u000B"), // ASCII vertical tab
        entry('f', "\u000C"), // ASCII form-feed
        entry('r', "\r"), // carriage-return (&#92;r maps to 0x0D)
        entry('e', "\u001B"), // ASCII escape character (Esc)
        entry(' ', "\u0020"), // ASCII space
        entry('"', "\""), // ASCII double-quote
        entry('/', "/"), // ASCII slash, for JSON compatibility.
        entry('\\', "\\"), // ASCII backslash
        entry('N', "\u0085"), // Unicode next line
        entry('_', "\u00A0") // Unicode non-breaking-space
    );

    ESCAPE_CODES = Map.of('x', 2, // 8-bit Unicode
        'u', 4, // 16-bit Unicode
        'U', 8 // 32-bit Unicode (Supplementary characters are supported)
    );
  }

  /**
   * Replace a single character with its string representation
   *
   * @param chRepresentation - the char to escape
   * @return the same string or its escaped representation
   */
  public static String escapeChar(String chRepresentation) {
    for (Character s : ESCAPE_REPLACEMENTS.keySet()) {
      String v = ESCAPE_REPLACEMENTS.get(s);
      if (" ".equals(v) || "/".equals(v) || "\"".equals(v)) {
        continue;
      }
      if (v.equals(chRepresentation)) {
        return "\\" + s; // '<TAB>' -> '\t'
      }
    }
    return chRepresentation;
  }
}
