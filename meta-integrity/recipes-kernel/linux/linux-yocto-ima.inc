FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

IMA_ENABLED = "${@bb.utils.contains('DISTRO_FEATURES', 'ima', '1', '0', d)}"

DEPENDS += "${@'key-store openssl-native' \
               if d.getVar('IMA_ENABLED') == '1' \
               else ''}"

SRC_URI += "\
    ${@'file://ima.scc file://ima.cfg' \
       if d.getVar('IMA_ENABLED') == '1' else ''} \
"

inherit user-key-store

do_configure:prepend() {
    sys_cert="${STAGING_DIR_TARGET}${sysconfdir}/keys/system_trusted_key.crt"

    if [ -f "$sys_cert" ]; then
        install -m 0644 "$sys_cert" "${B}"
    fi
}
