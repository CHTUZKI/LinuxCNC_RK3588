SUMMARY = "EtherCAT-dedicated network naming for HD-RK3588-CORE CNC"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://30-ec0.link \
    file://30-ec0.network \
"

inherit allarch

do_install() {
	install -d ${D}${systemd_unitdir}/network
	install -m 0644 ${WORKDIR}/30-ec0.link ${D}${systemd_unitdir}/network/30-ec0.link
	install -m 0644 ${WORKDIR}/30-ec0.network ${D}${systemd_unitdir}/network/30-ec0.network
}

FILES:${PN} = " \
	${systemd_unitdir}/network/30-ec0.link \
	${systemd_unitdir}/network/30-ec0.network \
"

RDEPENDS:${PN} = "systemd"
