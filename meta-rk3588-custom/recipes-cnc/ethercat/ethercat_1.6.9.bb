SUMMARY = "IgH EtherCAT Master (linuxcnc-ethercat fork) — kernel module + userspace tools"
DESCRIPTION = "EtherLab EtherCAT master with LinuxCNC-specific fixes. Builds ec_master.ko \
kernel module (generic driver) and the 'ethercat' command-line tool."
HOMEPAGE = "https://github.com/linuxcnc-ethercat/ethercat"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
    file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c \
"

SRC_URI = " \
    git://github.com/linuxcnc-ethercat/ethercat.git;protocol=https;branch=dev-1.6 \
    file://ethercat.conf \
    file://ethercat.init \
    file://ethercat.service \
    file://ethercat-udev.rules \
"

# dev-1.6 tip (2025-05-05): 1.6.9+parallelop1~pre8
SRCREV = "4f9aa6f33c6706ff743b07e978c2ad306599d30f"

S = "${WORKDIR}/git"

# Kernel module build needs the shared kernel workdir
do_configure[depends] += "virtual/kernel:do_shared_workdir"

inherit autotools-brokensep pkgconfig module-base systemd

EXTRA_OECONF += " \
    --with-linux-dir=${STAGING_KERNEL_BUILDDIR} \
    --with-module-dir=kernel/ethercat \
    --enable-generic \
    --disable-8139too \
    --disable-e100 \
    --disable-e1000 \
    --disable-e1000e \
    --disable-r8169 \
    --disable-eoe \
"

# IgH autotools: need ChangeLog to exist for automake
do_configure:prepend() {
    touch ${S}/ChangeLog
}

# Build userspace first (default), then kernel modules
do_compile:append() {
    oe_runmake modules
}

do_install:append() {
    # Install kernel modules
    oe_runmake MODLIB=${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION} modules_install

    # Remove example mini module (not needed in production)
    rm -f ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/ethercat/examples/mini/ec_mini.ko
    rm -rf ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/ethercat/examples

    # Remove upstream systemd/sysconfig (we provide our own)
    rm -rf ${D}${libdir}/systemd
    rm -rf ${D}${sysconfdir}/sysconfig
    rm -f ${D}${sysconfdir}/ethercat.conf

    # Install our config + init + systemd service
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/ethercat.conf ${D}${sysconfdir}/ethercat.conf

    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/ethercat.init ${D}${sysconfdir}/init.d/ethercat

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/ethercat.service ${D}${systemd_system_unitdir}/ethercat.service

    # udev rule: make /dev/EtherCAT0 world-readable/writable so LinuxCNC
    # (running as non-root via RTAPI_UID) can access it.
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${WORKDIR}/ethercat-udev.rules \
        ${D}${sysconfdir}/udev/rules.d/99-ethercat.rules
}

# Kernel modules live under .../kernel/ethercat/ (IgH installs to kernel/ subdir)
FILES:${PN} += " \
    ${nonarch_base_libdir}/modules/${KERNEL_VERSION}/kernel/ethercat \
    ${sysconfdir}/ethercat.conf \
    ${sysconfdir}/init.d/ethercat \
    ${sysconfdir}/udev/rules.d/99-ethercat.rules \
    ${systemd_system_unitdir}/ethercat.service \
    ${datadir}/bash-completion \
"

# Userspace libethercat for linking (linuxcnc-ethercat needs -lethercat)
FILES:${PN}-dev += " \
    ${includedir}/ethercat \
    ${libdir}/libethercat.so \
    ${libdir}/libethercat.la \
"
FILES:${PN}-staticdev += "${libdir}/libethercat.a"

# Kernel modules (ec_master.ko, ec_generic.ko) are included in ${PN}.
# The image already installs kernel-modules which provides the kernel itself.

SYSTEMD_SERVICE:${PN} = "ethercat.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "dev-so"
