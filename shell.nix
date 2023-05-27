with import <nixpkgs> {};

(pkgs.buildFHSUserEnv {
  name = "surveyor-idea-env";
  targetPkgs = pkgs: (with pkgs;
    [
      pkgs.git
      pkgs.jdk11
      pkgs.zlib # required for JBR JVM installed by various Gradle test tasks
    ]);

  profile = ''
export JAVA_HOME="${pkgs.jdk11.home}"
export GRADLE_OPTS="-Dorg.gradle.java.home=${pkgs.jdk11.home}"
'';
}).env
