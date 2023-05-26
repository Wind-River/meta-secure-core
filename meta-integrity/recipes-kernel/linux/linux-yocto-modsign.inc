FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

MODSIGN_ENABLED = "${@bb.utils.contains('DISTRO_FEATURES', 'modsign', '1', '0', d)}"

DEPENDS += "${@'key-store openssl-native' \
               if d.getVar('MODSIGN_ENABLED') == '1' \
               else ''}"

SRC_URI += "\
    ${@'file://modsign.scc file://modsign.cfg' \
       if d.getVar('MODSIGN_ENABLED') == '1' else ''} \
"

INHIBIT_PACKAGE_STRIP = "${@'1' if d.getVar('MODSIGN_ENABLED') == '1' else '0'}"

inherit user-key-store

do_configure:prepend() {
    if [ ${MODSIGN_ENABLED} = "1" ]; then
        modsign_key="${@uks_modsign_keys_dir(d)}/modsign_key.key"
    else
        modsign_key="${STAGING_DIR_TARGET}${sysconfdir}/keys/modsign_key.key"
    fi
    modsign_cert="${STAGING_DIR_TARGET}${sysconfdir}/keys/modsign_key.crt"

    if [ -f "$modsign_key" -a -f "$modsign_cert" ]; then
        cat "$modsign_key" "$modsign_cert" \
            > "${B}/modsign_key.pem"
    else
        true
    fi
}
