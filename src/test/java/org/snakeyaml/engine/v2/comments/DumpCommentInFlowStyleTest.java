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
package org.snakeyaml.engine.v2.comments;

import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.api.lowlevel.Parse;
import org.snakeyaml.engine.v2.api.lowlevel.Present;
import org.snakeyaml.engine.v2.api.lowlevel.Serialize;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;

import java.io.StringReader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DumpCommentInFlowStyleTest {

  private String extractInlineComment(Node node) {
    MappingNode mapping = (MappingNode) node;
    List<NodeTuple> value = mapping.getValue();
    NodeTuple first = value.get(0);
    Node textNode = first.getValueNode();
    return textNode.getInLineComments().get(0).getValue();
  }

  @Test
  public void testFlowWithComments() {
    LoadSettings loadSettings = LoadSettings.builder().setParseComments(true).build();
    Compose loader = new Compose(loadSettings);
    String content = "{url: text # comment breaks it\n}";
    Parse parser = new Parse(loadSettings);
    for (Event event : parser.parseReader(new StringReader(content))) {
      // System.out.println(event);
    }

    Node node = loader.composeReader(new StringReader(content)).get();
    assertEquals(" comment breaks it", extractInlineComment(node));

    DumpSettings dumpSettings = DumpSettings.builder().setDumpComments(true).build();
    Serialize serialize = new Serialize(dumpSettings);
    List<Event> events = serialize.serializeOne(node);
    for (Event event : events) {
      // System.out.println(event);
    }
    assertEquals(9, events.size());

    Present present = new Present(dumpSettings);
    String output = present.emitToString(events.iterator());
    assertEquals(content, output.trim());
  }

  @Test
  public void testBlockWithComments() {
    Compose loader = new Compose(LoadSettings.builder().setParseComments(true).build());
    String content = "url: text # comment breaks it\n";
    Node node = loader.composeReader(new StringReader(content)).get();

    assertEquals(" comment breaks it", extractInlineComment(node));

    DumpSettings dumpSettings = DumpSettings.builder().setDumpComments(true).build();
    Serialize serialize = new Serialize(dumpSettings);
    List<Event> events = serialize.serializeOne(node);
    for (Event event : events) {
      // System.out.println(event);
    }
    assertEquals(9, events.size());

    Present present = new Present(dumpSettings);
    String output = present.emitToString(events.iterator());
    assertEquals(content, output);
  }
}
