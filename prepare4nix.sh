#!/bin/sh
set -exu

./gradlew check buildPlugin -x :plugin:test --write-verification-metadata sha256
./gradlew check buildPlugin -x :plugin:test --info --refresh-dependencies | grep -P -o 'http[^,\]\s]+' | grep -v 'Resource missing' >artifacts.lst

# TODO: Only keep the last download URL for each artifact

# Remove artifacts that are not needed for 'nix build'
mv -f artifacts.lst artifacts.old && grep -v '/com/fasterxml/jackson/jackson-base/2.14.2/jackson-base-2.14.2.pom' artifacts.old > artifacts.lst
mv -f artifacts.lst artifacts.old && grep -v '/com/fasterxml/jackson/jackson-bom/2.14.2/jackson-bom-2.14.2.pom' artifacts.old > artifacts.lst
mv -f artifacts.lst artifacts.old && grep -v '/com/fasterxml/jackson/jackson-parent/2.14/jackson-parent-2.14.pom' artifacts.old > artifacts.lst
mv -f artifacts.lst artifacts.old && grep -v '/com/fasterxml/jackson/module/jackson-module-kotlin/2.14.2/jackson-module-kotlin-2.14.2.pom' artifacts.old > artifacts.lst
mv -f artifacts.lst artifacts.old && grep -v '/com/fasterxml/oss-parent/48/oss-parent-48.pom' artifacts.old > artifacts.lst
mv -f artifacts.lst artifacts.old && grep -v '/org/junit/junit-bom/5.7.2/junit-bom-5.7.2.module' artifacts.old > artifacts.lst
mv -f artifacts.lst artifacts.old && grep -v '/org/junit/junit-bom/5.9.1/junit-bom-5.9.1.module' artifacts.old > artifacts.lst
mv -f artifacts.lst artifacts.old && grep -v '/org/junit/junit-bom/5.9.2/junit-bom-5.9.2.module' artifacts.old > artifacts.lst
rm artifacts.old

{
  # These dependencies are not found by the above commands:
  echo 'https://repo.maven.apache.org/maven2/com/jgoodies/forms/1.1-preview/forms-1.1-preview.jar'
  echo 'https://repo.maven.apache.org/maven2/com/jgoodies/forms/1.1-preview/forms-1.1-preview.pom'
  echo 'https://repo1.maven.org/maven2/org/jetbrains/annotations/20.1.0/annotations-20.1.0.jar'
  echo 'https://repo1.maven.org/maven2/org/jetbrains/annotations/20.1.0/annotations-20.1.0.pom'
  echo 'https://repo1.maven.org/maven2/org/jetbrains/annotations/24.0.0/annotations-24.0.0.jar'
  echo 'https://repo1.maven.org/maven2/org/jetbrains/annotations/24.0.0/annotations-24.0.0.pom'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-reflect/1.6.10/kotlin-reflect-1.6.10.jar'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-reflect/1.6.10/kotlin-reflect-1.6.10.pom'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-script-runtime/1.9.10/kotlin-script-runtime-1.9.10.jar'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-script-runtime/1.9.10/kotlin-script-runtime-1.9.10.pom'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-stdlib/1.9.10/kotlin-stdlib-1.9.10.jar'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-stdlib/1.9.10/kotlin-stdlib-1.9.10.pom'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-stdlib-common/1.9.10/kotlin-stdlib-common-1.9.10.jar'
  echo 'https://plugins.gradle.org/m2/org/jetbrains/kotlin/kotlin-stdlib-common/1.9.10/kotlin-stdlib-common-1.9.10.pom'
  echo 'https://jitpack.io/com/github/TarCV/beanshell/beanshell/5e2cc2f240eaec2d7d9c9b745aa3a5d04bc8fa2c/beanshell-5e2cc2f240eaec2d7d9c9b745aa3a5d04bc8fa2c.pom'
  echo 'https://jitpack.io/com/github/TarCV/beanshell/bsh/5e2cc2f240eaec2d7d9c9b745aa3a5d04bc8fa2c/bsh-5e2cc2f240eaec2d7d9c9b745aa3a5d04bc8fa2c.jar'
  echo 'https://jitpack.io/com/github/TarCV/beanshell/bsh/5e2cc2f240eaec2d7d9c9b745aa3a5d04bc8fa2c/bsh-5e2cc2f240eaec2d7d9c9b745aa3a5d04bc8fa2c.pom'
  echo 'https://dl.google.com/dl/android/maven2/androidx/test/uiautomator/uiautomator/2.2.0/uiautomator-2.2.0.aar'
  echo 'https://dl.google.com/dl/android/maven2/androidx/test/uiautomator/uiautomator/2.2.0/uiautomator-2.2.0.pom'
} >> artifacts.lst

sort -u -o artifacts.lst artifacts.lst

# TODO: Remove artifacts that are missing in $HOME/.gradle
# TODO: Add missing artefacts to verification-metadata.xml (mostly POMs)

# FindSha
fs() { find "$HOME/.gradle" -name "$1*" -exec sha256sum {} \;; }
echo 'Done updating verification metadata and artifact download URL list'
