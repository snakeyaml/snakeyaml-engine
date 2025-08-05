#!/usr/bin/env bash
# it may be needed for older docker    -u `id -u`:`id -g`

docker run --rm -it               \
    -v `pwd`:/work                \
    -v ~:/my-home                 \
    -e "HOME=/my-home"            \
    -w /work                      \
    $1                            \
    ./mvnw -Dmaven.repo.local=/my-home/.m2/repository clean install site ${@:2}

