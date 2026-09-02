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
package org.snakeyaml.engine.issues.issue90;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.Tag;

/**
 * Issue 90: a tagged empty scalar (e.g. !!null) followed by a blank line used to leave the
 * enclosing block mapping unterminated when comment parsing was enabled, so the next key was
 * mistaken for the start of a new document.
 */
@org.junit.jupiter.api.Tag("fast")
class NullTagFollowedByEmptyLineTest {

  private final LoadSettings settings = LoadSettings.builder().setParseComments(true).build();

  @Test
  @DisplayName("Issue 90: !!null followed by an empty line does not break the mapping")
  void composeNullTagFollowedByEmptyLine() {
    String yaml = "a: !!null\n\nb: 1\n";
    Optional<Node> nodeOptional = new Compose(settings).composeString(yaml);
    assertEquals(true, nodeOptional.isPresent());

    MappingNode mappingNode = (MappingNode) nodeOptional.get();
    List<NodeTuple> tuples = mappingNode.getValue();
    assertEquals(2, tuples.size());

    ScalarNode keyA = (ScalarNode) tuples.get(0).getKeyNode();
    ScalarNode valueA = (ScalarNode) tuples.get(0).getValueNode();
    assertEquals("a", keyA.getValue());
    assertEquals(Tag.NULL, valueA.getTag());

    ScalarNode keyB = (ScalarNode) tuples.get(1).getKeyNode();
    ScalarNode valueB = (ScalarNode) tuples.get(1).getValueNode();
    assertEquals("b", keyB.getValue());
    assertEquals("1", valueB.getValue());
  }

  @Test
  @DisplayName("Issue 90: !!null followed by an empty line loads as expected")
  void loadNullTagFollowedByEmptyLine() {
    String yaml = "a: !!null\n\nb: 1\n";
    Object loaded = new Load(settings).loadFromString(yaml);

    Map<String, Object> expected = new HashMap<>();
    expected.put("a", null);
    expected.put("b", 1);
    assertEquals(expected, loaded);
    assertNull(((Map<?, ?>) loaded).get("a"));
  }
}
