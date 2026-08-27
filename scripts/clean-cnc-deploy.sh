#!/bin/bash
# LinuxCNC 分支：重置 CNC deploy / sstate manifest（改内核、U-Boot、rkbin 后偶发 deploy 跳过时用）
set -euo pipefail

TOPDIR="${1:-$(dirname "$0")/../build}"
CTRL="${TOPDIR}/tmp/sstate-control"
DEPLOY="${TOPDIR}/tmp/deploy/images/hd-rk3588-core-cnc"

if [ ! -d "${CTRL}" ]; then
	echo "sstate-control 不存在: ${CTRL}" >&2
	exit 1
fi

echo "清理 CNC deploy manifest..."
rm -fv "${CTRL}"/manifest-hd_rk3588_core_cnc-*-{rockchip-rkbin,linux-rockchip,u-boot}.deploy \
	"${CTRL}"/manifest-hd_rk3588_core_cnc-*-rk3588-image-cnc.image_complete 2>/dev/null || true

if [ -d "${DEPLOY}" ]; then
	echo "清理 CNC bootloader 二进制（强制 rockchip-rkbin 重 deploy）: ${DEPLOY}"
	rm -fv "${DEPLOY}"/{bl31-rk3588.elf,ddr-rk3588.bin,loader.bin,MiniLoaderAll.bin,tee-rk3588.bin} 2>/dev/null || true
fi

echo "完成。接着: bitbake rockchip-rkbin -c deploy -f && bitbake rk3588-image-cnc"
