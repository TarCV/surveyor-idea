#!/bin/sh
set -exu

./gradlew check buildPlugin --write-verification-metadata sha256
./gradlew check buildPlugin --info --refresh-dependencies | grep -P -o 'http[^,\]\s]+' >artifacts.lst

# TODO: Only keep the last download URL for each artifact
sort -u -o artifacts.lst artifacts.lst
{
  # These dependencies are not found by the above commands:
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-reflect/1.6.10/kotlin-reflect-1.6.10.jar'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-reflect/1.6.10/kotlin-reflect-1.6.10.pom'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-script-runtime/1.9.10/kotlin-script-runtime-1.9.10.jar'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-script-runtime/1.9.10/kotlin-script-runtime-1.9.10.pom'
} >> artifacts.lst
# TODO: Remove artifacts that are missing in $HOME/.gradle
# TODO: Add missing artefacts to verification-metadata.xml (mostly POMs)

# FindSha
fs() { find "$HOME/.gradle" -name "$1*" -exec sha256sum {} \;; }
echo 'Done updating verification metadata and artifact download URL list'
