with import <nixpkgs> {};
let
  swiftlint = stdenv.mkDerivation rec {
    pname = "swiftlint";
    version = "0.39.2";
    src = fetchurl {
      url = "https://github.com/realm/SwiftLint/releases/download/${version}/portable_swiftlint.zip";
      sha256 = "16ipl0md7jj4rbqxb7bs7f4sb2ndidr105ixvldj96d7mj0r2l0z";
    };
    nativeBuildInputs = [ unzip ];
    sourceRoot = ".";
    installPhase = ''
      mkdir -p "$out/bin"
      mv LICENSE swiftlint "$out/bin/"
    '';
  };
in mkShell {
  buildInputs = [
    git
    swiftlint
  ];
}
