SUMMARY = "The minimal, blazing-fast, and infinitely customizable prompt for any shell"
DESCRIPTION = "Starship is the minimal, blazing fast, and highly customizable prompt \
for any shell! Shows relevant information at a glance, works on any shell, \
and is highly configurable."
HOMEPAGE = "https://starship.rs"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a25cce5cb436456c4b21461a3ff95b0d"

SRC_URI = "git://github.com/starship/starship.git;protocol=https;nobranch=1;tag=v${PV} \
    file://starship.toml \
    file://starship.sh \
    file://catppuccin-mocha.theme \
    file://xfce4-terminalrc \
    file://locale.sh \
    file://neofetch-config.conf \
"

S = "${WORKDIR}/git"

CARGO_SRC_DIR = ""

inherit cargo cargo-update-recipe-crates

require ${BPN}-crates.inc

# Disable default features (battery, notify, gix-max-perf) to avoid
# extra native deps like cmake/zlib-ng and unnecessary functionality on a CNC board
CARGO_BUILD_FLAGS = "-v --frozen --target ${RUST_HOST_SYS} --release --manifest-path=${S}/Cargo.toml --no-default-features"

do_configure:prepend() {
    # Disable strip and LTO to speed up cross-build
    sed -i 's/strip\ =\ true/strip\ =\ false/g' ${S}/Cargo.toml
    sed -i 's/lto\ =\ true/lto\ =\ false/g' ${S}/Cargo.toml
    sed -i 's/codegen-units\ =\ 1/codegen-units\ =\ 16/g' ${S}/Cargo.toml
}

do_install:append() {
    # Default starship config
    install -d ${D}${sysconfdir}
    install -m 0644 ${WORKDIR}/starship.toml ${D}${sysconfdir}/starship.toml

    # bash integration for login shells (/etc/profile.d)
    install -d ${D}${sysconfdir}/profile.d
    install -m 0644 ${WORKDIR}/starship.sh ${D}${sysconfdir}/profile.d/starship.sh

    # Root .bashrc — Poky bash doesn't compile /etc/bash.bashrc support,
    # so non-login interactive shells (XFCE Terminal, SSH non-login) only
    # read ~/.bashrc. Install one for root that sources starship.
    install -d ${D}/home/root
    cat > ${D}/home/root/.bashrc <<'BASHRC'
# ~/.bashrc — sourced by interactive non-login bash shells
# Set UTF-8 locale so Nerd Font / Powerline glyphs render correctly
export LANG="${LANG:-C.UTF-8}"
export LC_ALL="${LC_ALL:-C.UTF-8}"
export STARSHIP_CONFIG="${STARSHIP_CONFIG:-/etc/starship.toml}"
if [ -x /usr/bin/starship ]; then
    eval "$(starship init bash)"
fi
BASHRC
    chmod 0644 ${D}/home/root/.bashrc

    # Also set locale system-wide via /etc/profile.d
    install -m 0644 ${WORKDIR}/locale.sh ${D}${sysconfdir}/profile.d/locale.sh

    # xfce4-terminal Catppuccin Mocha color scheme
    install -d ${D}${datadir}/xfce4/terminal/colorschemes
    install -m 0644 ${WORKDIR}/catppuccin-mocha.theme \
        ${D}${datadir}/xfce4/terminal/colorschemes/catppuccin-mocha.theme

    # xfce4-terminal default config: Catppuccin colors + JetBrainsMono Nerd Font + transparency
    install -d ${D}/home/root/.config/xfce4/terminal
    install -m 0644 ${WORKDIR}/xfce4-terminalrc \
        ${D}/home/root/.config/xfce4/terminal/terminalrc

    # Custom neofetch config with PREEMPT_RT / EtherCAT / LinuxCNC info
    install -d ${D}/home/root/.config/neofetch
    install -m 0644 ${WORKDIR}/neofetch-config.conf \
        ${D}/home/root/.config/neofetch/config.conf
}

FILES:${PN} += " \
    ${sysconfdir}/starship.toml \
    ${sysconfdir}/profile.d/starship.sh \
    ${sysconfdir}/profile.d/locale.sh \
    /home/root/.bashrc \
    ${datadir}/xfce4/terminal/colorschemes/catppuccin-mocha.theme \
    /home/root/.config/xfce4/terminal/terminalrc \
    /home/root/.config/neofetch/config.conf \
"

RDEPENDS:${PN} += "bash jetbrainsmono-nerd-font neofetch"
