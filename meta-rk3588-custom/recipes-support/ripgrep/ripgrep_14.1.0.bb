SUMMARY = "ripgrep (rg) — recursively search directories with regex"
HOMEPAGE = "https://github.com/BurntSushi/ripgrep"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE-MIT;md5=8d0d0aa488af0ab9aafa3b85a7fc8e12"

# Prebuilt aarch64 binary — avoids a long Rust bootstrap on the build host.
COMPATIBLE_HOST = "aarch64.*-linux"

SRC_URI = "https://github.com/BurntSushi/ripgrep/releases/download/${PV}/ripgrep-${PV}-aarch64-unknown-linux-gnu.tar.gz"
SRC_URI[sha256sum] = "c8c210b99844fbf16b7a36d1c963e8351bca5ff2dd7c788f5fba4ac18ba8c60d"

S = "${WORKDIR}/ripgrep-${PV}-aarch64-unknown-linux-gnu"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/rg ${D}${bindir}/rg
}

INSANE_SKIP:${PN} += "already-stripped ldflags"
