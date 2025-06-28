# SPDX-License-Identifier: MIT

SUMMARY = "Generate Signing UEFI keys for Secure Boot"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS = "bash-native coreutils-native efitools-native openssl-native"

SRC_URI = "file://gen_sbkeys.sh"

S = "${UNPACKDIR}"

do_patch[noexec] = "1"
do_compile[noexec] = "1"
do_configure[noexec] = "1"
do_install[nostamp] = "1"

python do_install() {
    keys_dir = d.getVar('SBSIGN_KEYS_DIR')
    if not keys_dir:
        bb.warn("SBSIGN_KEYS_DIR is not set. Skipping generating keys...")
        return

    keys_to_check = [
        keys_dir + "/PK.esl",
        keys_dir + "/KEK.esl",
        keys_dir + "/db.esl",
        keys_dir + "/dbx.esl",
        keys_dir + "/db.key",
        keys_dir + "/db.crt",
    ]

    missing_keys = [f for f in keys_to_check if not os.path.exists(f)]

    if not missing_keys:
        bb.debug(2, "All UEFI keys found in '%s' to sign binaries'" % keys_dir)
        return

    gen_sbkeys = d.getVar('UNPACKDIR') + "/gen_sbkeys.sh"

    import subprocess
    bb.debug(2, "Calling '%s' to generate UEFI keys in path: '%s'" % (gen_sbkeys, keys_dir))
    cmd = "%s %s" % (gen_sbkeys, keys_dir)
    subprocess.Popen(cmd, shell=True)
}

FILES:${PN} += "${SBSIGN_KEYS_DIR}/db.key"
FILES:${PN} += "${SBSIGN_KEYS_DIR}/db.crt"
