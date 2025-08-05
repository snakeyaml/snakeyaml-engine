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
package org.snakeyaml.engine.usecases.json;

import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * https://bitbucket.org/snakeyaml/snakeyaml/issues/1084/dump-as-json
 */
@org.junit.jupiter.api.Tag("fast")
public class DumperJsonTest {

  private Dump createYaml() {
    DumpSettings options = DumpSettings.builder().setDefaultFlowStyle(FlowStyle.FLOW)
        .setDefaultScalarStyle(ScalarStyle.JSON_SCALAR_STYLE).build();
    return new Dump(options);
  }

  @Test
  public void testJsonStr() {
    assertEquals("\"bar1\"\n", createYaml().dumpToString("bar1"));
  }

  @Test
  public void testJsonInt() {
    assertEquals("17\n", createYaml().dumpToString(17));
  }

  @Test
  public void testJsonIntAsStr() {
    assertEquals("\"17\"\n", createYaml().dumpToString("17"));
  }

  @Test
  public void testJsonIntInCollection() {
    List<Object> list = new ArrayList<Object>();
    list.add(17);
    list.add("17");
    assertEquals("[17, \"17\"]\n", createYaml().dumpToString(list));
  }

  @Test
  public void testJsonBoolean() {
    assertEquals("true\n", createYaml().dumpToString(true));
  }

  @Test
  public void testJsonBooleanInCollection() {
    List<Object> list = new ArrayList<Object>();
    list.add(true);
    list.add("true");
    assertEquals("[true, \"true\"]\n", createYaml().dumpToString(list));
  }

  @Test
  public void testJsonNull() {
    assertEquals("null\n", createYaml().dumpToString(null));
  }

  @Test
  public void testJsonNullInCollection() {
    List<Object> list = new ArrayList<Object>();
    list.add(null);
    list.add("null");
    assertEquals("[null, \"null\"]\n", createYaml().dumpToString(list));
  }

  /**
   * https://bitbucket.org/snakeyaml/snakeyaml/issues/1084/dump-as-json
   */
  @Test
  public void testJson() {
    Dump dump = createYaml();

    List<Object> list = new ArrayList<Object>();
    list.add(17);
    list.add("foo");
    list.add(true);
    list.add("true");
    list.add(false);
    list.add("false");
    list.add(null);
    list.add("null");

    assertEquals("[17, \"foo\", true, \"true\", false, \"false\", null, \"null\"]\n",
        dump.dumpToString(list));
  }

  /**
   * https://bitbucket.org/snakeyaml/snakeyaml/issues/1084/dump-as-json
   */
  @Test
  public void testJsonObject() {
    Dump dump = createYaml();

    Map<String, Object> map = new LinkedHashMap<>();
    map.put("str1", "foo");
    map.put("bool", true);
    map.put("strBool", "true");
    map.put("null", null);
    map.put("strNull", "null");
    assertEquals(
        "{\"str1\": \"foo\", \"bool\": true, \"strBool\": \"true\", \"null\": null, \"strNull\": \"null\"}\n",
        dump.dumpToString(map));
  }

  /**
   * TODO decide what to do with binary when dumped as JSON
   */
  @Test
  public void testJsonBinary() {
    Dump dump = createYaml();
    Map<String, Object> map = new LinkedHashMap<>();
    byte[] binary = {8, 14, 15, 10, 126, 32, 65, 65, 65};
    map.put("binary", binary);
    assertEquals("{\"binary\": !!binary \"CA4PCn4gQUFB\"}\n", dump.dumpToString(map));
  }
}
