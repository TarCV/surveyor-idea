#!/bin/sh
set -exu
find /nix/store/*gradleBuild* -maxdepth 0 -exec nix store delete {} \;
