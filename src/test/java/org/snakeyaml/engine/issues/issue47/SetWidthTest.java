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
package org.snakeyaml.engine.issues.issue47;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.StreamDataWriter;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.events.DocumentEndEvent;
import org.snakeyaml.engine.v2.events.DocumentStartEvent;
import org.snakeyaml.engine.v2.events.ImplicitTuple;
import org.snakeyaml.engine.v2.events.ScalarEvent;
import org.snakeyaml.engine.v2.events.StreamEndEvent;
import org.snakeyaml.engine.v2.events.StreamStartEvent;

import java.io.StringWriter;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test issue 47 <a href="https://yaml.org/spec/1.2.2/#3231-node-styles">Node styles</a>
 */
public class SetWidthTest {
  String stringToSerialize =
      "arn:aws:iam::12345678901234567890:foobarbaz:testing:testing2:role/github-actions-role/${{ github.token }}";

  private String parseBack(String yaml) {
    LoadSettings settings = LoadSettings.builder().build();
    Load load = new Load(settings);
    return load.loadFromString(yaml).toString();
  }

  @Test
  @DisplayName("Issue 47: emit plain and split")
  void emitPlainString() {
    DumpSettings settings = DumpSettings.builder().setWidth(80) // Intentionally limited.
        .build();
    StreamDataWriter writer = new StreamToStringWriter();
    Dump dump = new Dump(settings);

    dump.dump(stringToSerialize, writer);
    String yaml = writer.toString();
    String expected =
        "arn:aws:iam::12345678901234567890:foobarbaz:testing:testing2:role/github-actions-role/${{\n  github.token }}";
    assertEquals(stringToSerialize, parseBack(yaml));
    assertEquals(expected, yaml.trim());
  }

  @Test
  @DisplayName("Issue 47: emit plain and split")
  void emitPlain() {
    DumpSettings settings = DumpSettings.builder().setWidth(80) // Intentionally limited.
        .build();
    StreamDataWriter writer = new StreamToStringWriter();
    Emitter emitter = new Emitter(settings, writer);
    emitter.emit(new StreamStartEvent());
    emitter.emit(new DocumentStartEvent(false, Optional.empty(), emptyMap()));

    emitter.emit(new ScalarEvent(Optional.empty(), Optional.empty(), new ImplicitTuple(true, true),
        stringToSerialize, ScalarStyle.PLAIN));

    emitter.emit(new DocumentEndEvent(false));
    emitter.emit(new StreamEndEvent());
    String yaml = writer.toString();
    String expected =
        "arn:aws:iam::12345678901234567890:foobarbaz:testing:testing2:role/github-actions-role/${{\n  github.token }}";
    assertEquals(stringToSerialize, parseBack(yaml));
    assertEquals(expected, yaml.trim());
  }

  @Test
  @DisplayName("Issue 47: emit plain and no split")
  void emitPlainNoSplit() {
    DumpSettings settings = DumpSettings.builder().setWidth(180) // Intentionally limited.
        .build();
    StreamDataWriter writer = new StreamToStringWriter();
    Emitter emitter = new Emitter(settings, writer);
    emitter.emit(new StreamStartEvent());
    emitter.emit(new DocumentStartEvent(false, Optional.empty(), emptyMap()));

    emitter.emit(new ScalarEvent(Optional.empty(), Optional.empty(), new ImplicitTuple(true, true),
        stringToSerialize, ScalarStyle.PLAIN));

    emitter.emit(new DocumentEndEvent(false));
    emitter.emit(new StreamEndEvent());
    String yaml = writer.toString();
    String expected =
        "arn:aws:iam::12345678901234567890:foobarbaz:testing:testing2:role/github-actions-role/${{ github.token }}\n";
    assertEquals(expected, yaml);
    assertEquals(stringToSerialize, parseBack(yaml));
  }

  @Test
  @DisplayName("Issue 47: emit folded")
  void emitFolded() {
    DumpSettings settings = DumpSettings.builder().setWidth(80) // Intentionally limited.
        .setDefaultScalarStyle(ScalarStyle.FOLDED).build();
    Dump dump = new Dump(settings);
    String yaml = dump.dumpToString(stringToSerialize);
    String expected = ">-\n"
        + "  arn:aws:iam::12345678901234567890:foobarbaz:testing:testing2:role/github-actions-role/${{\n"
        + "  github.token }}\n";
    assertEquals(expected, yaml);
    assertEquals(stringToSerialize, parseBack(yaml));
  }
}


class StreamToStringWriter extends StringWriter implements StreamDataWriter {
}
