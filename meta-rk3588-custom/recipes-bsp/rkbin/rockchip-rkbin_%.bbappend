# Deploy MiniLoader for RKDevTool / update.img.
#
# Prefer the board-validated MiniLoader from the vendor Ubuntu package
# (DDR v1.17, same as live factory fwver). Fall back to rkbin boot_merger.

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append:rk3588s = " file://MiniLoaderAll.bin"

RK_MINILOADER_INI:rk3588s ?= "RKBOOT/RK3588MINIALL.ini"

do_deploy:append:rk3588s() {
	cd "${S}"

	board_loader="${WORKDIR}/MiniLoaderAll.bin"
	if [ -f "${board_loader}" ]; then
		loader="${board_loader}"
		bbnote "Using board-validated MiniLoaderAll.bin (vendor DDR), not boot_merger output"
	else
		if [ ! -x tools/boot_merger ]; then
			chmod +x tools/boot_merger || bbfatal "rkbin tools/boot_merger missing"
		fi
		./tools/boot_merger "${RK_MINILOADER_INI}" || bbfatal "boot_merger failed for ${RK_MINILOADER_INI}"
		loader=$(ls -1 "${S}"/rk3588_spl_loader_*.bin 2>/dev/null | head -1)
		if [ -z "${loader}" ] || [ ! -f "${loader}" ]; then
			bbfatal "boot_merger did not produce rk3588_spl_loader_*.bin"
		fi
	fi

	magic=$(dd if="${loader}" bs=4 count=1 2>/dev/null)
	if [ "${magic}" != "LDR " ]; then
		bbfatal "Unexpected MiniLoader magic '${magic}' (expected 'LDR ')"
	fi

	install -m 644 "${loader}" "${DEPLOYDIR}/$(basename "${loader}")"
	install -m 644 "${loader}" "${DEPLOYDIR}/loader.bin"
	install -m 644 "${loader}" "${DEPLOYDIR}/MiniLoaderAll.bin"
}
