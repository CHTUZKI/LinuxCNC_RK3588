SUMMARY = "JetBrainsMono Nerd Font — monospace font with Powerline/icons for terminal use"
DESCRIPTION = "JetBrains Mono with Nerd Font patches: includes Powerline symbols, \
dev icons, and other glyphs for use with Starship and other prompt tools."
HOMEPAGE = "https://www.nerdfonts.com/"
LICENSE = "OFL-1.1"
LIC_FILES_CHKSUM = "file://OFL.txt;md5=43dc1a748ef82aa746d6a645d52578a9"

SRC_URI = "https://github.com/ryanoasis/nerd-fonts/releases/download/v3.5.1/JetBrainsMono.zip;subdir=jetbrainsmono \
    file://OFL.txt \
"
SRC_URI[sha256sum] = "fab782a66f7d3019da64f6572db9fc5d3a4bcb19f9fa13e2d8a62e3693d6396e"

S = "${WORKDIR}/jetbrainsmono"

do_install() {
    install -d ${D}${datadir}/fonts/truetype/jetbrainsmono-nerd-font
    # Only install Regular and Bold (NerdFont variant, not Mono/Propo/NL) to keep image small
    for f in JetBrainsMonoNerdFont-Regular.ttf JetBrainsMonoNerdFont-Bold.ttf; do
        if [ -f "${S}/$f" ]; then
            install -m 0644 "${S}/$f" ${D}${datadir}/fonts/truetype/jetbrainsmono-nerd-font/
        fi
    done
}

FILES:${PN} = "${datadir}/fonts/truetype/jetbrainsmono-nerd-font/*.ttf"

INSANE_SKIP:${PN} += "arch"
