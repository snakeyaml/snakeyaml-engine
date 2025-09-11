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
package org.snakeyaml.engine.usecases.untrusted;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Iterator;
import org.junit.jupiter.api.Test;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;

/**
 * https://bitbucket.org/snakeyaml/snakeyaml/issues/1065
 */
class DocumentSizeLimitTest {

  /**
   * The document start '---\n' is added to the first document
   */
  @Test
  void testFirstLoadManyDocuments() {
    // within the limit = 8
    LoadSettings settings1 = LoadSettings.builder().setCodePointLimit(8).build();
    Load load1 = new Load(settings1);
    String doc = "---\nfoo\n---\nbarbar\n";
    Iterator<Object> iter1 = load1.loadAllFromString(doc).iterator();
    assertEquals("foo", iter1.next());
    assertEquals("barbar", iter1.next());
    assertFalse(iter1.hasNext());
    // exceed the limit by 1
    LoadSettings settings2 = LoadSettings.builder().setCodePointLimit(8 - 1).build();
    Load load2 = new Load(settings2);
    Iterator<Object> iter2 = load2.loadAllFromString(doc).iterator();
    assertEquals("foo", iter2.next()); // the first document is loaded
    try {
      iter2.next();
      fail("The second document should fail because of the limit");
    } catch (YamlEngineException e) {
      assertEquals("The incoming YAML document exceeds the limit: 7 code points.", e.getMessage());
    }
  }

  /**
   * The document start '---\n' is added to the non-first documents. Document indicators affect the
   * limit ('---' and '...')
   */
  @Test
  public void testLastLoadManyDocuments() {
    LoadSettings settings1 = LoadSettings.builder().setCodePointLimit(7).build();
    Load load1 = new Load(settings1);
    String complete = "foo\n...\n---\nbar\n";
    Iterator<Object> iter1 = load1.loadAllFromString(complete).iterator();
    assertEquals("foo", iter1.next());
    assertEquals("bar", iter1.next());
    assertFalse(iter1.hasNext());
    // exceed the limit
    LoadSettings settings2 = LoadSettings.builder().setCodePointLimit(6).build();
    Load load2 = new Load(settings2);
    Iterator<Object> iter2 = load2.loadAllFromString(complete).iterator();
    assertEquals("foo", iter2.next());
    try {
      iter2.next();
      fail("Second doc should fail because of doc limit");
    } catch (YamlEngineException e) {
      assertEquals("The incoming YAML document exceeds the limit: 6 code points.", e.getMessage());
    }
  }

  @Test
  void testLoadDocuments() {
    String doc1 = "document: this is document one\n";
    String doc2 = "---\ndocument: this is document 2\n";
    String docLongest = "---\ndocument: this is document three\n";
    String input = doc1 + doc2 + docLongest;

    assertTrue(dumpAllDocs(input, input.length()),
        "Test1. All should load, all docs are less than total input size.");

    assertTrue(dumpAllDocs(input, docLongest.length()),
        "Test2. All should load, all docs are less or equal to docLongest size.");

    assertFalse(dumpAllDocs(input, doc2.length()),
        "Test3. Fail to load, doc2 is not the longest in the stream.");
  }

  private boolean dumpAllDocs(String input, int codePointLimit) {
    LoadSettings settings1 = LoadSettings.builder().setCodePointLimit(codePointLimit).build();
    Load load = new Load(settings1);
    Iterator<Object> docs = load.loadAllFromString(input).iterator();
    for (int ndx = 1; ndx <= 3; ndx++) {
      try {
        Object doc = docs.next();
        assertNotNull(doc);
      } catch (Exception e) {
        return false;
      }
    }
    return true;
  }
}
