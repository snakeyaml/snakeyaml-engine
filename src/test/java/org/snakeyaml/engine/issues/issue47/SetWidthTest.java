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

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.StringWriter;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.StreamDataWriter;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.emitter.Emitter;
import org.snakeyaml.engine.v2.events.DocumentEndEvent;
import org.snakeyaml.engine.v2.events.DocumentStartEvent;
import org.snakeyaml.engine.v2.events.ImplicitTuple;
import org.snakeyaml.engine.v2.events.ScalarEvent;
import org.snakeyaml.engine.v2.events.StreamEndEvent;
import org.snakeyaml.engine.v2.events.StreamStartEvent;

/**
 * https://bitbucket.org/snakeyaml/snakeyaml-engine/issues/47/strings-with-spaces-are-incorrectly
 */
public class SetWidthTest {

  @Test
  @DisplayName("Issue 46: parse different values")
  void parseDifferentValues() {
    DumpSettings settings = DumpSettings.builder().setWidth(80) // Intentionally limited.
        .build();
    StreamDataWriter writer = new StreamToStringWriter();
    Emitter emitter = new Emitter(settings, writer);
    emitter.emit(new StreamStartEvent());
    emitter.emit(new DocumentStartEvent(false, Optional.empty(), emptyMap()));

    String stringToSerialize =
        "arn:aws:iam::12345678901234567890:foobarbaz:testing:testing2:role/github-actions-role/${{ github.token }}";
    emitter.emit(new ScalarEvent(Optional.empty(), Optional.empty(), new ImplicitTuple(true, true),
        stringToSerialize, ScalarStyle.PLAIN));

    emitter.emit(new DocumentEndEvent(false));
    emitter.emit(new StreamEndEvent());
    String yaml = writer.toString();
    String expected =
        "arn:aws:iam::12345678901234567890:foobarbaz:testing:testing2:role/github-actions-role/${{ github.token }}";
    assertEquals(expected, yaml);
  }
}


class StreamToStringWriter extends StringWriter implements StreamDataWriter {

}
