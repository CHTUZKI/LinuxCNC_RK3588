SUMMARY = "Yet Another Python Parser System (native)"
DESCRIPTION = "Build-time parser generator required by LinuxCNC halcompile."
HOMEPAGE = "https://github.com/david-beazley/yapps"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${S}/LICENSE;md5=75db4afbd75feb0b856fb4fade51aad2"

SRC_URI = "https://deb.debian.org/debian/pool/main/y/yapps2/yapps2_${PV}.orig.tar.gz"
SRC_URI[sha256sum] = "3f46dbef0d9b067a00dced333c1b2c09d78963e0dd14872a39715889b7228f73"

S = "${WORKDIR}/yapps2-${PV}"

inherit python3native setuptools3 native

RDEPENDS:${PN} = "python3-core-native"

do_install:append() {
	# LinuxCNC configure 查找 yapps 或 yapps2
	ln -sf yapps2 ${D}${bindir}/yapps
}
