SUMMARY = "IMA/EVM signing utility"
DESCRIPTION = "The evmctl utility can be used for producing and verifying \
digital signatures, which are used by Linux kernel integrity subsystem (IMA/EVM). \
It can be also used to import keys into the kernel keyring"
HOMEPAGE = "https://sourceforge.net/projects/linux-ima/"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "\
    git://git.code.sf.net/p/linux-ima/ima-evm-utils;branch=master \
    file://0001-Install-evmctl-to-sbindir-rather-than-bindir.patch \
"
SRCREV = "1803accc3ff8a4e347f9a93e68b14ed9cbbb56f7"


DEPENDS = "openssl attr keyutils"

inherit pkgconfig autotools

EXTRA_OECONF = "--with-kernel-headers=${STAGING_KERNEL_DIR}"
EXTRA_OECONF += "MANPAGE_DOCBOOK_XSL=0"

do_compile:append() {
    # Remove build host references
    find ${B}/src -name '*.h' | xargs sed -i -e 's|${STAGING_KERNEL_DIR}/||g'
}

RDEPENDS:${PN}:class-target = "libcrypto libattr keyutils"

BBCLASSEXTEND = "native nativesdk"
