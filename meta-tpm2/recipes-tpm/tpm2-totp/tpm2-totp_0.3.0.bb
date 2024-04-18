SUMMARY = "Attest the trustworthiness of a device against a human using time-based one-time passwords"
DESCRIPTION = "This is a reimplementation of tpmtotp software for TPM 2.0 using \
the tpm2-tss software stack. Its purpose is to attest the trustworthiness of a \
device against a human using time-based one-time passwords (TOTP), facilitating \
the Trusted Platform Module (TPM) to bind the TOTP secret to the known trustworthy system state."
HOMEPAGE = "https://github.com/tpm2-software/tpm2-totp"
SECTION = "tpm"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ed23833e93c95173c8d8913745e4b4e1"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/v${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "1a8c83dc0d0dc58bd85a3fbfc9da6e39414c0d33f1a19886cde20f063f0c527b"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

DEPENDS = "tpm2-tss qrencode"

inherit autotools pkgconfig
