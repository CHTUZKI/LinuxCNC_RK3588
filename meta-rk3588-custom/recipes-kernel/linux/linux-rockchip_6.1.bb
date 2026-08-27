# Rockchip vendor kernel (develop-6.1) for HD-RK3588-CORE CNC image.
# LinuxCNC 分支：PREEMPT_RT + Mali GPU + 关 NPU（见 hd-rk3588-core-cnc-overlay.dtsi）。

SUMMARY = "Rockchip Linux kernel (official develop-6.1)"
SECTION = "kernel"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel

COMPATIBLE_MACHINE = "^hd-rk3588-core-cnc"

LINUX_VERSION = "6.1.141"
PV = "${LINUX_VERSION}+git${SRCPV}"
LINUX_VERSION_EXTENSION ?= "-rockchip-cnc"

SRC_URI = " \
    git://github.com/rockchip-linux/kernel.git;protocol=https;branch=develop-6.1 \
    file://hd-rk3588-core.dts \
    file://mali-valhall.cfg \
    file://rockchip_rt.config \
    file://disable-npu-only.cfg \
    file://hd-rk3588-core-cnc-overlay.dtsi \
    file://0001-stmmac-resume-PHY-before-DMA-soft-reset.patch \
    file://0002-dwmac-rk-enable-clk-mac-and-default-rgmii-1g.patch \
    file://0003-dw-hdmi-rockchip-drive-enable-gpio-high-at-probe.patch \
    file://0004-arm64-select-ARCH_SUPPORTS_RT.patch \
"

SRCREV = "b4ef083dc0c3608e744deabb43dc6b781aadbe6e"

S = "${WORKDIR}/git"

KBUILD_DEFCONFIG = "rockchip_linux_defconfig"
KERNEL_IMAGETYPE ?= "Image"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

do_configure:prepend() {
	if [ -f "${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}" ]; then
		cp -f "${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG}" "${WORKDIR}/defconfig"
	else
		bbfatal "KBUILD_DEFCONFIG ${KBUILD_DEFCONFIG} not found under ${S}/arch/${ARCH}/configs/"
	fi

	install -d "${B}"
	cp -f "${WORKDIR}/defconfig" "${B}/.config"
	oe_runmake -C "${S}" O="${B}" ARCH="${ARCH}" scripts
	"${S}/scripts/kconfig/merge_config.sh" -m -O "${B}" \
		"${B}/.config" \
		"${WORKDIR}/rockchip_rt.config" \
		"${WORKDIR}/disable-npu-only.cfg" \
		"${WORKDIR}/mali-valhall.cfg"
	cp -f "${B}/.config" "${WORKDIR}/defconfig"
	rm -f "${B}/.config"
}

do_configure:append() {
	install -d "${S}/arch/arm64/boot/dts/rockchip"
	cp -f "${WORKDIR}/hd-rk3588-core.dts" \
		"${S}/arch/arm64/boot/dts/rockchip/hd-rk3588-core.dts"

	if [ -f "${WORKDIR}/hd-rk3588-core-cnc-overlay.dtsi" ]; then
		cat "${WORKDIR}/hd-rk3588-core-cnc-overlay.dtsi" >> \
			"${S}/arch/arm64/boot/dts/rockchip/hd-rk3588-core.dts"
	fi

	makefile="${S}/arch/arm64/boot/dts/rockchip/Makefile"
	if [ -f "${makefile}" ] && ! grep -q 'hd-rk3588-core\.dtb' "${makefile}"; then
		sed -i '/rk3588-evb4-lp4-v10-linux\.dtb/a\\tdtb-$(CONFIG_ARCH_ROCKCHIP) += hd-rk3588-core.dtb' \
			"${makefile}"
	fi
}

INSANE_SKIP:kernel-vmlinux += "buildpaths"
