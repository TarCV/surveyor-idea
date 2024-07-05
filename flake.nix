{
  description = "UI Surveyor plugin for Idea IDE";
    inputs = {
      nixpkgs = {
        url = "nixpkgs/release-23.11";
      };
      nixpkgsForSwift = {
        url = "nixpkgs/007ccf2f4f1da567903ae392cbf19966eb30cf20";
      };
      gitignore = {
        url = "github:hercules-ci/gitignore.nix";
        inputs.nixpkgs.follows = "nixpkgs";
      };
      ipredicateKt.url = "path:./ipredicate";
    flake-parts.url = "github:hercules-ci/flake-parts";

    # For ipredicate:
    gnuStepBaseSrc = {
      url = "github:gnustep/libs-base/cc38f2f4a1ce2d3f0a5f478ead595d2b011ecf41";
      flake = false;
    };
    webDriverAgentSrc = {
      url = "github:appium/WebDriverAgent/v3.16.0";
      flake = false;
    };

    # For thirdparty/objc2swift:
    objc2swift-src = {
     url = "github:okaxaki/objc2swift/ee82a541de19e075f197abf307082a19bdfac7c6";
     flake = false;
    };

    # For thirdparty/gryphon:
    gryphon-src = {
     url = "github:vinivendra/Gryphon/v0.18.1";
     flake = false;
    };
  };
  outputs = inputs@{ flake-parts, gitignore, ... }:
    flake-parts.lib.mkFlake { inherit inputs; } {
          imports = [
            # To import a flake module
            # 1. Add foo to inputs
            # 2. Add foo as a parameter to the outputs function
            # 3. Add here: foo.flakeModule
            ./ipredicate/flake-module.nix
          ];
          debug = true;
          systems = [ "x86_64-linux" ]; # TODO
          flake = {
            # TODO: extract to a helper instead of copy-paste
            generateVerificationMetadata = let
                lib = inputs.nixpkgs.lib;
              in let
              gradleUrls = builtins.filter
                  (x: x != "")
                  (lib.strings.splitString "\n" (builtins.readFile ./artifacts.lst));
              gradleArtifacts =
                            (builtins.concatMap
                              (componentPart:
                                 (builtins.map
                                   (artifactPart:
                                     rec {
                                       group = builtins.elemAt componentPart 0;
                                       name = builtins.elemAt componentPart 1;
                                       version = builtins.elemAt componentPart 2;
                                       file = builtins.elemAt artifactPart 0;
                                       path = (
                                          (builtins.replaceStrings ["."] ["/"] group) +
                                          "/" +
                                          name +
                                          "/" +
                                          version +
                                          "/" +
                                          file
                                       );
                                       sha256 = builtins.elemAt artifactPart 1;
                                       origin = builtins.elemAt artifactPart 2;
                                     }
                                   )
                                   (builtins.filter
                                     (x: builtins.isList x)
                                     (builtins.split
                                       ''<artifact[[:space:]]+name="([^"]+)">[^<]+<sha256[[:space:]]+value="([^"]+)"[[:space:]]+origin="([^"]+)"''
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
              in let
                  # TODO: Sort in the same way Gradle does
                  # TODO: Copy okio-2.4.3.jar metadata as okio-jvm-2.4.3.jar metadata
                  # TODO: Calculate hashes for libraries extracted for IDE zips
                  verificationList = (builtins.map
                        (artifacts: let
                            firstArtifact = builtins.elemAt artifacts 0;
                            artifactString = builtins.concatStringsSep
                              "\n"
                              (builtins.map
                                (artifact:
                                  ''
                                  ${""}         <artifact name="${artifact.file}">
                                  ${""}            <sha256 value="${artifact.sha256}" origin="${artifact.origin}"/>
                                  ${""}         </artifact>''
                                )
                                artifacts
                              );
                          in  # This set should match function in first argument of the groupBy below
                            ''
                            ${""}      <component group="${firstArtifact.group}" name="${firstArtifact.name}" version="${firstArtifact.version}">
                            ${artifactString}
                            ${""}      </component>''
                        )
                        (builtins.sort
                          (l1: l2: let # lessThan
                              e1 = builtins.elemAt l1 0;
                              e2 = builtins.elemAt l2 0;
                            in if e1.group == e2.group then
                              if e1.name == e2.name then
                                builtins.lessThan e1.version e2.version
                              else
                                builtins.lessThan e1.name e2.name
                            else
                              builtins.lessThan e1.group e2.group
                          )
                          (builtins.attrValues (builtins.groupBy
                            (artifact: artifact.group + ":" + artifact.name + ":" + artifact.version)
                            (builtins.map # TODO: Remove duplicate artifacts from the list
                                             (url:
                                               (lib.lists.findFirst
                                                  (artifactItem: lib.strings.hasSuffix
                                                    artifactItem.path
                                                    url
                                                  )
                                                  (let
                                                    knownRepos = [
                                                      "https://dl.google.com/dl/android/maven2/"
                                                      "https://jitpack.io/"
                                                      "https://plugins.gradle.org/m2/"
                                                      "https://repo1.maven.org/maven2/"
                                                      "https://repo.maven.apache.org/maven2/"
                                                      "https://cache-redirector.jetbrains.com/www.jetbrains.com/intellij-repository/releases/"
                                                    ];
                                                    artiPath = builtins.fetchurl url;
                                                    urlParts = builtins.elemAt
                                                      (builtins.split "^(.+)/([^/]+)/([^/]+)/([^/]+)$" url)
                                                      1;
                                                  in let
                                                    groupPath = (builtins.elemAt urlParts 0);
                                                  in let
                                                    artifactRepoBaseUrl = lib.lists.findFirst
                                                      (repository: lib.strings.hasPrefix
                                                        repository
                                                        groupPath
                                                      )
                                                      (abort ("Unknown repository is used in the path: " + groupPath))
                                                      knownRepos;
                                                  in let
                                                    repoRelativePath = (lib.strings.removePrefix
                                                      artifactRepoBaseUrl
                                                      groupPath
                                                    );
                                                  in {
                                                    sha256 = builtins.hashFile "sha256" artiPath;
                                                    path = artiPath;
                                                    group = builtins.replaceStrings ["/"] ["."] repoRelativePath;
                                                    name = builtins.elemAt urlParts 1;
                                                    version = builtins.elemAt urlParts 2;
                                                    file = builtins.elemAt urlParts 3;
                                                    origin = "Generated by flake.nix";
                                                  })
                                                  gradleArtifacts
                                               )
                                             )
                                             gradleUrls
                            )
                          ))
                        )
                    );
             in ''
             ${""}   <components>
             ${builtins.concatStringsSep "\n" verificationList}
             ${""}   </components>'';
          };
          perSystem = { config, self', inputs', pkgs, lib, ... }: {
             packages.default = let
                stdenv = pkgs.stdenv;
                ipredicateKt = self'.packages.ipredicateKt;
               in
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
                 gradleSha256 = lib.strings.stringAsChars (x: if x == "\\" then "" else x) (
                   lib.strings.removePrefix "distributionSha256Sum=" (lib.lists.findSingle (it: lib.strings.hasPrefix "distributionSha256Sum=" it) (abort "Unexpected gradle-wrapper content") (abort "Unexpected gradle-wrapper content") (
                     gradleLines
                 )));
                 gradleWrapper = builtins.fetchurl {
                   url = gradleDist;
                   sha256 = gradleSha256;
                 };
                 gradleInit = pkgs.writeText "init.gradle" ''
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
                 buildInputs = [
                   pkgs.autoPatchelfHook
                   pkgs.jdk17
                   pkgs.perl
                   pkgs.unzip

                   ipredicateKt
                 ];
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

                   install -Dm444 ${ipredicateKt}/* ipredicate/project/src/main/kotlin/com/github/tarcv/testingteam/surveyor/ipredicate/

                   export JAVA_HOME=${pkgs.jdk17.home}

                   # Use '--debug' to debug dependency downloads by plugins
                   ./gradlew -Dorg.gradle.jvmargs=-XX:MaxMetaspaceSize=1g \
                       -PNIX_GRADLE_DEPS_1=$out/gradleDeps \
                       --no-build-cache --no-configuration-cache \
                       --init-script ${gradleInit} check buildPlugin -x :plugin-test:test

                   cp -v plugin/build/distributions/*.zip $out
                   cp ${ipredicateKt}/* $out
                   popd

                   pushd $out
                   find . -maxdepth 1 -mindepth 1 -type d -exec rm -rfv '{}' \; # remove directories only
                   popd
                 '';
               };
     };
  };
}
