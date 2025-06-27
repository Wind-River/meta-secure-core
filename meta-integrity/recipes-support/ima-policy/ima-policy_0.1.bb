DESCRIPTION = "The default external IMA policy"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
"

SRC_URI = "\
    file://ima_policy.default \
"

S = "${UNPACKDIR}"

do_install() {
    install -d "${D}${sysconfdir}/ima"
    install -m 0400 "${S}/ima_policy.default" \
        "${D}${sysconfdir}/ima"
}

FILES:${PN} = "${sysconfdir}"
