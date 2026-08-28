SUMMARY = "Synergy - mouse and keyboard sharing utility"
DESCRIPTION = "Use the keyboard, mouse, or trackpad of one computer to control nearby computers."
HOMEPAGE = "https://symless.com/synergy"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4641e94ec96f98fabc56ff9cc48be14b"

SRC_URI = "file://synergy"
S = "${WORKDIR}/synergy"

inherit cmake pkgconfig python3native

DEPENDS = " \
    qtbase qtbase-native qttools \
    openssl \
    libxtst libxinerama libxi libxext libxrandr libxkbfile \
    libsm libice \
    libnotify gdk-pixbuf glib-2.0 \
    cli11 tomlplusplus \
    pkgconfig-native \
    python3-native \
"

EXTRA_OECMAKE = " \
    -DBUILD_GUI=ON \
    -DBUILD_TESTS=OFF \
    -DBUILD_INSTALLER=ON \
    -DBUILD_UNIFIED=OFF \
    -DSYSTEM_LIBEI=OFF \
    -DSYSTEM_LIBPORTAL=OFF \
    -DSYSTEM_CLI11=ON \
    -DSYSTEM_TOMLPLUSPLUS=ON \
    -DQT_HOST_PATH:PATH=${RECIPE_SYSROOT_NATIVE}${prefix_native}/ \
"

do_configure:prepend() {
    sed -i 's/add_compile_options(-Werror)/# disabled -Werror/' ${S}/cmake/Build.cmake
    sed -i 's/add_compile_options(-Werror)/# disabled -Werror/' ${S}/ext/synergy-extra/CMakeLists.txt
    sed -i 's/add_compile_options(\/WX)/# disabled \/WX/' ${S}/cmake/Build.cmake
    sed -i 's/add_compile_options(\/WX)/# disabled \/WX/' ${S}/ext/synergy-extra/CMakeLists.txt

    # Source is shipped as a plain directory (file://), so there's no .git.
    # Version.cmake requires a git repo to run `git describe origin/master --tags`.
    # Create a minimal repo with one commit + a version tag so the describe call succeeds.
    # Use git -C instead of cd so we don't change the working directory (cmake uses CWD as build dir).
    git -C ${S} init -q && \
    git -C ${S} config user.email "build@yocto" && \
    git -C ${S} config user.name "Yocto" && \
    git -C ${S} add -A && \
    git -C ${S} commit -q -m "init" && \
    git -C ${S} tag "v$(cat ${S}/VERSION)" && \
    git -C ${S} branch -M master && \
    git -C ${S} update-ref refs/remotes/origin/master refs/heads/master
}

FILES:${PN} += " \
    ${datadir}/applications/com.symless.synergy.desktop \
    ${datadir}/icons/hicolor/512x512/apps/com.symless.synergy.png \
"

RDEPENDS:${PN} = " \
    qtbase \
    openssl \
    libxtst libxinerama libxi libxext libxrandr libxkbfile \
    libsm libice \
    libnotify gdk-pixbuf glib-2.0 \
"
