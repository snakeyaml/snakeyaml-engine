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

