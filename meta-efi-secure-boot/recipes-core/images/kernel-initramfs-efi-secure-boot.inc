DEPENDS += "openssl-native"
inherit user-key-store deploy

# Always fetch the latest initramfs image
do_install[nostamp] = "1"

fakeroot python do_sign() {
    if d.getVar('BUNDLE') == '0':
        for compr in d.getVar('INITRAMFS_FSTYPES').split():
            uks_bl_sign(d.expand('${D}/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.') + compr, d)
    else:
        uks_bl_sign(d.expand('${D}/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}'), d)
}
addtask sign after do_install before do_deploy do_package
do_sign[prefuncs] += "check_deploy_keys"
do_sign[prefuncs] += "${@'check_boot_public_key' if d.getVar('GRUB_SIGN_VERIFY') == '1' else ''}"

fakeroot do_initrdsig() {
    if [ "${BUNDLE}" = "0" ]; then
        for compr in ${INITRAMFS_FSTYPES}; do
            if [ -e "${D}/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.${compr}${SB_FILE_EXT}" ]; then
                ln -sf "${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.${compr}${SB_FILE_EXT}" "${D}/boot/initrd${SB_FILE_EXT}"
            fi
        done
    else
        if [ -e "${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}${SB_FILE_EXT}" ]; then
            ln -sf "${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}${SB_FILE_EXT}" "${D}/boot/initrd${SB_FILE_EXT}"
        fi
    fi
}
addtask initrdsig after do_sign before do_deploy do_package

do_deploy() {
    install -d "${DEPLOYDIR}"

    if [ "${BUNDLE}" = "0" ]; then
        for compr in ${INITRAMFS_FSTYPES}; do
            if [ -e "${D}/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.${compr}${SB_FILE_EXT}" ]; then
                install -m 0644 "${D}/boot/${INITRAMFS_IMAGE}${INITRAMFS_EXT_NAME}.${compr}${SB_FILE_EXT}" \
                                               "${DEPLOYDIR}/${INITRAMFS_IMAGE}-${MACHINE}.${compr}${SB_FILE_EXT}"
            fi
        done
    else
        if [ -e "${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}${SB_FILE_EXT}" ]; then
            install -m 0644 "${D}/boot/${KERNEL_IMAGETYPE}-initramfs${INITRAMFS_EXT_NAME}${SB_FILE_EXT}" \
                                               "${DEPLOYDIR}/${INITRAMFS_IMAGE}-${MACHINE}.${INITRAMFS_FSTYPES}${SB_FILE_EXT}"
        fi
    fi
}
addtask deploy after do_install before do_package
