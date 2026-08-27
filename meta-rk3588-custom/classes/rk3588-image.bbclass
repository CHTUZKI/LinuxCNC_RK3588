# HD-RK3588-CORE image convenience helpers.

IMAGE_POSTPROCESS_COMMAND:append = " link_latest_image;"
link_latest_image() {
    rm -rf "${TOPDIR}/latest"
    ln -sf "${DEPLOY_DIR_IMAGE}" "${TOPDIR}/latest"
}

# 默认终端：rxvt 太老（无 Unicode），改为 xfce4-terminal（VTE，UTF-8 原生）
# 用 ROOTFS_POSTPROCESS_COMMAND（do_rootf 末尾执行，在 update-alternatives 之后、
# 镜像生成之前），否则 IMAGE_POSTPROCESS_COMMAND 跑在 ext4 已打包之后，改不进去。
ROOTFS_POSTPROCESS_COMMAND:append = " set_default_terminal;"
set_default_terminal() {
    ln -sf /usr/bin/xfce4-terminal "${IMAGE_ROOTFS}${bindir}/x-terminal-emulator"
}

# LinuxCNC gremlin.py 用 ctypes.LoadLibrary('libX11.so') 加载 X11 库，
# 但运行时只有 libX11.so.6，无 .so 符号链接（通常由 -dev 包提供）。
# 创建符号链接，避免拉入整个 -dev 包（含头文件）。
ROOTFS_POSTPROCESS_COMMAND:append = " link_runtime_so_symlinks;"
link_runtime_so_symlinks() {
    ln -sf libX11.so.6 "${IMAGE_ROOTFS}${libdir}/libX11.so"
}
