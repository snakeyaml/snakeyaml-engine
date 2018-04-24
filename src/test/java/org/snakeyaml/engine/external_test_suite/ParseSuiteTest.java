/**
 * Copyright (c) 2018, http://www.snakeyaml.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.snakeyaml.engine.external_test_suite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.snakeyaml.engine.api.LoadSettings;
import org.snakeyaml.engine.api.Parse;
import org.snakeyaml.engine.events.Event;
import org.snakeyaml.engine.exceptions.YamlEngineException;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

@org.junit.jupiter.api.Tag("fast")
class ParseSuiteTest {
    public static final List<String> deviationsWithSuccess = Lists.newArrayList("9C9N", "SU5Z", "QB6E", "QLJ7", "EB22");
    public static final List<String> deviationsWithError = Lists.newArrayList("CXX2", "KZN9", "J3BT", "DC7X", "6HB6", "2JQS", "6M2F", "S3PD", "Q5MG", "FRK4", "NHX8", "DBG4", "4ABK", "M7A3", "9MMW", "6BCT", "A2M4", "2SXE", "DK3J", "W5VH", "8XYN", "K54U", "HS5T", "UT92", "W4TN", "FP8R", "WZ62", "7Z25");

    private List<SuiteData> all = SuiteUtils.getAll().stream()
            .filter(data -> !deviationsWithSuccess.contains(data.getName()))
            .filter(data -> !deviationsWithError.contains(data.getName()))
            .collect(Collectors.toList());

    public static ParseResult parseData(SuiteData data) {
        Optional<Exception> error = Optional.empty();
        List<Event> list = new ArrayList();
        try {
            LoadSettings settings = new LoadSettings();
            settings.setLabel(data.getLabel());
            Iterable<Event> iterable = new Parse(settings).parseString(data.getInput());
            iterable.forEach(event -> list.add(event));
        } catch (YamlEngineException e) {
            error = Optional.of(e);
        }
        return new ParseResult(list, error);
    }

    @Test
    @DisplayName("Parse: Run one test")
    void runOne(TestInfo testInfo) {
        SuiteData data = SuiteUtils.getOne("6FWR");
        LoadSettings settings = new LoadSettings();
        settings.setLabel(data.getLabel());
        Iterable<Event> iterable = new Parse(settings).parseString(data.getInput());
        for (Event event : iterable) {
            System.out.println(event);
        }
    }

    @Test
    @DisplayName("Run comprehensive test suite")
    void runAll(TestInfo testInfo) {
        for (SuiteData data : all) {
            ParseResult result = parseData(data);
            if (data.getError()) {
                assertTrue(result.getError().isPresent(), "Expected error, but got none in file " + data.getName() + ", " +
                        data.getLabel() + "\n" + result.getEvents());
            } else {
                if (result.getError().isPresent()) {
                    fail("Expected NO error, but got: " + result.getError().get());
                } else {
                    List<ParsePair> pairs = Streams.zip(data.getEvents().stream(), result.getEvents().stream(), ParsePair::new)
                            .collect(Collectors.toList());
                    for (ParsePair pair : pairs) {
                        assertEquals(pair.getEtalon(), pair.getEvent().toString());
                    }
                }
            }
        }
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


class ParsePair {
    private String etalon;
    private Event event;

    public ParsePair(String etalon, Event event) {
        this.etalon = etalon;
        this.event = event;
    }

    public String getEtalon() {
        return etalon;
    }

    public Event getEvent() {
        return event;
    }
}