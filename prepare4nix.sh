#!/bin/sh
set -exu

# Gathering URLs should be done when no metadata exists (and thus before writing metadata too),
# otherwise verification error might interfere with the gathering process
mv ./gradle/verification-metadata.xml ./gradle/verification-metadata.xml.old || true
./gradlew check buildPlugin -x :plugin-test:test --info --refresh-dependencies | grep -v 'Resource missing' | grep -P -o 'https?://[^,\]\s]+' >artifacts.lst
mv ./gradle/verification-metadata.xml.old ./gradle/verification-metadata.xml || true

./gradlew check buildPlugin -x :plugin-test:test --write-verification-metadata sha256

# Remove non-Maven URLs
mv -f artifacts.lst artifacts.old && grep -v 'https://cache-redirector.jetbrains.com/intellij-jbr/' artifacts.old > artifacts.lst
mv -f artifacts.lst artifacts.old && grep -v 'https://docs.gradle.org/' artifacts.old > artifacts.lst
rm artifacts.old

sort -u -o artifacts.lst artifacts.lst

nix eval --impure --raw .#generateVerificationMetadata > artifacts.xml

# FindSha
fs() { find "$HOME/.gradle" -name "$1*" -exec sha256sum {} \;; }
echo 'Done updating verification metadata and artifact download URL list'
