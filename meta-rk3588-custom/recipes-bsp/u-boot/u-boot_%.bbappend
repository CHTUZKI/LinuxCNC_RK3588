# Match vendor UART baud (115200). Upstream evb-rk3588 defaults to 1500000.
# Boot from eMMC rootfsA extlinux (update.img GPT: uboot/misc/rootfsA).

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " \
    file://115200.cfg \
    file://emmc-boot.cfg \
    file://0001-rockchip-prefer-mmc0-boot-targets.patch \
"
