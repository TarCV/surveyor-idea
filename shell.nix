with import <nixpkgs> {};

(pkgs.buildFHSUserEnv {
  name = "surveyor-idea-env";
  targetPkgs = pkgs: (with pkgs;
    [
      pkgs.git
      pkgs.jdk11

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
export GRADLE_OPTS="-Dorg.gradle.java.home=${pkgs.jdk11.home}"

# Write out a configuration for EnvFile Idea Plugin.
cat <<EOF > .env
JAVA11_HOME="${pkgs.jdk11.home}"
EOF

./gradlew --stop # Make sure there is no leftover daemons running outside of this env
./gradlew help || true
'';
}).env
