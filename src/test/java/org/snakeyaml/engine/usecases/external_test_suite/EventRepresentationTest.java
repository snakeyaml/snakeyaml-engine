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
package org.snakeyaml.engine.usecases.external_test_suite;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.common.Anchor;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.events.AliasEvent;
import org.snakeyaml.engine.v2.events.DocumentEndEvent;
import org.snakeyaml.engine.v2.events.DocumentStartEvent;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.events.ImplicitTuple;
import org.snakeyaml.engine.v2.events.MappingStartEvent;
import org.snakeyaml.engine.v2.events.ScalarEvent;
import org.snakeyaml.engine.v2.events.SequenceEndEvent;
import org.snakeyaml.engine.v2.events.SequenceStartEvent;
import org.snakeyaml.engine.v2.events.StreamEndEvent;
import org.snakeyaml.engine.v2.events.StreamStartEvent;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@org.junit.jupiter.api.Tag("fast")
class EventRepresentationTest {

  @Test
  @DisplayName("Represent StreamStartEvent")
  void testStreamStartEvent() {
    StreamStartEvent event = new StreamStartEvent();
    EventRepresentation representation = new EventRepresentation(event);
    assertTrue(representation.isSameAs("+STR"));
    assertFalse(representation.isSameAs("-STR"));
    assertFalse(representation.isSameAs("=VAL"));
  }

  @Test
  @DisplayName("Represent StreamEndEvent")
  void testStreamEndEvent() {
    StreamEndEvent event = new StreamEndEvent();
    EventRepresentation representation = new EventRepresentation(event);
    assertTrue(representation.isSameAs("-STR"));
    assertFalse(representation.isSameAs("+STR"));
  }

  @Test
  @DisplayName("Represent AliasEvent")
  void testAliasEvent() {
    AliasEvent event = new AliasEvent(Optional.of(new Anchor("a")));
    EventRepresentation representation = new EventRepresentation(event);
    assertTrue(representation.isSameAs("=ALI *a"));
    assertTrue(representation.isSameAs("=ALI *b"));
    assertTrue(representation.isSameAs("=ALI *002"));
    assertFalse(representation.isSameAs("=ALI &002"));
    assertFalse(representation.isSameAs("+STR"));
  }

  @Test
  @DisplayName("Represent DocumentStartEvent")
  void testDocumentStartEvent() {
    valid(new DocumentStartEvent(true, Optional.empty(), Collections.emptyMap()), "+DOC ---");
    valid(new DocumentStartEvent(true, Optional.empty(), Collections.emptyMap()), "+DOC");

    valid(new DocumentStartEvent(false, Optional.empty(), Collections.emptyMap()), "+DOC");
    valid(new DocumentStartEvent(false, Optional.empty(), Collections.emptyMap()), "+DOC ---");
  }

  @Test
  @DisplayName("Represent DocumentEndEvent")
  void testDocumentEndEvent() {
    valid(new DocumentEndEvent(true), "-DOC ...");
    valid(new DocumentEndEvent(true), "-DOC");
    invalid(new DocumentEndEvent(true), "+DOC ---");
  }

  @Test
  @DisplayName("Represent SequenceStartEvent")
  void testSequenceStartEvent() {
    valid(new SequenceStartEvent(Optional.of(new Anchor("a")), Optional.of("ttt"), false,
        FlowStyle.FLOW), "+SEQ [] &a <ttt>");
    valid(new SequenceStartEvent(Optional.of(new Anchor("a")), Optional.of("ttt"), false,
        FlowStyle.BLOCK), "+SEQ &a <ttt>");
    invalid(new SequenceStartEvent(Optional.of(new Anchor("a")), Optional.of("ttt"), false,
        FlowStyle.BLOCK), "+SEQ *a <ttt>");
    invalid(new SequenceStartEvent(Optional.of(new Anchor("a")), Optional.of("ttt"), false,
        FlowStyle.BLOCK), "+SEQ &a <t>");
    invalid(new SequenceStartEvent(Optional.of(new Anchor("a")), Optional.of("ttt"), false,
        FlowStyle.BLOCK), "+SEQ <ttt>");
    invalid(new SequenceStartEvent(Optional.of(new Anchor("a")), Optional.of("ttt"), false,
        FlowStyle.BLOCK), "+SEQ *a");
  }

  @Test
  @DisplayName("Represent SequenceEndEvent")
  void testSequenceEndEvent() {
    valid(new SequenceEndEvent(), "-SEQ");
    invalid(new SequenceEndEvent(), "-MAP");
  }

  @Test
  @DisplayName("Represent ScalarEvent")
  void testScalarEvent() {
    valid(new ScalarEvent(Optional.of(new Anchor("a")), Optional.of("ttt"),
        new ImplicitTuple(false, false), "v1", ScalarStyle.FOLDED), "=VAL &a <ttt> >v1");

    invalid(new ScalarEvent(Optional.of(new Anchor("a")), Optional.of("ttt"),
        new ImplicitTuple(false, false), "v1", ScalarStyle.PLAIN), "=VAL <ttt> >v1");
    invalid(new ScalarEvent(Optional.of(new Anchor("a")), Optional.of("ttt"),
        new ImplicitTuple(false, false), "v1", ScalarStyle.PLAIN), "=VAL &a >v1");
    invalid(new ScalarEvent(Optional.of(new Anchor("a")), Optional.of("ttt"),
        new ImplicitTuple(false, false), "v1", ScalarStyle.PLAIN), "=VAL &a <ttt>");
    invalid(new ScalarEvent(Optional.of(new Anchor("a")), Optional.of("ttt"),
        new ImplicitTuple(false, false), "v1", ScalarStyle.PLAIN), "=VAL &a <ttt> |v1");
  }

  @Test
  @DisplayName("Represent MappingStartEvent")
  void testMappingStartEvent() {
    invalid(new MappingStartEvent(Optional.of(new Anchor("a")), Optional.of("ttt"), false,
        FlowStyle.FLOW), "+MAP");
    valid(
        new MappingStartEvent(Optional.empty(),
            Optional.of(org.snakeyaml.engine.v2.nodes.Tag.MAP.getValue()), false, FlowStyle.FLOW),
        "+MAP");
    valid(new MappingStartEvent(Optional.empty(), Optional.empty(), false, FlowStyle.FLOW), "+MAP");
  }

  private void valid(Event event, String expectation) {
    EventRepresentation representation = new EventRepresentation(event);
    assertTrue(representation.isSameAs(expectation));
  }

  private void invalid(Event event, String expectation) {
    EventRepresentation representation = new EventRepresentation(event);
    assertFalse(representation.isSameAs(expectation));
  }
}
