{
  description = "UI Surveyor plugin for Idea IDE";
    inputs = {
      nixpkgs.url = "nixpkgs/release-22.11";
      gitignore = {
        url = "github:hercules-ci/gitignore.nix";
        inputs.nixpkgs.follows = "nixpkgs";
      };
    };
  outputs = { self, gitignore, nixpkgs } : {
      # TODO: use forEachSystem here
      defaultPackage.x86_64-linux =
        with import nixpkgs { system = "x86_64-linux"; };
               let
                 pluginSource = ./.;
                 customGitIgnoreFilter = src:
                   let
                     # Use a let binding like this to memoize info about the git directories.
                     # MDs are included in during the build as parts of CHANGELOG and README are copied
                     #  into the plugin manifest.
                     srcIgnored = gitignore.lib.gitignoreFilterWith { basePath = src; extraRules = ''
                       /.*
                       /ci
                       /docs
                       /*.yml
                       /*.txt
                       /prepare4nix.sh
                       flake.lock
                       *.nix
                     ''; };
                   in path: type: srcIgnored path type;
               in let
                 gradleUrls = builtins.filter
                                   (x: x != "")
                                   (lib.strings.splitString "\n" (builtins.readFile ./artifacts.lst));
                 gradleArtifacts =
                            (builtins.concatMap
                              (componentPart:
                                 (builtins.map
                                   (artifactPart:
                                     {
                                       path = (
                                          (builtins.replaceStrings ["."] ["/"] (builtins.elemAt componentPart 0)) +
                                          "/" +
                                          (builtins.elemAt componentPart 1) +
                                          "/" +
                                          (builtins.elemAt componentPart 2) +
                                          "/" +
                                          (builtins.elemAt artifactPart 0)
                                       );
                                       sha256 = builtins.elemAt artifactPart 1;
                                     }
                                   )
                                   (builtins.filter
                                     (x: builtins.isList x)
                                     (builtins.split
                                       ''<artifact[[:space:]]+name="([^"]+)">[^<]+<sha256[[:space:]]+value="([^"]+)"''
                                       (builtins.elemAt componentPart 3)
                                     )
                                   )
                                 )
                              )
                              (builtins.filter
                                (x: builtins.isList x)
                                (builtins.split
                                  ''<component[[:space:]]+group="([^"]+)"[[:space:]]+name="([^"]+)"[[:space:]]+version="([^"]+)">(([^<]+</?(artifact|sha256))+)''
                                  (builtins.readFile ./gradle/verification-metadata.xml)
                                )
                              )
                            );
               in stdenv.mkDerivation rec {
                 name = "gradleBuild";

                 gradleLines = lib.strings.splitString "\n" (builtins.readFile ./gradle/wrapper/gradle-wrapper.properties);
                 gradleDist = lib.strings.stringAsChars (x: if x == "\\" then "" else x) (
                   lib.strings.removePrefix "distributionUrl=" (lib.lists.findSingle (it: lib.strings.hasPrefix "distributionUrl=" it) (abort "Unexpected gradle-wrapper content") (abort "Unexpected gradle-wrapper content") (
                     gradleLines
                 )));
                 gradleWrapper = builtins.fetchurl {
                   url = gradleDist;
                   sha256 = "0jwycvzv8a5v2bhg5d8zccr2csr3sf9y5mrr9d2ap44p09a8r9kv";
                 };
                 gradleInit = writeText "init.gradle" ''
                   gradle.projectsLoaded {
                     rootProject.allprojects {
                       buildscript {
                         repositories {
                           clear()
                           maven { url "''${System.env.out}/m2" }
                           maven { url "''${System.env.out}/gradleDeps" }
                         }
                       }
                       repositories {
                         clear()
                         maven { url "''${System.env.out}/m2" }
                         maven { url "''${System.env.out}/gradleDeps" }
                       }
                     }
                   }
                   settingsEvaluated { settings ->
                     settings.pluginManagement {
                       repositories {
                         clear()
                         maven { url "''${System.env.out}/m2" }
                         maven { url "''${System.env.out}/gradleDeps" }
                       }
                     }
                   }
                 '';

                 src = lib.cleanSourceWith {
                   src = pluginSource;
                   filter = customGitIgnoreFilter pluginSource;
                 };
                 buildInputs = [ pkgs.autoPatchelfHook pkgs.jdk11 pkgs.perl pkgs.unzip ];
                 dontBuild = true;

                 # TODO: also build and package sources jar
                 installPhase = ''
                   set -ex

                   mkdir -p $out/gradleDeps
                   pushd $out/gradleDeps
'' + builtins.concatStringsSep
                      "\n"
                      (builtins.map
                        (url:
                          let
                            artifact = (lib.lists.findFirst
                             (artifactItem: lib.strings.hasSuffix
                               artifactItem.path
                               url
                             )
                             (abort ("Artifact is missing in gradle/verification-metadata.xml: " + url))
                             gradleArtifacts
                           );
                         in
                          "install -Dm444 ${builtins.fetchurl {url = url; sha256 = artifact.sha256;}} ./${artifact.path}"
                        )
                        gradleUrls
                      )
 + ''

                   popd

                   mkdir -p $out/build
                   cp -R ./* $out/build
                   pushd $out/build

                   perl -i -0pe 's|(distributionUrl=)|#$1|' gradle/wrapper/gradle-wrapper.properties
                   install -Dm444 ${gradleWrapper} gradle/wrapper/
                   echo "" >> gradle/wrapper/gradle-wrapper.properties
                   echo "distributionUrl=$(cd gradle/wrapper/ && ls *.zip)" >> gradle/wrapper/gradle-wrapper.properties

                   patchShebangs .

                   export JAVA_HOME=${jdk11}

                   ./gradlew -Dorg.gradle.jvmargs=-XX:MaxMetaspaceSize=1g \
                       -PNIX_GRADLE_DEPS_1=$out/gradleDeps \
                       -Dkotlin.compiler.execution.strategy="in-process" --no-daemon \
                       --no-build-cache --no-configuration-cache \
                       --info --full-stacktrace \
                       --init-script ${gradleInit} buildPlugin

                   cp -v plugin/build/distributions/*.zip $out
                   popd

                   pushd $out
                   find . -maxdepth 1 -mindepth 1 -type d -exec rm -rfv '{}' \; # remove directories only
                   popd
                 '';
               };
  };
}