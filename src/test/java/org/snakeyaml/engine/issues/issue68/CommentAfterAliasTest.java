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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.nodes.Node;

/**
 * Issue 68: Comments are not parsed correctly when they follow an alias
 */
@org.junit.jupiter.api.Tag("fast")
class CommentAfterAliasTest {
  // TODO Issue 68: parse comments with setParseComments(true)
  private final LoadSettings loadSettings = LoadSettings.builder().setParseComments(false).build();

  @Test
  @DisplayName("Issue 68: inline")
  void testCommentAfterAlias3() {
    Compose compose = new Compose(loadSettings);
    String input = "field_with_alias: &alias_name # inline comment 1\n  555";
    Optional<Node> node = compose.composeString(input);
    assertNotNull(node);
    assertTrue(node.isPresent());
  }

  @Test
  @DisplayName("Issue 68: block comment and flat after")
  void testCommentAfterAlias1() {
    Compose compose = new Compose(loadSettings);
    String input = "field_with_alias: &alias_name\n"
        + "# separate line comment following the alias\n" + "    555";
    Optional<Node> node = compose.composeString(input);
    assertNotNull(node);
    assertTrue(node.isPresent());
  }

  @Test
  @DisplayName("Issue 68: block comment and nested after")
  void testCommentAfterAlias() {
    Compose compose = new Compose(loadSettings);
    String input = "field_with_alias: &alias_name\n"
        + "# separate line comment following the alias\n" + "    nested_field: nested_value";
    Optional<Node> node = compose.composeString(input);
    assertNotNull(node);
    assertTrue(node.isPresent());
  }
}
