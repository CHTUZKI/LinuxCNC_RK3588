SUMMARY = "LinuxCNC motion controller with Axis GUI"
DESCRIPTION = "LinuxCNC uspace build for PREEMPT_RT + IgH EtherCAT on RK3588."
HOMEPAGE = "https://linuxcnc.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "git://github.com/linuxcnc/linuxcnc.git;protocol=https;branch=master \
           file://skip-tkinter-check-when-cross.patch \
"
SRCREV = "ab930078a55e06a8c591fe25ade20f729be22d34"

S = "${WORKDIR}/git/src"
B = "${WORKDIR}/build"

PV = "2.9.10+git${SRCPV}"

inherit autotools pkgconfig python3native

DEPENDS = " \
    autoconf-native automake-native libtool-native intltool-native \
    tcl-native tk-native \
    tcl tk \
    gtk+3 glib-2.0 libmodbus libusb1 libgpiod \
    boost fmt \
    libxmu libxinerama libxext libxi libxrandr libxrender libxscrnsaver \
    mesa libglu \
    readline libedit \
    python3 python3-numpy-native python3-pybind11 \
    yapps2-native \
    libtirpc \
    bison-native flex-native \
"

RDEPENDS:${PN} = " \
    tcl tk bwidget \
    bash \
    python3 python3-numpy python3-tkinter python3-pyopengl python3-pygobject python3-xlib \
    gtk+3 gtksourceview4 \
    mesa-demos \
    procps psmisc util-linux \
    coreutils \
    udev \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} \
"

# configure 会探测目标机运行时工具路径；Yocto 构建时 PATH 不含 ps/kill 等，需显式缓存。
CACHED_CONFIGUREVARS += " \
    ac_cv_path_GREP=${bindir}/grep \
    ac_cv_path_AWK=${bindir}/awk \
    ac_cv_path_PS=${bindir}/ps \
    ac_cv_path_KILL=${bindir}/kill \
    ac_cv_path_WHOAMI=${bindir}/whoami \
    ac_cv_path_PIDOF=${base_sbindir}/pidof \
    ac_cv_path_IPCS=${bindir}/ipcs \
    ac_cv_path_FUSER=${bindir}/fuser \
"

EXTRA_OECONF = " \
    --with-realtime=uspace \
    --disable-build-documentation \
    --disable-manpages \
    --disable-check-runtime-deps \
    --with-tclConfig=${STAGING_LIBDIR}/tclConfig.sh \
    --with-tkConfig=${STAGING_LIBDIR}/tkConfig.sh \
"

export PYTHON = "${STAGING_BINDIR_NATIVE}/python3-native/python3"

do_configure:prepend() {
	cd ${S}
	./autogen.sh
	export PATH="${STAGING_BINDIR_NATIVE}:${PATH}"
}

do_configure() {
	cd ${S}
	${CACHED_CONFIGUREVARS} ./configure ${CONFIGUREOPTS} ${EXTRA_OECONF}
}

# tclConfig.sh / tkConfig.sh 给出 -I/usr/include/...，Yocto 交叉编译器不会将其映射到 sysroot
do_configure:append() {
	sed -i \
		-e "s|-I/usr/include/tcl8.6|-I${STAGING_INCDIR}/tcl8.6|g" \
		-e "s|-I/usr/include |-I${STAGING_INCDIR} |g" \
		-e "s|-L/usr/lib|-L${STAGING_LIBDIR}|g" \
		-e "s|${STAGING_DIR_NATIVE}/usr/lib -lpython3|${STAGING_LIBDIR} -lpython3|g" \
		-e "s|${STAGING_DIR_NATIVE}/usr/include/python3|${STAGING_INCDIR}/python3|g" \
		-e 's/$(Q)ld -d -r/$(Q)$(CC) -r/' \
		${S}/Makefile.inc
	sed -i \
		-e 's/$(Q)ld -d -r/$(Q)$(CC) -r/' \
		${S}/Makefile
}

do_compile() {
	oe_runmake -C ${S} -O build-software
}

do_install() {
	oe_runmake -C ${S} DESTDIR=${D} install-software
}

do_install:append() {
	# install 脚本可能保留构建机 python3-native / hosttools 路径
	for f in $(grep -rl 'python3-native/python3' ${D} 2>/dev/null || true); do
		sed -i '1s|.*|#!/usr/bin/env python3|' "$f"
	done
	for f in $(grep -rl 'hosttools' ${D}${bindir} ${D}${libdir}/linuxcnc 2>/dev/null || true); do
		sed -i -E 's|[^"= ]*hosttools/grep|/usr/bin/grep|g' "$f"
		sed -i -E 's|[^"= ]*hosttools/awk|/usr/bin/awk|g' "$f"
	done
	# Poky: python 模块在 dist-packages；pidof 在 /usr/bin
	if [ -f ${D}${bindir}/linuxcnc ]; then
		sed -i \
			-e 's|PYTHONPATH=$LINUXCNC_HOME/lib/python|PYTHONPATH=$LINUXCNC_HOME/lib/python3/dist-packages:$LINUXCNC_HOME/lib/python|g' \
			-e 's|PIDOF="/usr/sbin/pidof|PIDOF="/usr/bin/pidof|g' \
			-e 's|TCLLIBPATH=$LINUXCNC_HOME/lib/tcltk|TCLLIBPATH="/usr/lib/tcl8.6/bwidget $LINUXCNC_HOME/lib/tcltk"|g' \
			-e 's|TCLLIBPATH="$LINUXCNC_HOME/lib/tcltk $TCLLIBPATH"|TCLLIBPATH="/usr/lib/tcl8.6/bwidget $LINUXCNC_HOME/lib/tcltk $TCLLIBPATH"|g' \
			${D}${bindir}/linuxcnc
	fi

	# 安装 sample-configs 到 linuxcnc.tcl 硬编码的搜索路径，使无参数启动
	# linuxcnc 时 pickconfig.tcl 能列出可用配置（不依赖 $HOME）。
	# install-software 已装到 ${datadir}/doc/linuxcnc/examples/sample-configs，
	# 但 Yocto 默认把 ${datadir}/doc split 到 -doc 包；移到 ${datadir}/linuxcnc
	# 下（已被 FILES:${PN} 的 ${datadir}/linuxcnc 包含），并改 linuxcnc.tcl 路径。
	if [ -d "${D}${datadir}/doc/linuxcnc/examples/sample-configs" ]; then
		install -d ${D}${datadir}/linuxcnc/examples
		cp -a --no-preserve=ownership ${D}${datadir}/doc/linuxcnc/examples/sample-configs \
			${D}${datadir}/linuxcnc/examples/sample-configs
		chown -R root:root ${D}${datadir}/linuxcnc/examples/sample-configs
		rm -rf ${D}${datadir}/linuxcnc/examples/sample-configs/.git
		rm -rf ${D}${datadir}/doc/linuxcnc/examples/sample-configs
		rmdir --ignore-fail-on-non-empty ${D}${datadir}/doc/linuxcnc/examples \
			${D}${datadir}/doc/linuxcnc 2>/dev/null || true
		# 改 linuxcnc.tcl 里硬编码的 CONFIG_PATH 搜索路径
		sed -i 's|/usr/share/doc/linuxcnc/examples/sample-configs|/usr/share/linuxcnc/examples/sample-configs|g' \
			${D}${libdir}/tcltk/linuxcnc/linuxcnc.tcl
	fi

	# 删除自带的 linuxcnc.desktop，改由 rk3588-cnc-config 提供修改版
	# （root 登录需 RTAPI_UID≠0 等 env，官方版 Exec 无 env 不适用）
	rm -f ${D}${datadir}/applications/linuxcnc.desktop

	# 安装 debian/extras 菜单资源（与官方 Debian 包对齐）：
	#   - CNC.menu / .directory（菜单结构）
	#   - hicolor SVG 图标（linuxcncicon.svg + 语言变体 + alt）
	#   - udev rules（hm2-pci / realtime / shuttle / xhc）
	# 文档 .desktop 暂不安装（构建时禁用了文档生成）。
	EXTRAS=${WORKDIR}/git/debian/extras
	if [ -d "$EXTRAS" ]; then
		# CNC.menu
		install -d ${D}${sysconfdir}/xdg/menus/applications-merged
		install -m 0644 $EXTRAS/etc/xdg/menus/applications-merged/CNC.menu \
			${D}${sysconfdir}/xdg/menus/applications-merged/CNC.menu
		# .directory
		install -d ${D}${datadir}/desktop-directories
		install -m 0644 $EXTRAS/usr/share/desktop-directories/linuxcnc-*.directory \
			${D}${datadir}/desktop-directories/
		# hicolor SVG 图标
		install -d ${D}${datadir}/icons/hicolor/scalable/apps
		cp -a --no-preserve=ownership $EXTRAS/usr/share/icons/hicolor/scalable/apps/. \
			${D}${datadir}/icons/hicolor/scalable/apps/
		# udev rules
		install -d ${D}${nonarch_base_libdir}/udev/rules.d
		install -m 0644 $EXTRAS/lib/udev/rules.d/99-*.rules \
			${D}${nonarch_base_libdir}/udev/rules.d/
	fi
}

FILES:${PN} += " \
    ${datadir}/linuxcnc \
    ${sysconfdir}/linuxcnc \
    ${sysconfdir}/xdg/menus/applications-merged/CNC.menu \
    ${libdir}/linuxcnc \
    ${libdir}/python3/dist-packages \
    ${libdir}/tcltk/linuxcnc \
    ${datadir}/applications \
    ${datadir}/desktop-directories \
    ${datadir}/icons/hicolor \
    ${datadir}/axis \
    ${datadir}/glade \
    ${datadir}/gmoccapy \
    ${datadir}/gscreen \
    ${datadir}/gtksourceview-4/language-specs \
    ${datadir}/qtvcp \
    ${nonarch_base_libdir}/udev/rules.d \
"

# 导出到 sysroot，供 linuxcnc-ethercat 交叉构建使用：
#   - halcompile（HAL 组件编译器）— 标准过程不导出脚本，需手动安装并修复 shebang
#   - Makefile.modinc（HAL 模块构建规则）— ${datadir} 不在默认 SYSROOT_DIRS 中，需手动安装
# LinuxCNC 头文件和 liblinuxcnchal.so 由标准 do_populate_sysroot 自动处理。
linuxcnc_sysroot_preprocess() {
	# halcompile — 手动安装到 sysroot 并修复 shebang
	install -d ${SYSROOT_DESTDIR}${bindir}
	install -m 0755 ${D}${bindir}/halcompile ${SYSROOT_DESTDIR}${bindir}/halcompile
	sed -i '1s|.*|#!/usr/bin/env python3|' ${SYSROOT_DESTDIR}${bindir}/halcompile

	# Makefile.modinc 在 ${datadir} 下，标准 sysroot 不导出，需手动安装
	install -d ${SYSROOT_DESTDIR}${datadir}/linuxcnc
	if [ -f ${D}${datadir}/linuxcnc/Makefile.modinc ]; then
		install -m 0644 ${D}${datadir}/linuxcnc/Makefile.modinc \
			${SYSROOT_DESTDIR}${datadir}/linuxcnc/Makefile.modinc
	fi
}
SYSROOT_PREPROCESS_FUNCS += "linuxcnc_sysroot_preprocess"

INSANE_SKIP:${PN} += "already-stripped rpaths dev-so dev-elf shebang-size ldflags file-rdeps"
