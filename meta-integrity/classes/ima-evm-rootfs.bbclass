# No default! Either this or IMA_EVM_PRIVKEY/IMA_EVM_X509 have to be
# set explicitly in a local.conf before activating ima-evm-rootfs.
# To use the insecure (because public) example keys, use
# IMA_EVM_KEY_DIR = "${IMA_EVM_BASE}/data/debug-keys"
IMA_EVM_KEY_DIR ?= "${IMA_KEYS_DIR}"

# Private key for IMA signing. The default is okay when
# using the example key directory.
IMA_EVM_PRIVKEY ?= "${IMA_EVM_KEY_DIR}/x509_ima.key"

# Public part of certificates (used for both IMA and EVM).
# The default is okay when using the example key directory.
IMA_EVM_X509 ?= "${IMA_EVM_KEY_DIR}/x509_ima.der"

# Root CA to be compiled into the kernel, none by default.
# Must be the absolute path to a der-encoded x509 CA certificate
# with a .x509 suffix. See linux-%.bbappend for details.
#
# ima-local-ca.x509 is what ima-gen-local-ca.sh creates.
IMA_EVM_ROOT_CA ?= ""

# Sign all regular files by default.
IMA_EVM_ROOTFS_SIGNED ?= ". -type f"
# Hash nothing by default.
IMA_EVM_ROOTFS_HASHED ?= ". -depth 0 -false"

# Mount these file systems (identified via their mount point) with
# the iversion flags (needed by IMA when allowing writing).
IMA_EVM_ROOTFS_IVERSION ?= ""

ima_evm_sign_rootfs () {
    cd ${IMAGE_ROOTFS}

    # Note that "i_version" is documented in "man mount" only for ext4,
    # whereas "iversion" is said to be filesystem-independent. In practice,
    # there is only one MS_I_VERSION flag in the syscall and ext2/ext3/ext4
    # all support it.
    #
    # coreutils translates "iversion" into MS_I_VERSION. busybox rejects
    # "iversion" and only understands "i_version". systemd only understands
    # "iversion". We pick "iversion" here for systemd, whereas rootflags
    # for initramfs must use "i_version" for busybox.
    #
    # Deduplicates iversion in case that this gets called more than once.
    if [ -f etc/fstab ]; then
       perl -pi -e 's;(\S+)(\s+)(${@"|".join((d.getVar("IMA_EVM_ROOTFS_IVERSION", True) or "no-such-mount-point").split())})(\s+)(\S+)(\s+)(\S+);\1\2\3\4\5\6\7,iversion;; s/(,iversion)+/,iversion/;' etc/fstab
    fi

    # To sign a key with ima, it must use root account, thus it should make
    # the user had the sudo right and there was no password for this user.
    # To set the user without password when do sudo, please edit file /etc/sudoers
    # to add a new entry should look like
    # myuser ALL=(ALL) NOPASSWD:ALL for a single user.

    # Sign file with private IMA key. EVM not supported at the moment.
     find ${IMA_EVM_ROOTFS_SIGNED} -print0 | xargs -0 -L 1 --no-run-if-empty --verbose evmctl ima_sign --hashalgo sha256 --key ${IMA_EVM_PRIVKEY} --pass="${RPM_FSK_PASSWORD}"
}

# Signing must run as late as possible in the do_rootfs task.
# IMAGE_PREPROCESS_COMMAND runs after ROOTFS_POSTPROCESS_COMMAND, so
# append (not prepend!) to IMAGE_PREPROCESS_COMMAND, and do it with
# :append instead of += because :append gets evaluated later. In
# particular, we must run after prelink_image in
# IMAGE_PREPROCESS_COMMAND, because prelinking changes executables.

IMAGE_PREPROCESS_COMMAND:append = " ima_evm_sign_rootfs;"

# evmctl must have been installed first.
do_rootfs[depends] += "ima-evm-utils-native:do_populate_sysroot"

IMAGE_CMD_TAR = "tar --xattrs --xattrs-include=*"
do_image_tar[depends] += "tar-replacement-native:do_populate_sysroot"
EXTRANATIVEPATH += "tar-native"


#USER_CLASSES:remove = " image-mklibs image-prelink"
IMAGE_PREPROCESS_COMMAND:remove = " mklibs_optimize_image;"
IMAGE_PREPROCESS_COMMAND:remove = " prelink_setup;"
IMAGE_PREPROCESS_COMMAND:remove = " prelink_image;"
