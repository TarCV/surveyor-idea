#!/usr/bin/env bash
IDE_CODE=PC IDE_VERSION=2023.3.2 PLUGIN_PATH="$(ls "$(pwd)"/plugin/build/distributions/*.zip)" ./gradlew :plugin-test:test "$@"
