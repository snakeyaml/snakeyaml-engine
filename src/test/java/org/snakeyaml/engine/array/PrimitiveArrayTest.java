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
package org.snakeyaml.engine.array;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;

@org.junit.jupiter.api.Tag("fast")
public class PrimitiveArrayTest {

  private final byte[] bytes = new byte[] {1, 2, 3};
  private final short[] shorts = new short[] {300, 301, 302};
  private final int[] ints = new int[] {40000, 40001, 40002};
  private final long[] longs = new long[] {5000000000L, 5000000001L};
  private final float[] floats = new float[] {0.1f, 3.1415f};
  private final double[] doubles = new double[] {50.0001, 2150.0002};
  private final char[] chars = new char[] {'a', 'b', 'c', 'd', 'e'};
  private final boolean[] bools = new boolean[] {true, false};

  @Test
  void representArrayOfPrimitives() {
    Dump dumper = new Dump(DumpSettings.builder().build());
    assertEquals("!!binary |-\n" + "  AQID\n", dumper.dumpToString(bytes));

    assertEquals("[300, 301, 302]\n", dumper.dumpToString(shorts));
    assertEquals("[40000, 40001, 40002]\n", dumper.dumpToString(ints));
    assertEquals("[5000000000, 5000000001]\n", dumper.dumpToString(longs));
    assertEquals("[0.1, 3.1415]\n", dumper.dumpToString(floats));
    assertEquals("[50.0001, 2150.0002]\n", dumper.dumpToString(doubles));
    assertEquals("[a, b, c, d, e]\n", dumper.dumpToString(chars));
    assertEquals("[true, false]\n", dumper.dumpToString(bools));
  }

  @Test
  void representMultiArrayOfBytes() {
    byte[] bytes1 = new byte[] {1, 2, 3};
    byte[] bytes2 = new byte[] {11, 12, 13};
    byte[][] bytes3 = new byte[][] {bytes1, bytes2};
    Dump dumper = new Dump(DumpSettings.builder().build());
    assertEquals("- !!binary |-\n" + "  AQID\n" + "- !!binary |-\n" + "  CwwN\n",
        dumper.dumpToString(bytes3));
  }

  @Test
  void representMultiArrayOfIntPrimitives() {
    int[] bytes1 = new int[] {1, 2, 3};
    int[] bytes2 = new int[] {11, 12, 13};
    int[][] bytes3 = new int[][] {bytes1, bytes2};
    Dump dumper = new Dump(DumpSettings.builder().build());
    assertEquals("- [1, 2, 3]\n" + "- [11, 12, 13]\n", dumper.dumpToString(bytes3));
  }

  @Test
  void representMultiArrayOfIntegers() {
    Integer[] bytes1 = new Integer[] {1, 2, 3};
    Integer[] bytes2 = new Integer[] {11, 12, 13};
    Integer[][] bytes3 = new Integer[][] {bytes1, bytes2};
    Dump dumper = new Dump(DumpSettings.builder().build());
    assertEquals("- [1, 2, 3]\n" + "- [11, 12, 13]\n", dumper.dumpToString(bytes3));
  }
}
