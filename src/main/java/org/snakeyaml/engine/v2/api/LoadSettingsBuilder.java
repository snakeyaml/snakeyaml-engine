/*
 * Copyright (c) 2018, SnakeYAML
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.snakeyaml.engine.v2.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.UnaryOperator;
import org.snakeyaml.engine.v2.common.SpecVersion;
import org.snakeyaml.engine.v2.env.EnvConfig;
import org.snakeyaml.engine.v2.exceptions.YamlVersionException;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.JsonScalarResolver;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;

/**
 * Builder pattern implementation for LoadSettings
 */
public final class LoadSettingsBuilder {

  private String label;
  private Map<Tag, ConstructNode> tagConstructors;
  private ScalarResolver scalarResolver;
  private IntFunction<List> defaultList;
  private IntFunction<Set> defaultSet;
  private IntFunction<Map> defaultMap;
  private UnaryOperator<SpecVersion> versionFunction;
  private Integer bufferSize;
  private boolean allowDuplicateKeys;
  private boolean allowRecursiveKeys;
  private boolean parseComments;
  private int maxAliasesForCollections;
  private boolean useMarks;
  private Optional<EnvConfig> envConfig;


  //general
  private final Map<SettingKey, Object> customProperties = new HashMap();

  /**
   * Create builder
   */
  LoadSettingsBuilder() {
    this.label = "reader";
    this.tagConstructors = new HashMap<>();
    this.scalarResolver = new JsonScalarResolver();
    this.defaultList = ArrayList::new;    // same as new ArrayList(initSize)
    this.defaultSet = LinkedHashSet::new; // same as new LinkedHashSet(initSize)
    this.defaultMap = LinkedHashMap::new; // same as new LinkedHashMap(initSize)
    this.versionFunction = version -> {
      if (version.getMajor() != 1) {
        throw new YamlVersionException(version);
      }
      return version;
    };
    this.bufferSize = 1024;
    this.allowDuplicateKeys = false;
    this.allowRecursiveKeys = false;
    this.parseComments = false;
    this.maxAliasesForCollections = 50; //to prevent YAML at https://en.wikipedia.org/wiki/Billion_laughs_attack
    this.useMarks = true;
    this.envConfig = Optional.empty(); // no ENV substitution by default

  }

  /**
   * Label for the input data. Can be used to improve the error message.
   *
   * @param label - meaningful label to indicate the input source
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setLabel(String label) {
    Objects.requireNonNull(label, "label cannot be null");
    this.label = label;
    return this;
  }

  /**
   * Provide constructors for the specified tags.
   *
   * @param tagConstructors - the map from a Tag to its constructor
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setTagConstructors(Map<Tag, ConstructNode> tagConstructors) {
    this.tagConstructors = tagConstructors;
    return this;
  }

  /**
   * Provide resolver to detect a tag by the value of a scalar
   *
   * @param scalarResolver - specified ScalarResolver
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setScalarResolver(ScalarResolver scalarResolver) {
    Objects.requireNonNull(scalarResolver, "scalarResolver cannot be null");
    this.scalarResolver = scalarResolver;
    return this;
  }

  /**
   * Provide default List implementation. {@link ArrayList} is used if nothing provided.
   *
   * @param defaultList - specified List implementation (as a function from init size)
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setDefaultList(IntFunction<List> defaultList) {
    Objects.requireNonNull(defaultList, "defaultList cannot be null");
    this.defaultList = defaultList;
    return this;
  }

  /**
   * Provide default Set implementation. {@link LinkedHashSet} is used if nothing provided.
   *
   * @param defaultSet - specified Set implementation (as a function from init size)
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setDefaultSet(IntFunction<Set> defaultSet) {
    Objects.requireNonNull(defaultSet, "defaultSet cannot be null");
    this.defaultSet = defaultSet;
    return this;
  }

  /**
   * Provide default Map implementation. {@link LinkedHashMap} is used if nothing provided.
   *
   * @param defaultMap - specified Map implementation (as a function from init size)
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setDefaultMap(IntFunction<Map> defaultMap) {
    Objects.requireNonNull(defaultMap, "defaultMap cannot be null");
    this.defaultMap = defaultMap;
    return this;
  }

  /**
   * Buffer size for incoming data stream. If the incoming stream is already buffered, then changing
   * the buffer does not improve the performance
   *
   * @param bufferSize - buffer size (in bytes) for input data
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setBufferSize(Integer bufferSize) {
    this.bufferSize = bufferSize;
    return this;
  }

  /**
   * YAML 1.2 does require unique keys. To support the backwards compatibility it is possible to
   * select what should happend when non-unique keys are detected.
   *
   * @param allowDuplicateKeys - if true than the non-unique keys in a mapping are allowed (last key
   *                           wins). False by default.
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setAllowDuplicateKeys(boolean allowDuplicateKeys) {
    this.allowDuplicateKeys = allowDuplicateKeys;
    return this;
  }

  /**
   * Allow only non-recursive keys for maps and sets. By default is it not allowed. Even though YAML
   * allows to use anything as a key, it may cause unexpected issues when loading recursive
   * structures.
   *
   * @param allowRecursiveKeys - true to allow recursive structures as keys
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setAllowRecursiveKeys(boolean allowRecursiveKeys) {
    this.allowRecursiveKeys = allowRecursiveKeys;
    return this;
  }

  /**
   * Restrict the number of aliases for collection nodes to prevent Billion laughs attack. The
   * purpose of this setting is to force SnakeYAML to fail before a lot of CPU and memory resources
   * are allocated for the parser. Aliases for scalar nodes do not count because they do not grow
   * exponentially.
   *
   * @param maxAliasesForCollections - max number of aliases. More then 50 might be very dangerous.
   *                                 Default is 50
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setMaxAliasesForCollections(int maxAliasesForCollections) {
    this.maxAliasesForCollections = maxAliasesForCollections;
    return this;
  }

  /**
   * Marks are only used for error messages. But they requires a lot of memory. True by default.
   *
   * @param useMarks - use false to save resources but use less informative error messages (no line
   *                 and context)
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setUseMarks(boolean useMarks) {
    this.useMarks = useMarks;
    return this;
  }

  /**
   * Manage YAML directive value which defines the version of the YAML specification. This parser
   * supports YAML 1.2 but it can parse most of YAML 1.1 and YAML 1.0
   * <p>
   * This function allows to control the version management. For instance if the document contains
   * old version the parser can be adapted to compensate the problem. Or it can fail to indicate
   * that the incoming version is not supported.
   *
   * @param versionFunction - define the way to manage the YAML version. By default, 1.* versions
   *                        are accepted and treated as YAML 1.2. Other versions fail to parse
   *                        (YamlVersionException is thown)
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setVersionFunction(UnaryOperator<SpecVersion> versionFunction) {
    Objects.requireNonNull(versionFunction, "versionFunction cannot be null");
    this.versionFunction = versionFunction;
    return this;
  }

  /**
   * Define EnvConfig to parse EVN format. If not set explicitly the variable substitution is not
   * applied
   *
   * @param envConfig - non-empty configuration to substitute variables
   * @return the builder with the provided value
   * @see <a href="https://bitbucket.org/snakeyaml/snakeyaml-engine/wiki/Documentation#markdown-header-variable-substitution">Variable
   * substitution</a>
   */
  public LoadSettingsBuilder setEnvConfig(Optional<EnvConfig> envConfig) {
    this.envConfig = envConfig;
    return this;
  }

  public LoadSettingsBuilder setCustomProperty(SettingKey key, Object value) {
    customProperties.put(key, value);
    return this;
  }

  /**
   * Parse comments to the presentation tree (Node). False by default
   *
   * @param parseComments - use true to parse comments to the presentation tree (Node)
   * @return the builder with the provided value
   */
  public LoadSettingsBuilder setParseComments(boolean parseComments) {
    this.parseComments = parseComments;
    return this;
  }

  /**
   * Build immutable LoadSettings
   *
   * @return immutable LoadSettings
   */
  public LoadSettings build() {
    return new LoadSettings(label, tagConstructors,
        scalarResolver, defaultList,
        defaultSet, defaultMap,
        versionFunction, bufferSize,
        allowDuplicateKeys, allowRecursiveKeys, maxAliasesForCollections, useMarks,
        customProperties, envConfig, parseComments);
  }
}

