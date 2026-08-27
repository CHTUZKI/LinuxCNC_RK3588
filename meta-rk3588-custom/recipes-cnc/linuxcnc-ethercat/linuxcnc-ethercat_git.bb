SUMMARY = "LinuxCNC HAL driver for IgH EtherCAT master (linuxcnc-ethercat)"
DESCRIPTION = "Provides lcec.so HAL realtime component and lcec_conf XML parser \
for driving EtherCAT slaves via the IgH EtherCAT master from LinuxCNC."
HOMEPAGE = "https://github.com/linuxcnc-ethercat/linuxcnc-ethercat"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-only;md5=801f80980d171dd6425610833a22dbe6"

SRC_URI = "git://github.com/linuxcnc-ethercat/linuxcnc-ethercat.git;protocol=https;branch=master"

SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

PV = "0.1+git${SRCPV}"

DEPENDS = "linuxcnc ethercat expat virtual/libc"
RDEPENDS:${PN} = "linuxcnc ethercat expat"

inherit pkgconfig python3native

do_configure[noexec] = "1"

# Cross-compile flags matching LinuxCNC uspace HAL module build (Makefile.modinc)
# -DRTAPI: realtime API mode (not ULAPI)
# -D__MODULE__: HAL module
# -DSIM + -fPIC: uspace build (shared object, not kernel module)
# -D_GNU_SOURCE -Drealtime: LinuxCNC uspace defines
LCEC_CFLAGS = "-I${STAGING_INCDIR}/linuxcnc -I${S}/src -I${S}/src/devices \
    -DRTAPI -D__MODULE__ -DSIM -D_GNU_SOURCE -Drealtime \
    -fPIC -fno-fast-math -fno-unsafe-math-optimizations \
    -fno-builtin-sin -fno-builtin-cos -fno-builtin-sincos \
    -Wframe-larger-than=2560 -Wall -Wextra"

LCEC_LDFLAGS = "-L${STAGING_LIBDIR} -Wl,-rpath,${STAGING_LIBDIR}"

do_compile() {
    mkdir -p ${B}/obj ${B}/devices

    cd ${S}/src

    # --- 1. Compile device objects → liblcecdevices.a ---
    for f in devices/*.c; do
        obj=${B}/devices/$(basename ${f%.c}.o)
        ${CC} ${CFLAGS} ${LCEC_CFLAGS} -c -o $obj $f
    done
    ${AR} rcs ${B}/liblcecdevices.a ${B}/devices/*.o

    # --- 2. Compile common objects ---
    COMMON_SRCS="lcec_devicelist.c lcec_ethercat.c lcec_pins.c lcec_lookup.c lcec_modparam.c lcec_malloc.c"
    for f in $COMMON_SRCS; do
        ${CC} ${CFLAGS} ${LCEC_CFLAGS} -c -o ${B}/obj/${f%.c}.o $f
    done

    # --- 3. Build lcec.so (HAL realtime component) ---
    ${CC} ${CFLAGS} ${LCEC_CFLAGS} -c -o ${B}/obj/lcec_main.o lcec_main.c
    # Create export symbol version script (like linuxcnc-ethercat Makefile)
    ${CC} -r -o ${B}/lcec.tmp ${B}/obj/lcec_main.o ${B}/obj/lcec_devicelist.o \
        ${B}/obj/lcec_ethercat.o ${B}/obj/lcec_pins.o ${B}/obj/lcec_lookup.o \
        ${B}/obj/lcec_modparam.o ${B}/obj/lcec_malloc.o
    objcopy -j .rtapi_export -O binary ${B}/lcec.tmp ${B}/lcec.sym
    (echo '{ global : '; tr -s '\0' < ${B}/lcec.sym | xargs -r0 printf '%s;\n' | grep .; echo 'local : * ; };') > ${B}/lcec.ver
    ${CC} -shared -Bsymbolic ${LDFLAGS} ${LCEC_LDFLAGS} \
        -Wl,--version-script,${B}/lcec.ver \
        -o ${B}/lcec.so ${B}/obj/lcec_main.o ${B}/obj/lcec_devicelist.o \
        ${B}/obj/lcec_ethercat.o ${B}/obj/lcec_pins.o ${B}/obj/lcec_lookup.o \
        ${B}/obj/lcec_modparam.o ${B}/obj/lcec_malloc.o \
        -Wl,--whole-archive ${B}/liblcecdevices.a -Wl,--no-whole-archive \
        -llinuxcnchal -lethercat -lrt -lm
    chmod -x ${B}/lcec.so

    # --- 4. Build lcec_conf (userspace XML config parser) ---
    # NOTE: use explicit object list, NOT ${B}/obj/lcec_conf*.o — that glob also
    # matches lcec_configgen.o (lcec_conf* matches lcec_configgen), causing a
    # duplicate main() link error.
    CONF_SRCS="lcec_conf.c $(ls lcec_conf_*.c 2>/dev/null)"
    CONF_OBJS=""
    for f in $CONF_SRCS; do
        ${CC} ${CFLAGS} ${LCEC_CFLAGS} -c -o ${B}/obj/${f%.c}.o $f
        CONF_OBJS="$CONF_OBJS ${B}/obj/${f%.c}.o"
    done
    ${CC} -o ${B}/lcec_conf $CONF_OBJS ${B}/obj/lcec_devicelist.o ${B}/obj/lcec_ethercat.o \
        ${B}/obj/lcec_pins.o ${B}/obj/lcec_lookup.o ${B}/obj/lcec_modparam.o ${B}/obj/lcec_malloc.o \
        ${LDFLAGS} ${LCEC_LDFLAGS} \
        -Wl,--whole-archive ${B}/liblcecdevices.a -Wl,--no-whole-archive \
        -llinuxcnchal -lexpat -lethercat -lm

    # --- 5. Build lcec_devices (userspace device list tool) ---
    ${CC} ${CFLAGS} ${LCEC_CFLAGS} -c -o ${B}/obj/lcec_devices.o lcec_devices.c
    ${CC} -o ${B}/lcec_devices ${B}/obj/lcec_devices.o ${B}/obj/lcec_devicelist.o ${B}/obj/lcec_ethercat.o \
        ${B}/obj/lcec_pins.o ${B}/obj/lcec_lookup.o ${B}/obj/lcec_modparam.o ${B}/obj/lcec_malloc.o \
        ${LDFLAGS} ${LCEC_LDFLAGS} \
        -Wl,--whole-archive ${B}/liblcecdevices.a -Wl,--no-whole-archive \
        -llinuxcnchal -lexpat -lethercat -lm

    # --- 6. Build lcec_configgen (userspace config generator) ---
    ${CC} ${CFLAGS} ${LCEC_CFLAGS} -c -o ${B}/obj/lcec_configgen.o lcec_configgen.c
    ${CC} -o ${B}/lcec_configgen ${B}/obj/lcec_configgen.o ${B}/obj/lcec_devicelist.o ${B}/obj/lcec_ethercat.o \
        ${B}/obj/lcec_pins.o ${B}/obj/lcec_lookup.o ${B}/obj/lcec_modparam.o ${B}/obj/lcec_malloc.o \
        ${LDFLAGS} ${LCEC_LDFLAGS} \
        -Wl,--whole-archive ${B}/liblcecdevices.a -Wl,--no-whole-archive \
        -llinuxcnchal -lexpat -lethercat -lm
}

do_install() {
    # HAL realtime component
    install -d ${D}${libdir}/linuxcnc/modules
    install -m 0755 ${B}/lcec.so ${D}${libdir}/linuxcnc/modules/lcec.so

    # Userspace tools
    install -d ${D}${bindir}
    install -m 0755 ${B}/lcec_conf ${D}${bindir}/lcec_conf
    install -m 0755 ${B}/lcec_devices ${D}${bindir}/lcec_devices
    install -m 0755 ${B}/lcec_configgen ${D}${bindir}/lcec_configgen

    # Example configs
    if [ -d ${S}/examples ]; then
        install -d ${D}${datadir}/linuxcnc-ethercat/examples
        cp -a --no-preserve=ownership ${S}/examples/* ${D}${datadir}/linuxcnc-ethercat/examples/
        chown -R root:root ${D}${datadir}/linuxcnc-ethercat/
    fi
}

FILES:${PN} += " \
    ${libdir}/linuxcnc/modules/lcec.so \
    ${bindir}/lcec_conf \
    ${bindir}/lcec_devices \
    ${bindir}/lcec_configgen \
    ${datadir}/linuxcnc-ethercat \
"

INSANE_SKIP:${PN} += "already-stripped rpaths ldflags file-rdeps"
