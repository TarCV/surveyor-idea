# Based on https://gist.github.com/adisbladis/187204cb772800489ee3dac4acdd9947
{ pkgs ? import <nixpkgs> {} }:

let

  # To use this shell.nix on NixOS your user needs to be configured as such:
  # users.extraUsers.yourUser = {
  #   subUidRanges = [{ startUid = 100000; count = 65536; }];
  #   subGidRanges = [{ startGid = 100000; count = 65536; }];
  # };

  # Starting podman service: `podman system service --time=0`
  # Starting act: `act --container-daemon-socket $XDG_RUNTIME_DIR/podman/podman.sock`

  podmanSetupScript = let
    registriesConf = pkgs.writeText "registries.conf" ''
      unqualified-search-registries = ["docker.io"]

      [[registry]]
      prefix="docker.io"
      short-name-mode="permissive"
    '';
  in pkgs.writeScript "podman-setup" ''
    #!${pkgs.runtimeShell}

    # Dont overwrite customised configuration
    if ! test -f ~/.config/containers/policy.json; then
      install -Dm555 ${pkgs.skopeo.src}/default-policy.json ~/.config/containers/policy.json
    fi

    if ! test -f ~/.config/containers/registries.conf; then
      install -Dm555 ${registriesConf} ~/.config/containers/registries.conf
    fi
  '';

  # Provides a fake "docker" binary mapping to podman
  dockerCompat = pkgs.runCommandNoCC "docker-podman-compat" {} ''
    mkdir -p $out/bin
    ln -s ${pkgs.podman}/bin/podman $out/bin/docker
  '';

in pkgs.mkShell {

  buildInputs = [
    dockerCompat
    pkgs.act # GitHub actions runner

    pkgs.podman  # Docker compat
    pkgs.runc  # Container runtime
    pkgs.conmon  # Container runtime monitor
    pkgs.skopeo  # Interact with container registry
    pkgs.slirp4netns  # User-mode networking for unprivileged namespaces
    pkgs.fuse-overlayfs  # CoW for images, much faster than default vfs
  ];

  shellHook = ''
    # Install required configuration
    ${podmanSetupScript}
    podman system service --time=0 &
    export DOCKER_HOST=unix://$XDG_RUNTIME_DIR/podman/podman.sock
  '';

}
