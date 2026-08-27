SUMMARY = "Tool Command Language ToolKit Extension"
HOMEPAGE = "http://tcl.sourceforge.net"
SECTION = "devel/tcltk"
LICENSE = "TCL"
LIC_FILES_CHKSUM = "file://${S}/../license.terms;md5=c88f99decec11afa967ad33d314f87fe"

# Match poky tcl 8.6.13 for LinuxCNC Axis.
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

DEPENDS = "tcl virtual/libx11 libxt"

# SourceForge tarball for 8.6.13 is unreliable from CN mirrors; use upstream git.
SRC_URI = "git://github.com/tcltk/tk.git;protocol=https;branch=core-8-6-branch \
           file://confsearch.diff;striplevel=2 \
           file://tkprivate.diff;striplevel=2 \
           file://fix-xft.diff \
"
SRCREV = "0540bb61e34516730b6e0f8d44b39589d6c71f20"

S = "${WORKDIR}/git/unix"

VER = "${@os.path.splitext(d.getVar('PV'))[0]}"

LDFLAGS += "-Wl,-rpath,${libdir}/tcltk/${PV}/lib"
inherit autotools features_check pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

EXTRA_OECONF = "\
    --enable-threads \
    --with-x \
    --with-tcl=${STAGING_BINDIR}/crossscripts \
    --libdir=${libdir} \
"
export TK_LIBRARY='${libdir}/tk${VER}'

# Skip man-page/doc targets; LinuxCNC only needs libtk + wish.
do_compile() {
	oe_runmake -C ${B} binaries
}

do_install:append() {
    ln -sf libtk${VER}.so ${D}${libdir}/libtk${VER}.so.0
    oe_libinstall -so libtk${VER} ${D}${libdir}
    ln -sf wish${VER} ${D}${bindir}/wish

    sed -i "s;-L${B};-L${STAGING_LIBDIR};g" tkConfig.sh
    sed -i "s;'${WORKDIR};'${STAGING_INCDIR};g" tkConfig.sh
    install -d ${D}${bindir_crossscripts}
    install -m 0755 tkConfig.sh ${D}${bindir_crossscripts}
}

PACKAGECONFIG ??= "xft"
PACKAGECONFIG[xft] = "--enable-xft,--disable-xft,xft"
PACKAGECONFIG[xss] = "--enable-xss,--disable-xss,libxscrnsaver libxext"

PACKAGES =+ "${PN}-lib"

FILES:${PN}-lib = "${libdir}/libtk${VER}.so*"
FILES:${PN} += "${libdir}/tk*"

RDEPENDS:${PN} += "tk-lib"

BBCLASSEXTEND = "native nativesdk"

SSTATE_SCAN_FILES += "*Config.sh"

inherit binconfig

SYSROOT_DIRS += "${bindir_crossscripts}"
BINCONFIG_GLOB = "*Config.sh"

PACKAGE_PREPROCESS_FUNCS += "tcl_package_preprocess"
tcl_package_preprocess() {
    sed -i -e "s;${DEBUG_PREFIX_MAP};;g" \
           -e "s;-L${STAGING_LIBDIR};-L${libdir};g" \
           -e "s;${STAGING_INCDIR};${includedir};g" \
           -e "s;--sysroot=${RECIPE_SYSROOT};;g" \
           ${PKGD}${libdir}/tkConfig.sh

    rm -f ${PKGD}${bindir_crossscripts}/tkConfig.sh
}
