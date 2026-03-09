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
package org.snakeyaml.engine.issues.issue79;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.constructor.json.ConstructYamlJsonFloat;
import org.snakeyaml.engine.v2.util.TestUtils;

/**
 * Test for Issue 79 - float values should preserve decimal precision using BigDecimal
 */
@Tag("fast")
public class FloatPrecisionTest {

  /**
   * Custom float constructor that produces BigDecimal instead of Double for exact decimal
   * precision. Special values (.inf, -.inf, .nan) still return Double.
   */
  static class ConstructBigDecimalFloat extends ConstructYamlJsonFloat {

    @Override
    protected Object constructFromString(String value) {
      return new BigDecimal(value);
    }
  }

  @Test
  @DisplayName("Issue 79: float values should be parsed as BigDecimal for exact decimal precision")
  @SuppressWarnings("unchecked")
  void floatValuesShouldBeParsedAsBigDecimal() {
    String yaml = TestUtils.getResource("issues/issue79-input.yaml");
    // This is needed because SnakeYAML Engine parses floats as Double,
    // losing exact decimal precision (e.g. 1.202 becomes 1.2019999999999999... internally)
    LoadSettings settings = LoadSettings.builder()
        .setTagConstructors(
            Map.of(org.snakeyaml.engine.v2.nodes.Tag.FLOAT, new ConstructBigDecimalFloat()))
        .build();
    Load load = new Load(settings);
    Map<String, Object> root = (Map<String, Object>) load.loadFromString(yaml);

    Map<String, Object> responses = (Map<String, Object>) root.get("responses");
    Map<String, Object> ok = (Map<String, Object>) responses.get("200");
    Map<String, Object> content = (Map<String, Object>) ok.get("content");
    Map<String, Object> json = (Map<String, Object>) content.get("application/json");
    Map<String, Object> examples = (Map<String, Object>) json.get("examples");

    Map<String, Object> simple = (Map<String, Object>) examples.get("simple");
    Map<String, Object> simpleValue = (Map<String, Object>) simple.get("value");
    Object simpleAmount = simpleValue.get("amount");

    Map<String, Object> multimodal = (Map<String, Object>) examples.get("multimodal");
    Map<String, Object> multimodalValue = (Map<String, Object>) multimodal.get("value");
    Object multimodalAmount = multimodalValue.get("amount");

    assertInstanceOf(BigDecimal.class, simpleAmount);
    assertEquals(new BigDecimal("1.202"), simpleAmount);

    assertInstanceOf(BigDecimal.class, multimodalAmount);
    assertEquals(new BigDecimal("0.00341775"), multimodalAmount);
  }
}
