FILESEXTRAPATHS:prepend := "${THISDIR}/rpm:"

PACKAGECONFIG:append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'imaevm', '', d)} \
    "

# IMA signing support is provided by RPM plugin.
EXTRA_OECMAKE:remove = "\
    -DENABLE_PLUGINS=OFF \
"
EXTRA_OECMAKE:append:class-native = " -DENABLE_INHIBIT_PLUGIN=OFF -DWITH_AUDIT=OFF"

SRC_URI:append = " \
                  file://macros.ima \
                 "

do_install:append () {
    install -d ${D}${sysconfdir}/rpm
    install -m 0644 ${WORKDIR}/macros.ima ${D}${sysconfdir}/rpm/
}
