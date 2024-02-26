SUMMARY = "Trusted Platform Module 2.0 tools"
DESCRIPTION = "Trusted Platform Module (TPM2.0) tools based on tpm2-tss."
SECTION = "tpm"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=a846608d090aa64494c45fc147cc12e3"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-tests-switch-to-python3.patch \
          "

SRC_URI[sha256sum] = "52c8bcbaadca082abfe5eb7ee4967d2d632d84b1677675f2f071b6d2ec22cec3"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl"

inherit autotools pkgconfig bash-completion
