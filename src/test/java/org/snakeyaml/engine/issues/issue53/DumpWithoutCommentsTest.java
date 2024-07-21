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
package org.snakeyaml.engine.issues.issue53;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.YamlOutputStreamWriter;
import org.snakeyaml.engine.v2.api.lowlevel.Serialize;
import org.snakeyaml.engine.v2.composer.Composer;
import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.parser.Parser;
import org.snakeyaml.engine.v2.parser.ParserImpl;
import org.snakeyaml.engine.v2.scanner.StreamReader;
import org.snakeyaml.engine.v2.serializer.Serializer;

/**
 * Issue 53
 */
@org.junit.jupiter.api.Tag("fast")
public class DumpWithoutCommentsTest {

  private final String yaml = "a: 1 # A\n" + "b: 2 # B\n";


  @DisplayName("Issue 53 - Serialization failure of commented Node")
  @Test
  public void dumpMapWithComments() {
    LoadSettings loadSettings = LoadSettings.builder().setParseComments(true).build();
    DumpSettings dumpSettings = DumpSettings.builder().setDumpComments(true).build();// TODO false

    Parser parser =
        new ParserImpl(loadSettings, new StreamReader(loadSettings, new StringReader(yaml)));
    Composer composer = new Composer(loadSettings, parser);
    Node node = composer.getSingleNode().orElseThrow();
    assertNotNull(node);

    Emitter emitter = new Emitter(dumpSettings,
        new YamlOutputStreamWriter(new ByteArrayOutputStream(), StandardCharsets.UTF_8) {
          @Override
          public void processIOException(IOException e) {
            throw new RuntimeException(e);
          }
        });
    Serializer serializer = new Serializer(dumpSettings, emitter);

    serializer.emitStreamStart();
    serializer.serializeDocument(node);
    serializer.emitStreamEnd();
  }

  @DisplayName("Issue 53 - Serialization failure of commented Node")
  @Test
  public void dumpMapWithComments2() {
    LoadSettings loadSettings = LoadSettings.builder().setParseComments(true).build();
    String source = "a: 1 # comment";
    Parser parser =
        new ParserImpl(loadSettings, new StreamReader(loadSettings, new StringReader(source)));
    Composer composer = new Composer(loadSettings, parser);
    Node node = composer.getSingleNode().orElseThrow();
    assertNotNull(node);

    DumpSettings dumpSettings = DumpSettings.builder().setDumpComments(false).build();
    Serialize serializer = new Serialize(dumpSettings);
    List<Event> events = serializer.serializeOne(node);
    System.out.println(events);
  }
}
