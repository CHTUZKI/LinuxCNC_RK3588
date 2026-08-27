#!/usr/bin/env bash
# Cross-compile ssdc_homecomp.so using the linuxcnc Yocto workdir toolchain.
set -euo pipefail

ROOT=/home/xuning/yocto-rk3588-core
LCNC_WV=$ROOT/build/tmp/work/cortexa76-cortexa55-crypto-poky-linux/linuxcnc/2.9.10+git
LCNC_SRC=$LCNC_WV/git
SYSROOT=$LCNC_WV/recipe-sysroot
NATIVE=$LCNC_WV/recipe-sysroot-native
HERE=$(cd "$(dirname "$0")" && pwd)
OUT=${1:-$HERE/build}

HOMING_C=${HOMING_BASE:-$LCNC_SRC/src/emc/motion/homing.c}
if [[ ! -f "$HOMING_C" ]]; then
  echo "homing.c not found: $HOMING_C" >&2
  exit 1
fi

export PATH="$ROOT/build/tmp/sysroots-uninative/x86_64-linux/usr/bin:$NATIVE/usr/bin/python3-native:$ROOT/poky/scripts:$NATIVE/usr/bin/aarch64-poky-linux:$LCNC_WV/recipe-sysroot/usr/bin/crossscripts:$NATIVE/usr/sbin:$NATIVE/usr/bin:$NATIVE/sbin:$NATIVE/bin:$ROOT/poky/bitbake/bin:$ROOT/build/tmp/hosttools:${PATH:-}"

mkdir -p "$OUT"
cp -f "$HERE/ssdc_homecomp.comp" "$OUT/ssdc_homecomp.comp"
# Quoted path — cpp expands bare 'linux' token to 1
sed -i "s|^#define HOMING_BASE .*|#define HOMING_BASE \"$HOMING_C\"|" "$OUT/ssdc_homecomp.comp"

MODINC=$LCNC_SRC/src/Makefile.modinc
if [[ ! -f "$MODINC" ]]; then
  echo "Makefile.modinc missing: $MODINC" >&2
  exit 1
fi

CROSS_LD=$NATIVE/usr/bin/aarch64-poky-linux/aarch64-poky-linux-ld
CROSS_OBJDUMP=$NATIVE/usr/bin/aarch64-poky-linux/aarch64-poky-linux-objdump
if [[ ! -x "$CROSS_LD" ]]; then
  CROSS_LD=$(command -v aarch64-poky-linux-ld)
  CROSS_OBJDUMP=$(command -v aarch64-poky-linux-objdump)
fi
if [[ ! -x "$CROSS_LD" || ! -x "$CROSS_OBJDUMP" ]]; then
  echo "cross ld/objdump not found" >&2
  exit 1
fi

cat > "$OUT/Makefile" <<EOF
obj-m += ssdc_homecomp.o
include $MODINC
EXTRA_CFLAGS += -I$LCNC_SRC/include -I$LCNC_SRC/src -I$LCNC_SRC/src/emc/motion -I$SYSROOT/usr/include/linuxcnc
EOF

HC=$LCNC_SRC/bin/halcompile
if [[ ! -x "$HC" ]]; then
  echo "halcompile not found at $HC" >&2
  exit 1
fi

echo "Using halcompile: $HC"
echo "HOMING_BASE: $HOMING_C"
echo "Cross LD: $CROSS_LD"

cd "$OUT"
"$HC" --preprocess ssdc_homecomp.comp
rm -f ssdc_homecomp.so ssdc_homecomp.o ssdc_homecomp.tmp ssdc_homecomp.ver
make -f Makefile V=1 ssdc_homecomp.o

$CROSS_LD -d -r -o ssdc_homecomp.tmp ssdc_homecomp.o
$CROSS_OBJDUMP -w -j .rtapi_export -t ssdc_homecomp.tmp \
  | awk 'BEGIN{print "{ global :"} /rtapi_exported_/{printf("%s;\n", substr($6,16))} END{print "local : * ; };"}' \
  > ssdc_homecomp.ver

CC_CMD=$(awk -F':= ' '/^CC :=/{print $2; exit}' "$MODINC")
if [[ -z "$CC_CMD" ]]; then
  echo "Failed to parse CC from $MODINC" >&2
  exit 1
fi
echo "CC: $CC_CMD"
# shellcheck disable=SC2086
$CC_CMD -shared -Bsymbolic -Wl,--version-script,ssdc_homecomp.ver -o ssdc_homecomp.so ssdc_homecomp.o -lm
chmod -x ssdc_homecomp.so

file ssdc_homecomp.so
echo "Built: $OUT/ssdc_homecomp.so"
