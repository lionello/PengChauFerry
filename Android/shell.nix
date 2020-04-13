with import <nixpkgs> {}:
mkShell {
  buildInputs = [
    jdk
    git
    ktlint
  ];
}
