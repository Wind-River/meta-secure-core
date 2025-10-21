DEPENDS += "openssl-native"
FILESEXTRAPATHS:prepend := "${THISDIR}/grub-efi:"

GRUB_SIGN_VERIFY_STRICT ?= "1"

EXTRA_SRC_URI = "\
    ${@'file://efi-secure-boot.inc file://password.inc' if d.getVar('UEFI_SB') == '1' else ''} \
"

GRUB_MOKVERIFY_PATCH = " \
    file://0003-mok2verify-support-to-verify-non-PE-file-with-PKCS-7.patch \
    file://0004-support-mok2-verify-in-multiboot2-protocol.patch \
    file://0005-Verify-all-buffiles.patch \
    file://0006-grub-efi-support-passing-a-buffer-to-be-verified.patch \
    file://0007-kern-efi-init.c-disable-inside-lockdown-and-shim_loc.patch \
    file://0008-efi-fallback-to-legacy-mode-if-mok2verify-is-loaded.patch \
    file://0009-mok2verify-remove-unused-parameter-from-grub_efi_mok.patch \
"

SRC_URI:append:class-target = " \
    file://0001-Add-efivar-module-to-read-EFI-variables.patch \
    file://0002-grub-verify-Add-strict_security-variable.patch \
    file://grub-efi.cfg \
    file://boot-menu.inc \
    ${@d.getVar('GRUB_MOKVERIFY_PATCH') if d.getVar('UEFI_SELOADER') == '1' else ''} \
    ${EXTRA_SRC_URI} \
    file://sbat.csv \
"

COMPATIBLE_HOST:aarch64 = 'null'

GRUB_PREFIX_DIR ?= "/EFI/BOOT"
EFI_BOOT_PATH ?= "/boot/efi/EFI/BOOT"

GRUB_SECURE_BOOT_MODULES += "${@'efivar password_pbkdf2 ' if d.getVar('UEFI_SB') == '1' else ''}"

GRUB_SIGNING_MODULES += "${@'pgp gcry_rsa gcry_sha256 gcry_sha512 --pubkey %s ' % d.getVar('GRUB_PUB_KEY') \
  if d.getVar('GRUB_SIGN_VERIFY') == '1' else ''}"

GRUB_SELOADER_MODULES += "${@'mok2verify ' if d.getVar('UEFI_SELOADER') == '1' else ''}"

GRUB_TPM_MODULES += "${@bb.utils.contains('DISTRO_FEATURES', 'tpm2', 'tpm', '', d)}"

GRUB_SECURE_BUILDIN ??= ""
GRUB_SECURE_BUILDIN:append:class-target = " \
  tftp reboot chain \
  ${GRUB_SECURE_BOOT_MODULES} \
  ${GRUB_SIGNING_MODULES} \
  ${GRUB_SELOADER_MODULES} \
  ${GRUB_TPM_MODULES} \
  --sbat ${UNPACKDIR}/sbat.csv \
  "

# Set a default root specifier.
inherit user-key-store

python __anonymous () {
    if d.getVar('UEFI_SB') != "1":
        return

    # Override the default filename if efi-secure-boot enabled.
    # grub-efi must be renamed as grub${arch}.efi for working with shim
    # or SELoader.
    import re

    target = d.getVar('TARGET_ARCH')
    if target == "x86_64":
        grubimage = "grubx64.efi"
    elif re.match('i.86', target):
        grubimage = "grubia32.efi"
    else:
        raise bb.parse.SkipPackage("grub-efi is incompatible with target %s" % target)

    d.setVar("GRUB_IMAGE", grubimage)
}

do_configure:prepend:class-target() {
    sed -i -e 's,@VERSION@,${PV},g' ${UNPACKDIR}/sbat.csv
}

do_compile:append:class-target() {
    if [ "${GRUB_SIGN_VERIFY}" = "1" -a "${GRUB_SIGN_VERIFY_STRICT}" = "1" ] ; then
        cat<<EOF>${WORKDIR}/cfg
set strict_security=1
EOF
    else
        > ${WORKDIR}/cfg
    fi

    if [ "${GRUB_SIGN_VERIFY}" = "0" -a "${UEFI_SELOADER}" = "1" ] ; then
        cat<<EOF>>${WORKDIR}/cfg
search.file (\$cmdpath)${GRUB_PREFIX_DIR}/grub.cfg root
EOF
    else
        cat<<EOF>>${WORKDIR}/cfg
search.file ${GRUB_PREFIX_DIR}/grub.cfg root
EOF
    fi

    cat<<EOF>>${WORKDIR}/cfg
set prefix=(\$root)${GRUB_PREFIX_DIR}
EOF

}

do_compile:append:class-native() {
    make grub-editenv
}

do_install:append:class-native() {
    install -m 0755 grub-editenv "${D}${bindir}"
}

do_install:append:class-target() {
    local menu="${UNPACKDIR}/boot-menu.inc"

    # Enable the default IMA rules if IMA is enabled and luks is disabled.
    # This is because unseal operation will fail when any PCR is extended
    # due to updating the aggregate integrity value by the default IMA rules.
    [ x"${IMA}" = x"1" -a x"${@bb.utils.contains('DISTRO_FEATURES', 'luks', '1', '0', d)}" != x"1" ] && {
        ! grep -q "ima_policy=tcb" "$menu" &&
            sed -i 's/^\s*linux\s\+.*bzImage.*/& ima_policy=tcb/g' "$menu"
    }

    # Replace the root parameter in boot command line with BOOT_CMD_ROOT,
    # which can be configured. It is helpful when secure boot is enabled.
    [ -n "${BOOT_CMD_ROOT}" ] && {
        sed -i "s,root=LABEL=root,root=${BOOT_CMD_ROOT},g" "$menu"
    }

    # Set serial console
    if [ ! -z "${SERIAL_CONSOLES}" ]; then
        tmp="${SERIAL_CONSOLES}"
        for entry in $tmp; do
            baudrate=$(echo $entry | sed 's/\;.*//')
            ttydev=$(echo $entry | sed -e 's/^[0-9]*\;//' -e 's/\;.*//')
            sed -i "s/^\s*linux\s\+.*bzImage.*/& console=${ttydev},${baudrate}/g" "$menu"
        done
        sed -i "s/^\s*linux\s\+.*bzImage.*/& console=tty0/g" "$menu"
    fi

    # Install the stacked grub configs.
    install -d "${D}${EFI_BOOT_PATH}"
    install -m 0600 "${UNPACKDIR}/grub-efi.cfg" "${D}${EFI_BOOT_PATH}/grub.cfg"
    install -m 0600 "$menu" "${D}${EFI_BOOT_PATH}"
    [ x"${UEFI_SB}" = x"1" ] && {
        install -m 0600 "${UNPACKDIR}/efi-secure-boot.inc" "${D}${EFI_BOOT_PATH}"
        install -m 0600 "${UNPACKDIR}/password.inc" "${D}${EFI_BOOT_PATH}"
    }

    # Create the initial environment block with empty item.
    grub-editenv "${D}${EFI_BOOT_PATH}/grubenv" create

    install -d "${D}${EFI_BOOT_PATH}/${GRUB_TARGET}-efi"
    grub-mkimage -c ../cfg -p "${GRUB_PREFIX_DIR}" -d "./grub-core" \
        -O "${GRUB_TARGET}-efi" -o "${B}/${GRUB_IMAGE}" \
        ${GRUB_BUILDIN} ${GRUB_SECURE_BUILDIN}

    install -m 0644 "${B}/${GRUB_IMAGE}" "${D}${EFI_BOOT_PATH}/${GRUB_IMAGE}"

    # Install the modules to grub-efi's search path
    oe_runmake -C grub-core install DESTDIR="${D}${EFI_BOOT_PATH}" pkglibdir=""

    # Remove build host references
    find "${D}${EFI_BOOT_PATH}" -name modinfo.sh -type f -exec \
        sed -i \
        -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
        -e 's|${DEBUG_PREFIX_MAP}||g' \
        -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
        {} +

    # Remove .module
    rm -f ${D}${EFI_BOOT_PATH}/${GRUB_TARGET}-efi/*.module
}

python do_sign:prepend:class-target() {
    bb.build.exec_func("check_deploy_keys", d)
    if d.getVar('GRUB_SIGN_VERIFY') == '1':
        bb.build.exec_func("check_boot_public_key", d)
}

fakeroot python do_sign:class-target() {
    image_dir = d.getVar('D')
    efi_boot_path = d.getVar('EFI_BOOT_PATH')
    grub_image = d.getVar('GRUB_IMAGE')
    dir = image_dir + efi_boot_path + '/'

    sb_sign(dir + grub_image, dir + grub_image, d)
    uks_bl_sign(dir + 'grub.cfg', d)
    uks_bl_sign(dir + 'boot-menu.inc', d)

    if d.getVar('UEFI_SB') == "1":
        uks_bl_sign(dir + 'efi-secure-boot.inc', d)
        uks_bl_sign(dir + 'password.inc', d)
}

python do_sign() {
}

addtask sign after do_install before do_deploy do_package

fakeroot do_chownboot() {
    chown root:root -R "${D}${EFI_BOOT_PATH}/grub.cfg${SB_FILE_EXT}"
    chown root:root -R "${D}${EFI_BOOT_PATH}/boot-menu.inc${SB_FILE_EXT}"

    [ x"${UEFI_SB}" = x"1" ] && {
        chown root:root -R "${D}${EFI_BOOT_PATH}/efi-secure-boot.inc${SB_FILE_EXT}"
        chown root:root -R "${D}${EFI_BOOT_PATH}/password.inc${SB_FILE_EXT}"
    }
}

addtask chownboot after do_deploy before do_package

# Append the do_deploy() in oe-core.
do_deploy:append:class-target() {
    install -m 0644 "${D}${EFI_BOOT_PATH}/${GRUB_IMAGE}" "${DEPLOYDIR}"

    # Deploy the stacked grub configs.
    install -m 0600 "${D}${EFI_BOOT_PATH}/grubenv" "${DEPLOYDIR}"
    install -m 0600 "${D}${EFI_BOOT_PATH}/grub.cfg" "${DEPLOYDIR}"
    install -m 0600 "${D}${EFI_BOOT_PATH}/boot-menu.inc" "${DEPLOYDIR}"
    install -m 0600 "${D}${EFI_BOOT_PATH}/grub.cfg${SB_FILE_EXT}" "${DEPLOYDIR}"
    install -m 0600 "${D}${EFI_BOOT_PATH}/boot-menu.inc${SB_FILE_EXT}" "${DEPLOYDIR}"

    [ x"${UEFI_SB}" = x"1" ] && {
        install -m 0600 "${D}${EFI_BOOT_PATH}/efi-secure-boot.inc" "${DEPLOYDIR}"
        install -m 0600 "${D}${EFI_BOOT_PATH}/password.inc" "${DEPLOYDIR}"
        install -m 0600 "${D}${EFI_BOOT_PATH}/efi-secure-boot.inc${SB_FILE_EXT}" "${DEPLOYDIR}"
        install -m 0600 "${D}${EFI_BOOT_PATH}/password.inc${SB_FILE_EXT}" "${DEPLOYDIR}"
    }

    install -d "${DEPLOYDIR}/efi-unsigned"
    install -m 0644 "${B}/${GRUB_IMAGE}" "${DEPLOYDIR}/efi-unsigned"
    PSEUDO_DISABLED=1 cp -af "${D}${EFI_BOOT_PATH}/${GRUB_TARGET}-efi" "${DEPLOYDIR}/efi-unsigned"
}

FILES:${PN} += "${EFI_BOOT_PATH}"

CONFFILES:${PN} += "\
    ${EFI_BOOT_PATH}/grub.cfg \
    ${EFI_BOOT_PATH}/grubenv \
    ${EFI_BOOT_PATH}/boot-menu.inc \
    ${EFI_BOOT_PATH}/efi-secure-boot.inc \
"
