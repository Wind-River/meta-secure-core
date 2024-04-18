SUMMARY = "Provider for integration of TPM 2.0 to OpenSSL 3.X"
DESCRIPTION = "The tpm2-openssl project implements a provider \
that integrates the Trusted Platform Module (TPM 2.0) operations \
to the OpenSSL 3.x, which is the next version of OpenSSL after 1.1.1."
HOMEPAGE = "https://github.com/tpm2-software/tpm2-openssl"
SECTION = "tpm"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b75785ac083d3c3ca04d99d9e4e1fbab"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz"

SRC_URI[sha256sum] = "2ee15da2dceae1466ffba868e75a00b119d752babc1b6a2792286336a3324fb0"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

DEPENDS = "autoconf-archive-native tpm2-tss openssl"

inherit autotools pkgconfig

FILES:${PN} = "${libdir}/ossl-modules/*"
