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
package org.snakeyaml.engine.v2.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.nodes.Tag;

@org.junit.jupiter.api.Tag("fast")
class CoreScalarResolverTest {

  private final ScalarResolver scalarResolver = new CoreScalarResolver(true);

  @Test
  void resolveImplicitInteger() {
    assertTrue(CoreScalarResolver.INT.matcher("0o1010").matches());
    assertFalse(CoreScalarResolver.INT.matcher("0b1010").matches());

    assertEquals(Tag.STR, scalarResolver.resolve("0b1010", true));
  }
}
