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
package org.snakeyaml.engine.usecases.merge;


import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Compose;
import org.snakeyaml.engine.v2.api.lowlevel.Present;
import org.snakeyaml.engine.v2.api.lowlevel.Serialize;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.schema.CoreSchema;
import org.snakeyaml.engine.v2.utils.TestUtils;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


@org.junit.jupiter.api.Tag("fast")
public class MergeOnComposeTest {

  private String merge(String inputName, LoadSettings loadSettings) {
    String input = TestUtils.getResource(inputName);
    Compose loader = new Compose(loadSettings);
    Optional<Node> loaded = loader.composeReader(new StringReader(input));
    Node sourceTree = loaded.get();
    Serialize serialize = new Serialize(DumpSettings.builder().setDereferenceAliases(true).build());
    List<Event> events = serialize.serializeOne(sourceTree);
    Present present = new Present(DumpSettings.builder().build());

    return present.emitToString(events.iterator());
  }

  @Test
  public void simple_load_Merge() {
    String out = merge("merge/issue1096-simple-merge-input.yaml",
        LoadSettings.builder().setSchema(new CoreSchema()).build());
    String expected = TestUtils.getResource("merge/issue1096-simple-merge-output.yaml");
    assertEquals(expected, out);
  }

  @Test
  public void complex_load_Merge() {
    String out = merge("merge/issue1096-complex-merge-input.yaml",
        LoadSettings.builder().setSchema(new CoreSchema()).build());
    String expected = TestUtils.getResource("merge/issue1096-complex-merge-output.yaml");
    assertEquals(expected, out);
  }

  @Test
  public void specs_load_Merge() {
    String out = merge("merge/issue1096-merge-input.yaml",
        LoadSettings.builder().setSchema(new CoreSchema()).setParseComments(false).build());
    String expected = TestUtils.getResource("merge/issue1096-merge-output.yaml");
    assertEquals(expected, out);
  }

  @Test
  public void merge_As_Scalar() {
    String str =
        "test-list:\n" + " - &1\n" + "   a: 1\n" + "   b: 2\n" + " - &2 <<: *1\n" + " - <<: *2";

    Compose loader = new Compose(
        LoadSettings.builder().setSchema(new CoreSchema()).setParseComments(false).build());
    try {
      loader.composeReader(new StringReader(str));
      fail();
    } catch (Exception e) {
      String error = e.getMessage();
      assertTrue(error.contains("Expected mapping node or an anchor referencing mapping"), error);
      assertTrue(error.contains("in reader, line 6, column 10:"), error);
    }
  }
}
