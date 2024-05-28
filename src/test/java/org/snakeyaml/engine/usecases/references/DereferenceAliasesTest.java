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
package org.snakeyaml.engine.usecases.references;


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.utils.TestUtils;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("fast")
public class DereferenceAliasesTest {

  @Test
  public void testNoAliases() {
    LoadSettings settings = LoadSettings.builder().build();
    Load load = new Load(settings);
    Map map = (Map) load.loadFromString(TestUtils.getResource("issues/issue1086-1-input.yaml"));
    DumpSettings setting = DumpSettings.builder().setDefaultFlowStyle(FlowStyle.BLOCK)
        .setDereferenceAliases(true).build();
    Dump dump = new Dump(setting);
    String node = dump.dumpToString(map);
    StringWriter out = new StringWriter();
    String expected = TestUtils.getResource("issues/issue1086-1-expected.yaml");
    assertEquals(expected, node);
  }

  @Test
  public void testNoAliasesRecursive() {
    LoadSettings settings = LoadSettings.builder().build();
    Load load = new Load(settings);
    Map map = (Map) load.loadFromString(TestUtils.getResource("issues/issue1086-2-input.yaml"));
    DumpSettings setting = DumpSettings.builder().setDefaultFlowStyle(FlowStyle.BLOCK)
        .setDereferenceAliases(true).build();
    Dump dump = new Dump(setting);
    try {
      dump.dumpToString(map);
      fail();
    } catch (YamlEngineException e) {
      assertEquals("Cannot dereferenceAliases for recursive structures.", e.getMessage());
    }
  }
}

