# Set a default root specifier.
inherit user-key-store

python do_sign:prepend:class-target() {
    bb.build.exec_func("check_deploy_keys", d)
    if d.getVar('GRUB_SIGN_VERIFY') == '1':
        bb.build.exec_func("check_boot_public_key", d)
}

fakeroot python do_sign:class-target() {
    image_dir = d.getVar('D')
    efi_files_path = d.getVar('EFI_FILES_PATH')
    dir = image_dir + efi_files_path + '/'

    uks_bl_sign(dir + 'grub.cfg', d)
}

python do_sign() {
}

addtask sign after do_install before do_deploy do_package

fakeroot do_chownboot() {
    chown root:root -R "${D}${EFI_FILES_PATH}/grub.cfg${SB_FILE_EXT}"
}

addtask chownboot after do_deploy before do_package

do_deploy:append:class-target() {
    # Deploy the stacked grub configs.
    install -m 0600 "${D}${EFI_FILES_PATH}/grub.cfg${SB_FILE_EXT}" "${DEPLOYDIR}"
}

FILES:${PN} += "${EFI_FILES_PATH}/grub.cfg${SB_FILE_EXT}"
