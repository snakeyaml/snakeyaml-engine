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
package org.snakeyaml.engine.issues.issue54;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Issue 54: add a space after anchor (when it is a simple key)
 */
@org.junit.jupiter.api.Tag("fast")
public class DumpWithoutSpaceTest {
  @Test
  @DisplayName("The document does not have a space after the *1 alias")
  void failToParseWithoutSpaceAfterAlias() {
    try {
      Object obj = parse("--- &1\nhash:\n  :one: true\n  :two: true\n  *1: true");
      fail();
    } catch (Exception e) {
      assertTrue(e.getMessage().contains("could not find expected ':'"));
    }
  }

  @Test
  @DisplayName("The output does include a space after the *1 alias")
  void parseWithSpaceAfterAlias() {
    Object obj = parse("--- &1\nhash:\n  :one: true\n  :two: true\n  *1 : true");
    assertNotNull(obj);
  }

  private Object parse(String data) {
    LoadSettings loadSettings = LoadSettings.builder().setAllowRecursiveKeys(true).build();
    Load load = new Load(loadSettings);
    return load.loadFromString(data);
  }

  @Test
  @DisplayName("Dump and load an alias")
  void parseOwnOutput() {
    HashMap<Object, Boolean> map = new HashMap<>();
    map.put(":one", true);
    map.put(map, true);
    DumpSettings dumpSettings = DumpSettings.builder().build();
    Dump dump = new Dump(dumpSettings);
    String output = dump.dumpToString(map);
    assertEquals("&id001\n" + ":one: true\n" + "*id001 : true\n", output);
    Object recursive = parse(output);
    assertNotNull(recursive);
  }
}
