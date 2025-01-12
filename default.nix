{ pkgs ? import <nixpkgs-unstable> {} }:

pkgs.mkShell {
  packages = [
    pkgs.graalvmPackages.graalvm-oracle
  ];
}
