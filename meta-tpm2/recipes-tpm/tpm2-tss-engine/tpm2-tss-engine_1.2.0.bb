SUMMARY = "A cryptographic engine for OpenSSL for TPM 2.0"
DESCRIPTION = "The tpm2-tss-engine project implements a cryptographic engine \
for OpenSSL for Trusted Platform Module (TPM 2.0) using the tpm2-tss software \
stack that follows the Trusted Computing Groups (TCG) TPM Software Stack (TSS 2.0). \
It uses the Enhanced System API (ESAPI) interface of the TSS 2.0 for downwards communication. \
It supports RSA decryption and signatures as well as ECDSA signatures."
HOMEPAGE = "https://github.com/tpm2-software/tpm2-tss-engine"
SECTION = "tpm"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b3ab643b9ce041de515d1ed092a36d4"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-Fix-mismatch-of-OpenSSL-function-signatures-that-cau.patch \
          "

SRC_URI[sha256sum] = "3c94fef110dd3630b3c28c5875febba76b7d5ba2fcc04a14c4a30f5d2157c265"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v?(?P<pver>\d+(\.\d+)+)"

DEPENDS = "tpm2-tss openssl bash-completion"

inherit autotools pkgconfig

FILES:${PN} += "${libdir}/engines-3/* \
                ${datadir}/bash-completion/* \
               "
INSANE_SKIP:${PN} = "dev-so"
