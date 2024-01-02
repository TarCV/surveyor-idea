{
  pkgs ? import <nixpkgs> {
    overlays = [
       (self: super:
         let
           jextr21Pkgs = import (builtins.fetchTarball {
             name = "jextr21Pkgs";
             url = "https://github.com/NixOS/nixpkgs/archive/224b3a5ad9a960e4a6e3cd59233c1616164c5ef5.tar.gz";
          #        # Use nix-prefetch-url --unpack to generate:
             sha256 = "0bvxl07vgv1r6y7jd4yga0l6pps3h5zmniyjasq9rkrnsbmpxmn5";
           }) {};
         in
         {
          jextract = jextr21Pkgs.jextract;
         }
      )
    ];
  }
}:
(pkgs.buildFHSUserEnv {
  name = "surveyor-idea-env";
  targetPkgs = pkgs: (with pkgs;
    [
      pkgs.git
      pkgs.jdk11
      pkgs.jdk21
      pkgs.jextract
      pkgs.icu70
      pkgs.icu70.dev

      # required for JBR JVM installed by various Gradle test tasks
      # From https://github.com/NixOS/nixpkgs/blob/master/pkgs/development/compilers/openjdk/11.nix
      pkgs.freetype
      pkgs.harfbuzz
      pkgs.alsa-lib
      pkgs.libjpeg
      pkgs.giflib
      pkgs.libpng
      pkgs.zlib
      pkgs.lcms2
      pkgs.xorg.libX11
      pkgs.xorg.libICE
      pkgs.xorg.libXrender
      pkgs.xorg.libXext
      pkgs.xorg.libXt
      pkgs.xorg.libXtst
      pkgs.xorg.libXi
      pkgs.xorg.libXinerama
      pkgs.xorg.libXcursor
      pkgs.xorg.libXrandr
      pkgs.fontconfig
    ]);

  profile = ''
export JAVA_HOME="${pkgs.jdk11.home}"
export JAVA21_HOME="${pkgs.jdk21.home}"
export ICU_HOME="${pkgs.icu70}"
export ICU_INCLUDE="${pkgs.icu70.dev}"
export GRADLE_OPTS="-Dorg.gradle.java.home=${pkgs.jdk11.home}"
'';
# Regenerate icu bindings with:
# jextract --source -t icu -I$ICU_INCLUDE/include $ICU_INCLUDE/include/unicode/uregex.h
}).env
