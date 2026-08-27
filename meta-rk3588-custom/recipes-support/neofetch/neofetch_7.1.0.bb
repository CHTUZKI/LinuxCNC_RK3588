SUMMARY = "A command-line system information tool"
HOMEPAGE = "https://github.com/dylanaraps/neofetch"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=d300b86297c170b6498705fbb6794e3f"

SRC_URI = "https://github.com/dylanaraps/neofetch/archive/refs/tags/${PV}.tar.gz;downloadfilename=${BP}.tar.gz"
SRC_URI[sha256sum] = "58a95e6b714e41efc804eca389a223309169b2def35e57fa934482a6b47c27e7"

S = "${WORKDIR}/neofetch-${PV}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${S}/neofetch ${D}${bindir}/neofetch
}

RDEPENDS:${PN} = "bash"
