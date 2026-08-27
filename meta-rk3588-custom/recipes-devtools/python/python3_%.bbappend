# LinuxCNC needs _tkinter on target only; do not pull tk into python3-native.
PACKAGECONFIG:append:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', ' tk', '', d)}"
