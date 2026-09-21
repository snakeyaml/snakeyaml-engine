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
package org.snakeyaml.engine.issues.issue97;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.SequenceNode;

/**
 * Issue 97: a block scalar (| or &gt;) used as a sequence entry, with a comment on its header line,
 * has no preceding node to absorb that comment - unlike the equivalent mapping value, where the key
 * absorbs it. The composer threw an unchecked ClassCastException (CommentEvent cannot be cast to
 * NodeEvent) when comment parsing was enabled.
 */
@Tag("fast")
class BlockScalarAsSequenceEntryWithCommentTest {

  private final LoadSettings settings = LoadSettings.builder().setParseComments(true).build();

  @Test
  @DisplayName("Issue 97: folded block scalar as a sequence entry with a header comment loads")
  void loadFoldedBlockScalarAsSequenceEntry() {
    String yaml = "- > # c\n  text\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals(List.of("text\n"), loaded);
  }

  @Test
  @DisplayName("Issue 97: literal block scalar as a sequence entry with a header comment loads")
  void loadLiteralBlockScalarAsSequenceEntry() {
    String yaml = "- | # c\n  text\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals(List.of("text\n"), loaded);
  }

  @Test
  @DisplayName("Issue 97: strip chomping variant")
  void loadBlockScalarWithStripChompingAsSequenceEntry() {
    String yaml = "- >- # c\n  text\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals(List.of("text"), loaded);
  }

  @Test
  @DisplayName("Issue 97: keep chomping variant")
  void loadBlockScalarWithKeepChompingAsSequenceEntry() {
    String yaml = "- >+ # c\n  text\n\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals(List.of("text\n\n"), loaded);
  }

  @Test
  @DisplayName("Issue 97: empty block scalar as a sequence entry with a header comment")
  void loadEmptyBlockScalarAsSequenceEntry() {
    String yaml = "- > # c\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals(List.of(""), loaded);
  }

  @Test
  @DisplayName("Issue 97: block scalar as an entry of a sequence nested inside a mapping value")
  void loadBlockScalarAsEntryOfNestedSequence() {
    String yaml = "outer:\n  - > # c\n    text\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals(Map.of("outer", List.of("text\n")), loaded);
  }

  @Test
  @DisplayName("Issue 97: the header comment is attached to the block scalar's own node, not lost")
  void composeAttachesHeaderCommentToTheBlockScalarNode() {
    String yaml = "- > # c\n  text\n";
    Optional<Node> nodeOptional = new Compose(settings).composeString(yaml);
    assertTrue(nodeOptional.isPresent());

    SequenceNode root = (SequenceNode) nodeOptional.get();
    ScalarNode entry = (ScalarNode) root.getValue().get(0);
    List<CommentLine> inLineComments = entry.getInLineComments();
    assertEquals(1, inLineComments.size());
    assertEquals(" c", inLineComments.get(0).getValue());
  }

  @Test
  @DisplayName("Issue 97: regression - a block scalar mapping value still lets the key absorb the "
      + "header comment, unaffected by the sequence-entry fix")
  void mappingValueStillAttachesCommentToTheKey() {
    String yaml = "k: > # c\n  text\n";
    Optional<Node> nodeOptional = new Compose(settings).composeString(yaml);
    assertTrue(nodeOptional.isPresent());

    MappingNode root = (MappingNode) nodeOptional.get();
    ScalarNode key = (ScalarNode) root.getValue().get(0).getKeyNode();
    ScalarNode value = (ScalarNode) root.getValue().get(0).getValueNode();
    assertEquals(1, key.getInLineComments().size());
    assertEquals(" c", key.getInLineComments().get(0).getValue());
    assertEquals(List.of(), value.getInLineComments());
  }
}
