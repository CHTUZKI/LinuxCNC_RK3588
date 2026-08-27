# Previously injected mainline-style hd-rk3588-core.dts into linux-yocto.
# hd-rk3588-core now uses Rockchip vendor kernel (linux-rockchip); keep this
# bbappend empty so linux-yocto is untouched if selected by other machines.

COMPATIBLE_MACHINE:hd-rk3588-core = "(-)"
