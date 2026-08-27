SUMMARY = "Python OpenGL bindings"
DESCRIPTION = "PyOpenGL provides Python bindings to OpenGL and related APIs."
HOMEPAGE = "https://mcfletch.github.io/pyopengl/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e28db1891134277ef36837c3f33f4d39"

# PyPI 仅发布 wheel，无 sdist
SRC_URI = " \
    https://files.pythonhosted.org/packages/92/44/8634af40b0db528b5b37e901c0dc67321354880d251bf8965901d57693a5/PyOpenGL-3.1.9-py3-none-any.whl;downloadfilename=PyOpenGL-3.1.9-py3-none-any.whl \
    file://LICENSE \
"
SRC_URI[sha256sum] = "15995fd3b0deb991376805da36137a4ae5aba6ddbb5e29ac1f35462d130a3f77"

inherit python3native setuptools3

S = "${WORKDIR}"

do_configure[noexec] = "1"

do_compile() {
	${STAGING_BINDIR_NATIVE}/python3-native/python3 -m zipfile -e \
		${WORKDIR}/PyOpenGL-3.1.9-py3-none-any.whl ${B}/wheel
}

do_install() {
	install -d ${D}${PYTHON_SITEPACKAGES_DIR}
	cp -a --no-preserve=ownership ${B}/wheel/OpenGL ${D}${PYTHON_SITEPACKAGES_DIR}/
}

RDEPENDS:${PN} = "python3-numpy"
