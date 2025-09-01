***The art of simplicity is a puzzle of complexity.***

## Overview ##

[YAML](http://yaml.org) is a data serialization format designed for human readability and
interaction with scripting languages.

SnakeYAML Engine is a YAML 1.2 processor for the Java Virtual Machine version 8 and higher.

[Latest release](https://central.sonatype.com/artifact/org.snakeyaml/snakeyaml-engine)

## API

* The Engine will parse/emit basic Java structures (String, List<Integer>, Map<String, Boolean>).
  JavaBeans or any other custom instances are explicitly out of scope.
* Since the custom instances are not supported, parsing any YAML document is safe - the YAML input
  stream is not able to instruct the Engine to call arbitrary Java constructors (unless it is
  explicitly enabled)

## SnakeYAML Engine features ##

* a **complete** [YAML 1.2 processor](https://yaml.org/spec/1.2.2/). In particular, SnakeYAML
  can parse (almost) all examples from the specification.
* Integrated tests
  from [YAML Test Suite - Comprehensive Test Suite for YAML](https://github.com/yaml/yaml-test-suite)
* Unicode support including UTF-8/UTF-16/UTF-32 input/output.
* Low-level API for serializing and deserializing native Java objects.
* All the [Schemas](https://yaml.org/spec/1.2.2/#chapter-10-recommended-schemas) are supported.
  (A
  good [introduction to schemas](http://blogs.perl.org/users/tinita/2018/01/introduction-to-yaml-schemas-and-tags.html))
* Relatively sensible error messages (can be switched off to improve performance).
* When you plan to feed the parser with untrusted data please study the settings which allow to restrict incoming data.

## Info ##

* [Changes](https://bitbucket.org/snakeyaml/snakeyaml-engine/wiki/Changes)
* [Documentation](https://bitbucket.org/snakeyaml/snakeyaml-engine/wiki/Documentation)
* [CVE and untrusted sources](https://bitbucket.org/snakeyaml/snakeyaml/wiki/CVE%20&%20NIST.md)

## Contribute ##

* GIT is used to dance with the [source code](https://bitbucket.org/snakeyaml/snakeyaml-engine/src).
* If you find a bug
  please [file a bug report](https://bitbucket.org/snakeyaml/snakeyaml-engine/issues?status=new&status=open).
* You may discuss SnakeYAML Engine
  at [the mailing list](http://groups.google.com/group/snakeyaml-core).

## JMH microbenchmarks ##

To execute the [JMH](https://github.com/openjdk/jmh) microbenchmarks locally via Maven:

```shell
./mvnw jmh:benchmark
```
This will produce console results such as the following, as well as a `./jmh-result.json` that can be
visualized via https://jmh.morethan.io/ .

```text
Benchmark                    (entries)  Mode  Cnt    Score     Error  Units
EmitterBenchmark.emitScalar        N/A  avgt    3    0.299 ±   0.047  us/op
ParseBenchmark.load               1000  avgt    3    1.388 ±   0.103  ms/op
ParseBenchmark.load             100000  avgt    3  258.281 ± 367.009  ms/op
ParseBenchmark.parse              1000  avgt    3    0.886 ±   0.163  ms/op
ParseBenchmark.parse            100000  avgt    3   94.930 ±   3.995  ms/op
```
