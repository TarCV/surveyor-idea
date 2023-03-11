#!/bin/sh
set -exu

./gradlew :assemble :check :plugin:instrumentCode --write-verification-metadata sha256
./gradlew :assemble :check :plugin:instrumentCode --info --refresh-dependencies | grep -P -o 'http[^,\]\s]+' > artifacts.lst
sort -u -o artifacts.lst artifacts.lst

rm ./artifacts_valid.lst || true
while IFS= read -r line
do
  echo "checking $line"
  curl --head "$line" | head -n 1 | grep -v 404 || continue
  echo "$line" >> ./artifacts_valid.lst
done <./artifacts.lst

# TODO: Add missing artefacts to verification-metadata.xml

echo 'Done updating verification metadata and artifact download URL list'
