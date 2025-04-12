SUMMARY = "Trusted Platform Module 2.0 tools"
DESCRIPTION = "Trusted Platform Module (TPM2.0) tools based on tpm2-tss."
SECTION = "tpm"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://docs/LICENSE;md5=a846608d090aa64494c45fc147cc12e3"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-tests-switch-to-python3.patch \
          "

SRC_URI[sha256sum] = "3810d36b5079256f4f2f7ce552e22213d43b1031c131538df8a2dbc3c570983a"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

DEPENDS = "tpm2-abrmd tpm2-tss openssl curl"

inherit autotools pkgconfig bash-completion
