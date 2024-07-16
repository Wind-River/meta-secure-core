DESCRIPTION = "EFI Secure Boot packages for secure-environment."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

SELOADER_PKG = "${@'seloader' if d.getVar('UEFI_SELOADER') == '1' else ''}"
SHIM_PKG = "${@'' if d.getVar('UEFI_SELOADER') == '1' and d.getVar('MOK_SB') != '1' else 'shim'}"
ALLOW_EMPTY:${PN} = "1"

pkgs = "\
    grub-efi \
    efitools \
    efibootmgr \
    mokutil \
    ${SELOADER_PKG} \
    ${SHIM_PKG} \
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
