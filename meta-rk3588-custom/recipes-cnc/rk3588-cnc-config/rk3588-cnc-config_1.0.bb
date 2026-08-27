SUMMARY = "Default LinuxCNC/IgH EtherCAT configuration for RK3588 CNC image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://99-linuxcnc-rt.conf \
    file://ethercat.xml \
    file://lcec-skeleton.hal \
    file://xfwm4.xml \
    file://linuxcnc-env.sh \
    file://20-rockchip-modesetting.conf \
    file://linuxcnc.desktop \
    file://rk3588-linuxcnc.desktop \
    file://synergy-client.service \
    file://rk3588-linuxcnc \
"

# rk3588-linuxcnc sample config directory (installed to sample-configs so
# pickconfig.tcl lists it; users copy it to ~/linuxcnc/configs/ via the GUI)
S = "${WORKDIR}"

inherit allarch systemd

SYSTEMD_SERVICE:${PN} = "synergy-client.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
	install -d ${D}${sysconfdir}/security/limits.d
	install -m 0644 ${WORKDIR}/99-linuxcnc-rt.conf \
		${D}${sysconfdir}/security/limits.d/99-linuxcnc-rt.conf

	install -d ${D}${sysconfdir}/rk3588-cnc
	install -m 0644 ${WORKDIR}/ethercat.xml ${D}${sysconfdir}/rk3588-cnc/ethercat.xml
	install -m 0644 ${WORKDIR}/lcec-skeleton.hal ${D}${sysconfdir}/rk3588-cnc/lcec-skeleton.hal

	install -d ${D}${sysconfdir}/profile.d
	install -m 0644 ${WORKDIR}/linuxcnc-env.sh ${D}${sysconfdir}/profile.d/linuxcnc-env.sh

	install -d ${D}${sysconfdir}/xdg/xfce4/xfconf/xfce-perchannel-xml
	install -m 0644 ${WORKDIR}/xfwm4.xml \
		${D}${sysconfdir}/xdg/xfce4/xfconf/xfce-perchannel-xml/xfwm4.xml

	install -d ${D}${datadir}/X11/xorg.conf.d
	install -m 0644 ${WORKDIR}/20-rockchip-modesetting.conf \
		${D}${datadir}/X11/xorg.conf.d/20-rockchip-modesetting.conf

	# 覆盖 linuxcnc 自带的 .desktop（root 登录需 RTAPI_UID≠0 等 env）
	install -d ${D}${datadir}/applications
	install -m 0644 ${WORKDIR}/linuxcnc.desktop \
		${D}${datadir}/applications/linuxcnc.desktop

	# Synergy client 开机自启（PC 端 192.168.1.2 为 server）
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${WORKDIR}/synergy-client.service \
		${D}${systemd_system_unitdir}/synergy-client.service

	# Install the rk3588-linuxcnc real-hardware sample config into the
	# LinuxCNC sample-configs directory so pickconfig.tcl lists it.
	install -d ${D}${datadir}/linuxcnc/examples/sample-configs
	cp -r ${WORKDIR}/rk3588-linuxcnc \
		${D}${datadir}/linuxcnc/examples/sample-configs/rk3588-linuxcnc
	rm -rf ${D}${datadir}/linuxcnc/examples/sample-configs/rk3588-linuxcnc/python/__pycache__

	# Also install directly into /home/root/linuxcnc/configs/ so the config
	# is ready to use on first boot (no need to run pickconfig.tcl).
	install -d ${D}/home/root/linuxcnc/configs
	cp -r ${WORKDIR}/rk3588-linuxcnc \
		${D}/home/root/linuxcnc/configs/rk3588-linuxcnc
	rm -rf ${D}/home/root/linuxcnc/configs/rk3588-linuxcnc/python/__pycache__

	# Ensure the CiA402 enable Python script is executable.
	chmod +x ${D}/home/root/linuxcnc/configs/rk3588-linuxcnc/python/cia402_enable.py
	chmod +x ${D}${datadir}/linuxcnc/examples/sample-configs/rk3588-linuxcnc/python/cia402_enable.py

	# Create nc_files directory for G-code programs (gmoccapy expects this).
	install -d ${D}/home/root/linuxcnc/nc_files

	# Desktop shortcut that launches LinuxCNC with our config directly.
	install -d ${D}/home/root/Desktop
	install -m 0755 ${WORKDIR}/rk3588-linuxcnc.desktop \
		${D}/home/root/Desktop/rk3588-linuxcnc.desktop
}

FILES:${PN} = " \
	${sysconfdir}/security/limits.d/99-linuxcnc-rt.conf \
	${sysconfdir}/rk3588-cnc/ethercat.xml \
	${sysconfdir}/rk3588-cnc/lcec-skeleton.hal \
	${sysconfdir}/profile.d/linuxcnc-env.sh \
	${sysconfdir}/xdg/xfce4/xfconf/xfce-perchannel-xml/xfwm4.xml \
	${datadir}/X11/xorg.conf.d/20-rockchip-modesetting.conf \
	${datadir}/applications/linuxcnc.desktop \
	${systemd_system_unitdir}/synergy-client.service \
	${datadir}/linuxcnc/examples/sample-configs/rk3588-linuxcnc \
	/home/root/linuxcnc/configs/rk3588-linuxcnc \
	/home/root/linuxcnc/nc_files \
	/home/root/Desktop/rk3588-linuxcnc.desktop \
"

# /home/root is not a standard Yocto install path; suppress the warning
INSANE_SKIP:${PN} += "file-rdeps"
