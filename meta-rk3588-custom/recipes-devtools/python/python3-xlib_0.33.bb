SUMMARY = "Python X Library"
DESCRIPTION = "The Python X Library is an X11 client library for Python programs, written entirely in Python."
HOMEPAGE = "https://github.com/python-xlib/python-xlib"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8975de00e0aab10867abf36434958a28"

SRC_URI = "https://files.pythonhosted.org/packages/86/f5/8c0653e5bb54e0cbdfe27bf32d41f27bc4e12faa8742778c17f2a71be2c0/python-xlib-${PV}.tar.gz"
SRC_URI[sha256sum] = "55af7906a2c75ce6cb280a584776080602444f75815a7aff4d287bb2d7018b32"

S = "${WORKDIR}/python-xlib-${PV}"

inherit python3native

# 纯 Python 包，setup.py 有 setup_requires=['setuptools-scm'] 会触发 pip 下载，
# 而 python3-native 不带 pip。直接手动安装 Xlib 目录和版本文件即可。
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -a --no-preserve=ownership ${S}/Xlib ${D}${PYTHON_SITEPACKAGES_DIR}/
	find ${D}${PYTHON_SITEPACKAGES_DIR}/Xlib -name '__pycache__' -type d -exec rm -rf {} + 2>/dev/null || true
}

FILES:${PN} = "${libdir}/python3*/site-packages/*"

RDEPENDS:${PN} = "python3-six python3-core"
