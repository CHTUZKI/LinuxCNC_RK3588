SUMMARY = "Header-only TOML config file parser and serializer for C++17"
DESCRIPTION = "TOML++ - a header-only TOML parser and serializer for modern C++."
HOMEPAGE = "https://marzer.github.io/tomlplusplus/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=90960f22c10049c117d56ed2ee5ee167"

SRC_URI = "git://github.com/marzer/tomlplusplus.git;protocol=https;branch=master"
SRCREV = "30172438cee64926dc41fdd9c11fb3ba5b2ba9de"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DBUILD_EXAMPLES=OFF"

# Header-only library
RDEPENDS:${PN}-dev = ""
BBCLASSEXTEND = "native nativesdk"
