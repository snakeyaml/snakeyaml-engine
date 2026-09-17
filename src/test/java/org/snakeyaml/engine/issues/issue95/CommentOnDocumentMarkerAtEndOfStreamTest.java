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
package org.snakeyaml.engine.issues.issue95;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

/**
 * Issue 95: an in-line comment on a document marker ("---" or "...") with no following document
 * must not throw an IndexOutOfBoundsException when loading all documents in a stream.
 */
@org.junit.jupiter.api.Tag("fast")
class CommentOnDocumentMarkerAtEndOfStreamTest {

  private final Load load = new Load(LoadSettings.builder().setParseComments(true).build());

  private List<Object> loadAll(String yaml) {
    List<Object> docs = new ArrayList<>();
    load.loadAllFromString(yaml).forEach(docs::add);
    return docs;
  }

  @Test
  @DisplayName("Issue 95: in-line comment on the document start marker, no next document")
  void commentOnDocumentStartMarkerNoNextDocument() {
    assertEquals(List.of("foo"), loadAll("--- foo ###\n"));
  }

  @Test
  @DisplayName("Issue 95: in-line comment on the document end marker, no next document")
  void commentOnDocumentEndMarkerNoNextDocument() {
    LinkedHashMap<String, Integer> expected = new LinkedHashMap<>();
    expected.put("foo", 1);
    assertEquals(List.of(expected), loadAll("foo: 1\n... ###\n"));
  }

  @Test
  @DisplayName("Issue 95: in-line comment on the document end marker, CRLF line endings")
  void commentOnDocumentEndMarkerCrlf() {
    LinkedHashMap<String, Integer> expected = new LinkedHashMap<>();
    expected.put("foo", 1);
    assertEquals(List.of(expected), loadAll("foo: 1\r\n... ###\r\n"));
  }

  @Test
  @DisplayName("Issue 95: in-line comment on the document start marker, CRLF line endings")
  void commentOnDocumentStartMarkerCrlf() {
    assertEquals(List.of("foo"), loadAll("--- foo ###\r\n"));
  }

  @Test
  @DisplayName("Issue 95: in-line comment on the document start marker, quoted scalar")
  void commentOnDocumentStartMarkerQuotedScalar() {
    assertEquals(List.of("foo"), loadAll("--- \"foo\" ###\n"));
  }

  @Test
  @DisplayName("Issue 95: in-line comment on the document start marker, tagged scalar")
  void commentOnDocumentStartMarkerTaggedScalar() {
    assertEquals(List.of("1"), loadAll("--- !!str 1 ###\n"));
  }

  @Test
  @DisplayName("Issue 95: in-line comment on the document start marker, anchored scalar")
  void commentOnDocumentStartMarkerAnchoredScalar() {
    assertEquals(List.of("foo"), loadAll("--- &a foo ###\n"));
  }

  @Test
  @DisplayName("Issue 95: single-document API is unaffected")
  void singleDocumentApiStillWorks() {
    assertEquals("foo", load.loadFromString("--- foo ###\n"));
    LinkedHashMap<String, Integer> expected = new LinkedHashMap<>();
    expected.put("foo", 1);
    assertEquals(expected, load.loadFromString("foo: 1\n... ###\n"));
  }
}
