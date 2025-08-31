## Test Suite for YAML

Engine uses [Comprehensive Test Suite for YAML](https://github.com/yaml/yaml-test-suite) for the
tests.

When your contribution implements new topic from the spec or changes the core features, it should be checked with
the latest spec tests.


- clone [YAML Test Suite](https://github.com/yaml/yaml-test-suite)
- take tag (the trunk is work in progress)

```
    git tag | grep data
    git checkout <LATEST TAG>
```

- build with flat data (remove sub-folders)

```shell
make clean data && make data-update && mv data orig && mkdir data; find orig -name === | sed 's/===//; s/orig\///' | while read d; do (set -x; cp -r orig/$d data/${d/\/0/-0}); done; rm -fr orig
```

- copy *data* folder to `src/test/resources/comprehensive-test-suite-data`

