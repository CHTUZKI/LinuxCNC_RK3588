SUMMARY = "System font configuration: set JetBrainsMono Nerd Font as default monospace"
DESCRIPTION = "Configures xfce xsettings and gsettings to use JetBrainsMono Nerd Font \
as the system-wide monospace font, ensuring xfce4-terminal renders Powerline glyphs."

# This is a configuration package, no sources needed
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Set system monospace font via xfconf
# This runs at first boot via a systemd service
inherit systemd

SRC_URI = "file://set-monospace-font.service \
    file://set-monospace-font.sh \
"

SYSTEMD_SERVICE:${PN} = "set-monospace-font.service"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/set-monospace-font.sh ${D}${bindir}/set-monospace-font

    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/set-monospace-font.service ${D}${systemd_unitdir}/system/
}

FILES:${PN} = "${bindir}/set-monospace-font ${systemd_unitdir}/system/set-monospace-font.service"

RDEPENDS:${PN} = "jetbrainsmono-nerd-font xfconf glib-2.0-utils dconf"

pkg_postinst:${PN}() {
    # Also set during rootfs creation so it's ready for first boot
    if [ -n "$D" ]; then
        exit 0
    fi
}
