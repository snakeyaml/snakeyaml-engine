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
package org.snakeyaml.engine.v2.representer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.TreeRangeSet;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.SequenceNode;

@Tag("fast")
class StandardRepresenterTest {

  private final StandardRepresenter standardRepresenter =
      new StandardRepresenter(DumpSettings.builder().build());

  @Test
  @DisplayName("Represent unknown class")
  void representUnknownClass() {
    YamlEngineException exception = assertThrows(YamlEngineException.class,
        () -> standardRepresenter.represent(TreeRangeSet.create()));
    assertEquals("Representer is not defined for class com.google.common.collect.TreeRangeSet",
        exception.getMessage());
  }

  @Test
  @DisplayName("Represent Enum as node with global tag")
  void represenEnum() {
    Node node = standardRepresenter.represent(FormatEnum.JSON);
    assertEquals("tag:yaml.org,2002:org.snakeyaml.engine.v2.representer.FormatEnum",
        node.getTag().getValue());
  }

  @Test
  @DisplayName("Represent Iterator as node with global tag")
  void representIterator() {
    List<String> listOfStrings = List.of("hello", "world");
    Iterator<String> iterator = listOfStrings.iterator();
    Node node = standardRepresenter.represent(iterator);
    assertEquals("tag:yaml.org,2002:seq", node.getTag().getValue());
    SequenceNode seq = (SequenceNode) node;
    assertEquals(2, seq.getValue().size());
    seq.getValue().forEach(n -> assertEquals("tag:yaml.org,2002:str", n.getTag().getValue()));
    // dump
    Dump dumper = new Dump(DumpSettings.builder().build());
    assertEquals("[hello, world]\n", dumper.dumpToString(listOfStrings.iterator()));
  }
}
