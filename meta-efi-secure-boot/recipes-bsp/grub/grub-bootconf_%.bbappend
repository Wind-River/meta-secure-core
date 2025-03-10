FILESEXTRAPATHS:prepend := "${THISDIR}/grub-bootconf:"

SRC_URI:append:class-target = " \
    file://grub-efi.cfg \
"

inherit deploy
require ${@bb.utils.contains('DISTRO_FEATURES', 'efi-secure-boot', 'grub-bootconf-efi-secure-boot.inc', '', d)}

do_install:append() {
    rm ${D}${EFI_FILES_PATH}/grub.cfg
    install -m 0600 "${UNPACKDIR}/grub-efi.cfg" "${D}${EFI_FILES_PATH}/grub.cfg"
}

do_deploy() {
    # Deploy the stacked grub configs.
    install -m 0600 "${D}${EFI_FILES_PATH}/grub.cfg" "${DEPLOYDIR}"
}

addtask deploy after do_install before do_package

CONFFILES:${PN} += "${EFI_FILES_PATH}/grub.cfg"