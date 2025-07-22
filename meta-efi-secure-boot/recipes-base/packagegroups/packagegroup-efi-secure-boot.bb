DESCRIPTION = "EFI Secure Boot packages for secure-environment."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
"

S = "${WORKDIR}"

ALLOW_EMPTY:${PN} = "1"

pkgs = "\
    grub-efi \
    efibootmgr \
"

RDEPENDS:${PN}:x86 = "${pkgs}"
RDEPENDS:${PN}:x86-64 = "${pkgs}"

kmods = "\
    kernel-module-efivarfs \
    kernel-module-efivars \
"

RRECOMMENDS:${PN}:x86 += "${kmods}"
RRECOMMENDS:${PN}:x86-64 += "${kmods}"

IMAGE_INSTALL:remove = "grub"
