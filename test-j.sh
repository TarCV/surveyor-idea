#!/usr/bin/env bash
IDE_CODE=IC IDE_VERSION=2022.3.3 PLUGIN_PATH="$(ls "$(pwd)"/plugin/build/distributions/*.zip)"  ./gradlew :plugin-test:test "$@"
