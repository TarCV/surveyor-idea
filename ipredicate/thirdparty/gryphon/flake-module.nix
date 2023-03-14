/*
Copyright (c) 2024 TarCV

This file is part of UI Surveyor.
UI Surveyor is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

This file is based on SwiftPM that is covered under the following terms

Copyright (c) 2022 nixSwiftPM contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
top@{ inputs, ... } : {
  flake = { };
  perSystem = { self', inputs', pkgs, nixpkgsForSwift, flake-utils, ... }: {
    packages.gryphon =
      let
        gryphon-src = top.inputs.gryphon-src;
      in
      let
        fetchDependencies = resolvedDeps: let
          checkout = { package, repositoryURL, state, ... }:
            let
              repo = builtins.fetchGit {
                url = repositoryURL;
                rev = state.revision;
                allRefs = true;
              };
            in pkgs.runCommand "download-${package}" { } ''
              mkdir $out
              ln -s ${repo} $out/${package}
            '';
        in pkgs.symlinkJoin {
          name = "checkouts";
          paths = map checkout resolvedDeps;
        };
      in let
        linkRepos = resolvedDeps:
          let linkARepo = { package, repositoryURL, state, ... } : ''
                set -ex
                pushd .mirrors/${package}
                  pwd
                  perl -i -0pe 's#\.package\(\s*name:\s*"([^"]+?)"[-\s\w.,:/"]*(?:,\s*\.\w+\([^)]+\))*\)#\.package(name: "$1", path: "../../.mirrors/$1")#g' Package.swift || true
                  perl -i -0pe 's#\.package\(\s*url:\s*"[^"]+/([^"]+?)"[\s\S]*?\)#\.package(path: "../../.mirrors/$1")#g' Package.swift || true
                  rm Package.resolved || true
                popd
               '';
          in builtins.concatStringsSep " " (map linkARepo resolvedDeps);
      in let
        resolvedDeps = (builtins.fromJSON (builtins.readFile "${./.}/Package.resolved")).object.pins;
        swift = inputs'.nixpkgsForSwift.legacyPackages.swift;
      in inputs'.nixpkgsForSwift.legacyPackages.stdenv.mkDerivation {
              name = "gryphon";
              src = gryphon-src;
              buildInputs = [ inputs'.nixpkgsForSwift.legacyPackages.perl swift ];
              phases = [ "unpackPhase" "buildPhase" "installPhase" ];

              buildPhase = ''
                      set -ex
                      export HOME=$PWD
                      rm -rf .build && mkdir -p .build
                      rm -rf .swiftpm && mkdir -p .swiftpm
                      cp -RL ${fetchDependencies resolvedDeps} .mirrors
                      chmod -R 777 .build
                      chmod -R 777 .mirrors
                      chmod -R 777 .swiftpm
                      ${ linkRepos resolvedDeps }

                      pwd
                      ls -al
                      perl -i -0pe 's#\.package\(\s*name:\s*"([^"]+?)"[-\s\w.,:/"]*(?:,\s*\.\w+\([^)]+\))*\)#\.package(name: "$1", path: ".mirrors/$1")#g' Package.swift || true
                      perl -i -0pe 's#\.package\(\s*url:\s*"[^"]+/([^"]+?)"[\s\S]*?\)#\.package(path: ".mirrors/$1")#g' Package.swift || true

                      swift build
                      patchelf .build/debug/gryphon --add-rpath ${swift}/lib
              '';
              installPhase = ''
                set -ex
                mkdir -p $out/bin
                cp .build/debug/gryphon $out/bin/gryphon
              '';
        };
  };
}
