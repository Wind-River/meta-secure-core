SUMMARY = "Python bindings for TSS"
DESCRIPTION = "TPM2 TSS Python bindings for Enhanced System API (ESYS), \
Feature API (FAPI), Marshaling (MU), TCTI Loader (TCTILdr), TCTIs, policy, \
and RC Decoding (rcdecode) libraries."
HOMEPAGE = "https://github.com/tpm2-software/tpm2-pytss"
SECTION = "tpm"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"

PYPI_PACKAGE = "tpm2-pytss"

inherit pkgconfig pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "20071129379656f5f3c3bc16d364612672b147d81191fb4eb9f9ff9fbee48410"

DEPENDS = "python3-pkgconfig-native python3-pycparser-native \
           python3-setuptools-scm-native python3-cryptography-native \
           python3-asn1crypto-native python3-cffi-native \
           tpm2-tss \
          "

RDEPENDS:${PN} = "python3-asn1crypto python3-cryptography python3-cffi libtss2"
