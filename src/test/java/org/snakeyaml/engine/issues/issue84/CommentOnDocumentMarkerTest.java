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
package org.snakeyaml.engine.issues.issue84;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

/**
 * Issue 84: an in-line comment on a document separator ("---" or "...") must not break loading.
 */
@org.junit.jupiter.api.Tag("fast")
class CommentOnDocumentMarkerTest {

  private final Load load = new Load(LoadSettings.builder().setParseComments(true).build());

  @Test
  @DisplayName("Issue 84: comment on the document start marker")
  void commentOnDocumentStartMarker() {
    String yaml = "--- # This comment causes exception\nkey: value";
    Object result = load.loadFromString(yaml);
    LinkedHashMap<String, String> expected = new LinkedHashMap<>();
    expected.put("key", "value");
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Issue 84: document is only a marker with a comment")
  void commentOnlyDocumentStartMarker() {
    String yaml = "--- # just a comment\n";
    Object result = load.loadFromString(yaml);
    assertNull(result);
  }

  @Test
  @DisplayName("Issue 84: comment on the document start marker before a sequence")
  void commentOnDocumentStartMarkerBeforeSequence() {
    String yaml = "--- # Comment\n- a\n- b\n";
    Object result = load.loadFromString(yaml);
    assertEquals(List.of("a", "b"), result);
  }

  @Test
  @DisplayName("Issue 84: comment on the document start marker before a flow mapping")
  void commentOnDocumentStartMarkerBeforeFlowMapping() {
    String yaml = "--- # Comment\n{a: 1}\n";
    Object result = load.loadFromString(yaml);
    LinkedHashMap<String, Integer> expected = new LinkedHashMap<>();
    expected.put("a", 1);
    assertEquals(expected, result);
  }

  @Test
  @DisplayName("Issue 84: comment on the document separator between two documents")
  void commentOnDocumentSeparatorBetweenDocuments() {
    String yaml = "a: 1\n--- # Comment\nb: 2\n";
    List<Object> docs = new ArrayList<>();
    load.loadAllFromString(yaml).forEach(docs::add);
    LinkedHashMap<String, Integer> doc1 = new LinkedHashMap<>();
    doc1.put("a", 1);
    LinkedHashMap<String, Integer> doc2 = new LinkedHashMap<>();
    doc2.put("b", 2);
    assertEquals(List.of(doc1, doc2), docs);
  }

  @Test
  @DisplayName("Issue 84: comment on the document end marker before the next document start")
  void commentOnDocumentEndMarkerBeforeNextDocumentStart() {
    String yaml = "a: 1\n... # end comment\n--- # start comment\nb: 2\n";
    List<Object> docs = new ArrayList<>();
    load.loadAllFromString(yaml).forEach(docs::add);
    LinkedHashMap<String, Integer> doc1 = new LinkedHashMap<>();
    doc1.put("a", 1);
    LinkedHashMap<String, Integer> doc2 = new LinkedHashMap<>();
    doc2.put("b", 2);
    assertEquals(List.of(doc1, doc2), docs);
  }
}
