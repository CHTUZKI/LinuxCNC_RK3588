SUMMARY = "LinuxCNC + IgH EtherCAT packages for RK3588 CNC"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit packagegroup

RDEPENDS:${PN} = " \
    linuxcnc \
    ethercat \
    linuxcnc-ethercat \
    bwidget \
    packagegroup-rk3588-cnc-tools \
    rk3588-cnc-config \
    mesa-megadriver \
    mesa-demos \
    xterm \
    synergy \
    starship \
"
