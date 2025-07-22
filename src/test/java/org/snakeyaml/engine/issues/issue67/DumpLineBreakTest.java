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
package org.snakeyaml.engine.issues.issue67;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.common.ScalarStyle;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Issue 67: ScannerException on block scalar "\n"
 */
@org.junit.jupiter.api.Tag("fast")
public class DumpLineBreakTest {

  @Test
  @DisplayName("Dump default scalar style")
  void dumpDefaultScalaStyle() {
    DumpSettings dumpSettings = DumpSettings.builder().build();
    assertEquals(ScalarStyle.PLAIN, dumpSettings.getDefaultScalarStyle());
  }

  @Test
  @DisplayName("Dump literal scalar style")
  void dumpLiteralScalaStyle() {
    DumpSettings dumpSettings =
        DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.LITERAL).build();
    Dump dump = new Dump(dumpSettings);
    assertEquals("|2+\n\n", dump.dumpToString("\n"));
    assertEquals("\"\"\n", dump.dumpToString(""));
    assertEquals("\" \"\n", dump.dumpToString(" "));
  }

  @Test
  @DisplayName("Dump JSON scalar style")
  void dumpJSONScalaStyle() {
    DumpSettings dumpSettings =
        DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.JSON_SCALAR_STYLE).build();
    Dump dump = new Dump(dumpSettings);
    assertEquals("\"\\n\"\n", dump.dumpToString("\n"));
    assertEquals("\"\"\n", dump.dumpToString(""));
    assertEquals("\" \"\n", dump.dumpToString(" "));
  }

  @Test
  @DisplayName("Dump PLAIN scalar style")
  void dumpPlainScalaStyle() {
    DumpSettings dumpSettings =
        DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.PLAIN).build();
    Dump dump = new Dump(dumpSettings);
    assertEquals("|2+\n\n", dump.dumpToString("\n"));
    assertEquals("''\n", dump.dumpToString(""));
    assertEquals("' '\n", dump.dumpToString(" "));
  }

  @Test
  @DisplayName("Dump FOLDED scalar style")
  void dumpFoldedScalaStyle() {
    DumpSettings dumpSettings =
        DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.FOLDED).build();
    Dump dump = new Dump(dumpSettings);
    assertEquals(">2+\n\n", dump.dumpToString("\n"));
    assertEquals("\"\"\n", dump.dumpToString(""));
    assertEquals("\" \"\n", dump.dumpToString(" "));
  }

  @Test
  @DisplayName("Dump SINGLE_QUOTED scalar style")
  void dumpSINGLE_QUOTEDScalaStyle() {
    DumpSettings dumpSettings =
        DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.SINGLE_QUOTED).build();
    Dump dump = new Dump(dumpSettings);
    assertEquals("'\n\n  '\n", dump.dumpToString("\n"));
    assertEquals("''\n", dump.dumpToString(""));
    assertEquals("' '\n", dump.dumpToString(" "));
  }

  @Test
  @DisplayName("Dump DOUBLE_QUOTED scalar style")
  void dumpDOUBLE_QUOTEDScalaStyle() {
    DumpSettings dumpSettings =
        DumpSettings.builder().setDefaultScalarStyle(ScalarStyle.DOUBLE_QUOTED).build();
    Dump dump = new Dump(dumpSettings);
    assertEquals("\"\\n\"\n", dump.dumpToString("\n"));
    assertEquals("\"\"\n", dump.dumpToString(""));
    assertEquals("\" \"\n", dump.dumpToString(" "));
  }
}
