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
package org.snakeyaml.engine.issues.issue68;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.nodes.Node;

@org.junit.jupiter.api.Tag("fast")
public class CommentAfterScalarTest {
  private final LoadSettings loadSettings = LoadSettings.builder().setParseComments(true).build();

  @Test
  @DisplayName("Respect inline comment for '!!str # comment'")
  void testInLineCommentForScalarNode() {
    Compose compose = new Compose(loadSettings);
    Optional<Node> node = compose.composeString("!!str # comment");
    assertNotNull(node);
    assertTrue(node.isPresent());
    assertEquals(1, node.get().getInLineComments().size());
    assertEquals(" comment", node.get().getInLineComments().stream().findFirst().get().getValue());
  }

  @Test
  @DisplayName("Respect inline and block comments for '!!str # comment\n# block comment1'")
  void testInLineCommentForScalarNode2() {
    Compose compose = new Compose(loadSettings);
    Optional<Node> node =
        compose.composeString("!!str # comment\n# block comment1\n# block comment2");
    assertNotNull(node);
    assertTrue(node.isPresent());
    assertEquals(1, node.get().getInLineComments().size());
    assertEquals(" comment", node.get().getInLineComments().stream().findFirst().get().getValue());
    assertEquals(2, node.get().getBlockComments().size());
    assertEquals(" block comment1",
        node.get().getBlockComments().stream().findFirst().get().getValue());
    assertEquals(" block comment2",
        node.get().getBlockComments().stream().skip(1).findFirst().get().getValue());
  }
}
