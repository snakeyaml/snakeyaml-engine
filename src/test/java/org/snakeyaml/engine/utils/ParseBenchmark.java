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

package org.snakeyaml.engine.utils;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Parse;
import org.snakeyaml.engine.v2.events.Event;


import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * JMH microbenchmark to test average processing time for relatively simple small (18.34 KiB for one
 * thousand entries) and medium (2.17 MiB for one hundred thousand entries) yaml documents
 * containing map of `entries`.
 */
@Fork(1)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 3, time = 10)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ParseBenchmark {

  @Param({"1000", "100000"})
  private int entries;
  private String yamlString;
  private final Load load =
      new Load(LoadSettings.builder().setCodePointLimit(Integer.MAX_VALUE).build());
  private final Parse parse = new Parse(LoadSettings.builder().build());
  private final Dump dump = new Dump(DumpSettings.builder().build());

  public static void main(String[] args) throws RunnerException {
    new Runner(new OptionsBuilder().include(ParseBenchmark.class.getSimpleName()).build()).run();
  }

  @Setup
  public void setup() throws IOException {
    Map<Integer, String> map = new HashMap<>(entries);
    for (int i = 0; i < entries; i++) {
      map.put(i, Integer.toString(i));
    }
    yamlString = dump.dumpToString(map);
    System.out.printf("%nyaml bytes length: %d%n",
        yamlString.getBytes(StandardCharsets.UTF_8).length);
  }

  @Benchmark
  public int parse(Blackhole bh) throws IOException {
    int count = 0;
    for (Event event : parse.parseReader(new StringReader(yamlString))) {
      bh.consume(event.getEventId());
      count++;
    }
    return count;
  }

  @Benchmark
  public Object load() throws IOException {
    return load.loadFromString(yamlString);
  }
}

