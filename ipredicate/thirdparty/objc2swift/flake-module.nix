top@{ inputs, ... } : {
  flake = {
    description = "Patched objc2swift";
  };
  perSystem = { pkgs, ... }: {
    packages.objc2swift = let
       objc2swift-src = top.inputs.objc2swift-src;
      in pkgs.mkYarnPackage {
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
  };
}
