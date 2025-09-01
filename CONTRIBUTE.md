# Contribute to SnakeYAML Engine

## Architecture

Tons of useful information can be found on [the official site](https://yaml.org/spec/1.2.2/).

General steps are defined [in the spec](https://yaml.org/spec/1.2.2/#31-processes):

![](https://yaml.org/spec/1.2.2/img/overview2.svg)

Loading has the following explicit steps (please note that Events form a Stream, not a Tree)

![](doc/YAML-streams.drawio.png)

Composer delivers also Stream of Nodes (because there may be more than one document in the YAML
stream, but for simplification the diagram mentions only one Node graph)

## Testing

Engine uses [Comprehensive Test Suite for YAML](TestSuiteForYAML.md)
for the tests.

### Parser playground

You can use the [online playground](https://play.yaml.io/main/parser?input=LSAiXAkiCg==) to test the parser.
It shows how all the parsers treat the input. (You may need to start Local Docker Sandbox Server)
The input is encoded in Base64 and provided as a query parameter.

### Test locally

    ./mvnw clean verify

### Run tests in docker

Run the tests and fix the errors:

    ./docker-run-jdk11.sh
