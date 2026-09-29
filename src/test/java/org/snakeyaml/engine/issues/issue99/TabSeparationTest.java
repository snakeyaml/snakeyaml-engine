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
package org.snakeyaml.engine.issues.issue99;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

/**
 * Issue 99: YAML 1.2 forbids TAB in indentation only. TAB is legal separation whitespace between
 * tokens on a line, also in block context. It still may not be used where it would act as
 * indentation, i.e. in front of a block collection indicator or an implicit key which starts a new
 * block mapping (see Y79Y in the YAML test suite).
 */
@Tag("fast")
class TabSeparationTest {

  private Object load(String yaml) {
    return new Load(LoadSettings.builder().build()).loadFromString(yaml);
  }

  private void assertFails(String yaml) {
    YamlEngineException e = assertThrows(YamlEngineException.class, () -> load(yaml));
    assertTrue(e.getMessage().contains("TAB") || e.getMessage().contains("\\t"),
        "Error must mention TAB: " + e.getMessage());
  }

  @Test
  @DisplayName("Issue 99: TAB after the ':' value indicator")
  void tabAfterValueIndicator() {
    assertEquals(Map.of("a", "value"), load("a:\tvalue"));
    assertEquals(Map.of("a", "value"), load("a: \tvalue"));
    assertEquals(Map.of("a", "value"), load("a:\t \tvalue"));
    assertEquals(Map.of("a", Map.of("b", "c")), load("a:\n  b:\tc"));
  }

  @Test
  @DisplayName("Issue 99: TAB after the '-' block entry indicator")
  void tabAfterBlockEntryIndicator() {
    assertEquals(List.of("x"), load("-\tx"));
    assertEquals(List.of("x"), load("- \tx"));
    assertEquals(List.of("x", "y"), load("-\tx\n-\ty"));
  }

  @Test
  @DisplayName("Issue 99: TAB after explicit key and value indicators")
  void tabAfterExplicitIndicators() {
    assertEquals(Map.of("a", "b"), load("? a\n:\tb"));
    assertEquals(Map.of("a", "b"), load("?\ta\n:\tb"));
  }

  @Test
  @DisplayName("Issue 99: TAB before flow collections and quoted scalars")
  void tabBeforeFlowAndQuoted() {
    assertEquals(Map.of("a", List.of(1, 2)), load("a:\t[1, 2]"));
    assertEquals(Map.of("a", Map.of("b", 1)), load("a:\t{b: 1}"));
    assertEquals(Map.of("a", "q"), load("a:\t\"q\""));
    assertEquals(Map.of("a", "q"), load("a:\t'q'"));
    assertEquals(Map.of("a", "b"), load("\"a\"\t: b"));
  }

  @Test
  @DisplayName("Issue 99: TAB after the document start marker")
  void tabAfterDocumentStart() {
    assertEquals("x", load("---\tx"));
    assertEquals("x", load("--- \tx"));
  }

  @Test
  @DisplayName("Issue 99: TAB after anchors and tags")
  void tabAfterProperties() {
    assertEquals(Map.of("a", 1), load("a: &x\t1"));
    assertEquals(Map.of("a", "1"), load("a: !!str\t1"));
    assertEquals(Map.of("a", "1"), load("a: !!str\t&x\t1"));
  }

  @Test
  @DisplayName("Issue 99: TAB inside directives")
  void tabInDirectives() {
    assertEquals("a", load("%YAML\t1.2\t# c\n---\ta"));
    assertEquals("a", load("%TAG\t!e!\ttag:yaml.org,2002:\n--- !e!str\ta"));
  }

  @Test
  @DisplayName("Issue 99: TAB around block scalar headers")
  void tabAroundBlockScalarHeader() {
    assertEquals(Map.of("a", "x\n"), load("a:\t|\n  x\n"));
    assertEquals(Map.of("a", "x\n"), load("a: |\t# c\n  x\n"));
    assertEquals(Map.of("a", "x\n"), load("a: |\t\n  x\n"));
  }

  @Test
  @DisplayName("Issue 99: TAB before a plain scalar that only looks like an indicator")
  void tabBeforeIndicatorLikePlainScalar() {
    assertEquals(List.of(-1), load("-\t-1"));
    assertEquals(Map.of("a", List.of("b")), load("? a\n: -\tb"));
  }

  @Test
  @DisplayName("Issue 99: TAB and space mixed in flow context")
  void tabAndSpaceMixedInFlowContext() {
    assertEquals(Map.of("a", Map.of("b", "c")), load("a: {b:\t c}\n"));
    assertEquals(Map.of("a", List.of(1, 2)), load("a: [1,\t 2]"));
    assertEquals(Map.of("a", List.of(1, 2)), load("a: [1, \t \t2]"));
    assertEquals(Map.of("a", 1), load("{\n\t \"a\": 1\n}"));
  }

  @Test
  @DisplayName("Issue 99: TAB may not precede a block collection indicator")
  void tabBeforeBlockIndicatorFails() {
    assertFails("-\t-");
    assertFails("- \t-");
    assertFails("?\t-");
    assertFails("? -\n:\t-");
    // invalid also with a space instead of TAB
    assertThrows(YamlEngineException.class, () -> load("a:\t- x"));
  }

  @Test
  @DisplayName("Issue 99: TAB may not precede an implicit key which starts a new block mapping")
  void tabBeforeNewBlockMappingFails() {
    assertFails("?\tkey:");
    assertFails("? key:\n:\tkey:");
    assertFails("-\ta: b");
  }

  @Test
  @DisplayName("Issue 99: TAB as indentation is still rejected")
  void tabAsIndentationFails() {
    assertFails("a:\n\tb: c");
    assertFails("foo:\n  a: 1\n  \tb: 2");
  }
}
