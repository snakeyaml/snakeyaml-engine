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
package org.snakeyaml.engine.issues.issue92;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.ScalarNode;

/**
 * Issue 92: a block scalar (| or &gt;) nested inside a collection, followed by two or more indented
 * comment lines separated by a blank line, used to make the scanner misclassify those whole-line
 * comments as in-line comments. The stray comment event then leaked into the composer, which threw
 * an unchecked ClassCastException (CommentEvent cannot be cast to NodeEvent) when comment parsing
 * was enabled.
 */
@org.junit.jupiter.api.Tag("fast")
class BlockScalarFollowedByIndentedCommentsTest {

  private final LoadSettings settings = LoadSettings.builder().setParseComments(true).build();

  @Test
  @DisplayName("Issue 92: literal block scalar followed by two indented comments loads as expected")
  void loadLiteralBlockScalarFollowedByIndentedComments() {
    String yaml = "cm:\n  foo: |\n    x\n  # comment 1\n\n  # comment 2\n  bar: 1\n";
    Object loaded = new Load(settings).loadFromString(yaml);

    Map<String, Object> cm = new HashMap<>();
    cm.put("foo", "x\n");
    cm.put("bar", 1);
    Map<String, Object> expected = new HashMap<>();
    expected.put("cm", cm);
    assertEquals(expected, loaded);
  }

  @Test
  @DisplayName("Issue 92: the indented comments are attached as block comments, not dropped")
  void composeLiteralBlockScalarFollowedByIndentedComments() {
    String yaml = "cm:\n  foo: |\n    x\n  # comment 1\n\n  # comment 2\n  bar: 1\n";
    Optional<Node> nodeOptional = new Compose(settings).composeString(yaml);
    assertTrue(nodeOptional.isPresent());

    MappingNode root = (MappingNode) nodeOptional.get();
    MappingNode cm = (MappingNode) root.getValue().get(0).getValueNode();
    List<NodeTuple> tuples = cm.getValue();
    assertEquals(2, tuples.size());

    ScalarNode barKey = (ScalarNode) tuples.get(1).getKeyNode();
    assertEquals("bar", barKey.getValue());
    List<CommentLine> blockComments = barKey.getBlockComments();
    assertEquals(3, blockComments.size());
    assertEquals(CommentType.BLOCK, blockComments.get(0).getCommentType());
    assertEquals(" comment 1", blockComments.get(0).getValue());
    assertEquals(CommentType.BLANK_LINE, blockComments.get(1).getCommentType());
    assertEquals(CommentType.BLOCK, blockComments.get(2).getCommentType());
    assertEquals(" comment 2", blockComments.get(2).getValue());
  }

  @Test
  @DisplayName("Issue 92: folded block scalar variant")
  void loadFoldedBlockScalarFollowedByIndentedComments() {
    String yaml = "cm:\n  foo: >\n    x\n  # comment 1\n\n  # comment 2\n  bar: 1\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals("x\n", ((Map<?, ?>) ((Map<?, ?>) loaded).get("cm")).get("foo"));
  }

  @Test
  @DisplayName("Issue 92: explicit chomping indicator variant")
  void loadBlockScalarWithChompingIndicatorFollowedByIndentedComments() {
    String yaml = "cm:\n  foo: |-\n    x\n  # comment 1\n\n  # comment 2\n  bar: 1\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals("x", ((Map<?, ?>) ((Map<?, ?>) loaded).get("cm")).get("foo"));
  }

  @Test
  @DisplayName("Issue 92: block scalar inside a sequence item variant")
  void loadBlockScalarInSequenceFollowedByIndentedComments() {
    String yaml = "- foo: |\n    x\n  # comment 1\n\n  # comment 2\n  bar: 1\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals(List.of(Map.of("foo", "x\n", "bar", 1)), loaded);
  }

  @Test
  @DisplayName("Issue 92: '#' lines indented to the block scalar's own content level are literal "
      + "content, not comments - only dedented lines are")
  void loadBlockScalarFollowedByLinesIndentedAtContentLevel() {
    String yaml = "cm:\n  foo: |\n    x\n    # comment 1\n\n    # comment 2\n  bar: 1\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals(Map.of("cm", Map.of("foo", "x\n# comment 1\n\n# comment 2\n", "bar", 1)), loaded);
  }

  @Test
  @DisplayName("Issue 92: a single indented comment is a block comment on the next key, not an "
      + "in-line comment on the block scalar")
  void singleIndentedCommentIsABlockComment() {
    String yaml = "cm:\n  foo: |\n    x\n  # comment\n  bar: 1\n";
    Optional<Node> nodeOptional = new Compose(settings).composeString(yaml);
    assertTrue(nodeOptional.isPresent());

    MappingNode root = (MappingNode) nodeOptional.get();
    MappingNode cm = (MappingNode) root.getValue().get(0).getValueNode();

    assertEquals(List.of(), cm.getValue().get(0).getValueNode().getInLineComments());
    assertEquals(" comment",
        cm.getValue().get(1).getKeyNode().getBlockComments().get(0).getValue());
  }

  @Test
  @DisplayName("Issue 92: indented comments at the end of the stream variant")
  void loadBlockScalarFollowedByIndentedCommentsAtStreamEnd() {
    String yaml = "cm:\n  foo: |\n    x\n  # comment 1\n\n  # comment 2\n";
    Object loaded = new Load(settings).loadFromString(yaml);
    assertEquals("x\n", ((Map<?, ?>) ((Map<?, ?>) loaded).get("cm")).get("foo"));
  }
}
