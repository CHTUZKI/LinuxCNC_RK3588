SUMMARY = "BWidget extension for Tcl/Tk"
HOMEPAGE = "https://github.com/tcltk/bwidget"
LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "file://${S}/LICENSE.txt;md5=af21afb4e406f3d8e15b91dd3fa0a978"

SRC_URI = "https://deb.debian.org/debian/pool/main/b/bwidget/bwidget_${PV}.orig.tar.xz"
SRC_URI[sha256sum] = "3afd1ea5f8a4c835da2d1a1a22eb8350e0556e58f2d041b48bfcd4a7a11531de"

inherit allarch

S = "${WORKDIR}/bwidget-${PV}"

do_install() {
	install -d ${D}${libdir}/tcl8.6/bwidget
	install -m 0644 ${S}/*.tcl ${D}${libdir}/tcl8.6/bwidget/

	install -d ${D}${libdir}/tcl8.6/bwidget/images
	install -m 0644 ${S}/images/* ${D}${libdir}/tcl8.6/bwidget/images/
}

FILES:${PN} = "${libdir}/tcl8.6/bwidget"

RDEPENDS:${PN} = "tcl tk"
