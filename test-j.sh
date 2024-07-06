#!/usr/bin/env -S bash -e
rm --verbose "$(pwd)"/plugin/build/distributions/* || true
rm --verbose "$(pwd)"/plugin-test/screenshot/* || true
rm --verbose "$(pwd)"/plugin-test/video/* || true
./gradlew :plugin:buildPlugin
IDE_CODE=IC IDE_VERSION=2023.3.7 PLUGIN_PATH="$(ls "$(pwd)"/plugin/build/distributions/*.zip)" ./gradlew :plugin-test:test "$@"
