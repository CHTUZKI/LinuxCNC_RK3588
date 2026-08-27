# LinuxCNC uspace: rtapi_app 拒绝 RTAPI_UID=0，需非零 fallback UID（euid 仍为 root）
export RTAPI_UID=1000
export RTAPI_FIFO_PATH=/tmp/.rtapi_fifo
# Poky Python 默认 site-packages；LinuxCNC 装在 dist-packages
export PYTHONPATH=/usr/lib/python3/dist-packages${PYTHONPATH:+:${PYTHONPATH}}
# Yocto bwidget 需在 TCLLIBPATH；linuxcnc 脚本会追加 /usr/lib/tcltk
export TCLLIBPATH="/usr/lib/tcl8.6/bwidget${TCLLIBPATH:+:${TCLLIBPATH}}"
