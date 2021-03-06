***The art of simplicity is a puzzle of complexity.***

## Overview ##

[YAML](http://yaml.org) is a data serialization format designed for human readability and
interaction with scripting languages.

SnakeYAML Engine is a YAML 1.2 processor for the Java Virtual Machine version 8 and higher.

[Latest release](https://search.maven.org/search?q=a:snakeyaml-engine)

## API

* The Engine will parse/emit basic Java structures (String, List<Integer>, Map<String, Boolean>).
  JavaBeans or any other custom instances are explicitly out of scope.
* Since the custom instances are not supported, parsing any YAML document is safe - the YAML input
  stream is not able to instruct the Engine to call arbitrary Java constructors (unless it is
  explicitly enabled)
* The API is stable but might change. Feel free to review the code and propose features/changes.

## SnakeYAML Engine features ##

* a **complete** [YAML 1.2 processor](http://yaml.org/spec/1.2/spec.html). In particular, SnakeYAML
  can parse (almost) all examples from the specification.
* Integrated tests
  from [YAML Test Suite - Comprehensive Test Suite for YAML](https://github.com/yaml/yaml-test-suite)
* Unicode support including UTF-8/UTF-16/UTF-32 input/output.
* Low-level API for serializing and deserializing native Java objects.
* Only [JSON Schema](http://yaml.org/spec/1.2/spec.html#id2803231) is supported.
* The [Core Schema](http://yaml.org/spec/1.2/spec.html#id2804923) might be supported later. If
  anyone needs it. No one so far requested it.
  (A
  good [introduction to schemas](http://blogs.perl.org/users/tinita/2018/01/introduction-to-yaml-schemas-and-tags.html))
* Relatively sensible error messages (can be switched off to improve performance).

## Info ##

* [Changes](https://bitbucket.org/snakeyaml/snakeyaml-engine/wiki/Changes)
* [Documentation](https://bitbucket.org/snakeyaml/snakeyaml-engine/wiki/Documentation)

## Contribute ##

* GIT is used to dance with the [source code](https://bitbucket.org/snakeyaml/snakeyaml-engine/src).
* If you find a bug
  please [file a bug report](https://bitbucket.org/snakeyaml/snakeyaml-engine/issues?status=new&status=open).
* You may discuss SnakeYAML Engine
  at [the mailing list](http://groups.google.com/group/snakeyaml-core).
