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
package org.snakeyaml.engine.usecases.external_test_suite;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.api.lowlevel.Parse;
import org.snakeyaml.engine.v2.events.Event;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

public class SuiteUtils {

  // all 4 similar parsers fail (go-yaml/yaml, libyaml, PyYAML, Ruamel) if not specified

  public static final List<String> deviationsWithSuccess = Lists.newArrayList( // should have failed
      "9JBA", // Comment must be separated from other tokens by white space characters
      "CVW2", // Comments must be separated from other tokens by white space characters
      "9C9N", // Wrong indented flow sequence
      "SU5Z", // Comment without whitespace after double-quoted scalar
      "QB6E", // Wrong indented multiline quoted scalar
      "Y79Y-003" // TODO Tabs in various contexts (go-yaml/yaml, libyaml)
  );
  public static final List<String> deviationsWithError = Lists.newArrayList( // just keep it
      "HWV9", // Document-end marker
      "NB6Z", // TODO Multiline plain value with tabs on empty lines
      "VJP3-01", // Flow collections over many lines
      "5MUD", // Colon and adjacent value on next line
      "9SA2", // Multiline double quoted flow mapping key
      "QT73", // Comment and document-end marker
      "CFD4", // Empty implicit key in single pair flow sequences
      "NJ66", // Multiline plain flow mapping key
      "NKF9", // Empty keys in block and flow mapping
      "K3WX", // Colon and adjacent value after comment on next line
      "5T43", // Colon at the beginning of adjacent flow scalar
      "SM9W-01", // Single character streams
      "4MUZ-00", // Flow mapping colon on line after key
      "4MUZ-01", // Flow mapping colon on line after key
      "4MUZ-02", // Flow mapping colon on line after key
      "UKK6-00", // Syntax character edge cases (Go, libyaml, PyYAML)
      "K54U", // TODO Tab after document header
      "Y79Y-010", // Tabs in various contexts
      "2JQS", // Block Mapping with Missing Keys (Go, libyaml, PyYAML)
      "6M2F", // Aliases in Explicit Block Mapping (Go, libyaml, PyYAML)
      "S3PD", // Spec Example 8.18. Implicit Block Mapping Entries (Go, libyaml, PyYAML)
      "FRK4", // Spec Example 7.3. Completely Empty Flow Nodes (Go, libyaml, PyYAML)
      "NHX8", // Empty Lines at End of Document (Go, libyaml, PyYAML)
      "M2N8-00", // Question mark edge cases (Go, libyaml, PyYAML)
      "MUS6-03", // TODO Directive variants
      "6BCT", // Spec Example 6.3. Separation Spaces
      "Q5MG", // Tab at beginning of line followed by a flow mapping
      "DBG4", // Spec Example 7.10. Plain Characters (Go, libyaml, PyYAML)
      "M7A3", // Spec Example 9.3. Bare Documents
      "DK3J", // Zero indented block scalar with line that looks like a comment (Go, libyaml,
              // PyYAML)
      "W5VH", // Allowed characters in alias (Go, libyaml, PyYAML)
      "58MP", // Flow mapping edge cases (Go, libyaml, PyYAML)
      "UV7Q", // TODO Legal tab after indentation (PyYAML, Ruamel)
      "HM87-00", // Scalars in flow start with syntax char (Go, libyaml, PyYAML)
      "DC7X", // Various trailing tabs (PyYAML, Ruamel)
      "A2M4", // Spec Example 6.2. Indentation Indicators
      "J3BT", // Spec Example 5.12. Tabs and Spaces (PyYAML, Ruamel)
      "HS5T", // Spec Example 7.12. Plain Lines >> leading TAB (PyYAML, Ruamel)
      "UT92", // Spec Example 9.4. Explicit Documents
      "W4TN", // Spec Example 9.5. Directives Documents (Go, libyaml, PyYAML)
      "FP8R", // TODO Zero indented block scalar (Go, libyaml, PyYAML)
      "WZ62", // TODO Spec Example 7.2. Empty Content (Go, PyYAML, Ruamel)
      "7Z25" // TODO Bare document after document end marker (Go, libyaml, PyYAML)
  );


  public static final String FOLDER_NAME = "src/test/resources/comprehensive-test-suite-data";

  public static List<File> getAllFoldersIn(String folder) {
    File file = new File(folder);
    if (!file.exists()) {
      throw new RuntimeException("Folder not found: " + file.getAbsolutePath());
    }
    if (!file.isDirectory()) {
      throw new RuntimeException("Must be folder: " + file.getAbsolutePath());
    }
    return Arrays.stream(file.listFiles()).filter(f -> f.isDirectory())
        .collect(Collectors.toList());
  }

  public static SuiteData readData(File file) {
    try {
      String name = file.getName();
      String label = Files.asCharSource(new File(file, "==="), Charsets.UTF_8).read();
      String input = Files.asCharSource(new File(file, "in.yaml"), Charsets.UTF_8).read();
      List<String> events = Files.readLines(new File(file, "test.event"), Charsets.UTF_8).stream()
          .filter(line -> !line.isEmpty()).collect(Collectors.toList());
      boolean error = new File(file, "error").exists();
      return new SuiteData(name, label, input, events, error);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<SuiteData> getAll() {
    List<File> allSuiteFiles = getAllFoldersIn(FOLDER_NAME);
    return allSuiteFiles.stream().map(file -> readData(file)).collect(Collectors.toList());
  }

  public static SuiteData getOne(String name) {
    return readData(new File(FOLDER_NAME, name));
  }

  public static ParseResult parseData(SuiteData data) {
    Optional<Exception> error = Optional.empty();
    List<Event> list = new ArrayList();
    try {
      LoadSettings settings = LoadSettings.builder().setLabel(data.getLabel()).build();
      Iterable<Event> iterable = new Parse(settings).parseString(data.getInput());
      iterable.forEach(event -> list.add(event));
    } catch (YamlEngineException e) {
      error = Optional.of(e);
    }
    return new ParseResult(list, error);
  }
}


class ParseResult {

  private final List<Event> events;
  private final Optional<Exception> error;

  public ParseResult(List<Event> events, Optional<Exception> error) {
    this.events = events;
    this.error = error;
  }

  public List<Event> getEvents() {
    return events;
  }

  public Optional<Exception> getError() {
    return error;
  }
}
