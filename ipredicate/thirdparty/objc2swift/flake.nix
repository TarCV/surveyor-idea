{
  description = "Patched objc2swift";

  inputs = {
    nixpkgs.url = "nixpkgs/release-22.11";
    flake-utils.url = "github:numtide/flake-utils";
    objc2swift-src = {
     url = "github:okaxaki/objc2swift/ee82a541de19e075f197abf307082a19bdfac7c6";
     flake = false;
    };
  };

  outputs = { self, nixpkgs, flake-utils, objc2swift-src }:
  (flake-utils.lib.eachDefaultSystem
    (system:
      let pkgs = import nixpkgs { inherit system; }; in rec {
        packages.objc2swift = pkgs.mkYarnPackage {
          name = "objc2swift";
          src = pkgs.stdenv.mkDerivation {
              name = "objc2swift-patched";
              src = [ objc2swift-src ];
              dontBuild = true;
              buildInputs = [ pkgs.perl ];
              installPhase = ''
              mkdir -p $out && \
              cp -R $src/* $out && \
              chmod u+w $out/bin/cmd.js && \
              perl -0pe "s|process\.platform\s*!=\s*'darwin'|false|g" $src/bin/cmd.js > $out/bin/cmd.js && \
              ! grep 'darwin' <$out/bin/cmd.js
              '';
          };
          packageJSON = "${objc2swift-src}/package.json";
          yarnLock = ./yarn.lock;
        };

        defaultPackage = packages.objc2swift;
      }
    )
  );
}
