SUMMARY = "Python bindings for TSS"
DESCRIPTION = "TPM2 TSS Python bindings for Enhanced System API (ESYS), \
Feature API (FAPI), Marshaling (MU), TCTI Loader (TCTILdr), TCTIs, policy, \
and RC Decoding (rcdecode) libraries."
HOMEPAGE = "https://github.com/tpm2-software/tpm2-pytss"
SECTION = "tpm"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"

PYPI_PACKAGE = "tpm2-pytss"

inherit pkgconfig pypi setuptools3

SRC_URI += "file://0001-scripts-update-regex-for-defines.patch"

SRC_URI[sha256sum] = "b8f15473422f377f59c7217dcd1479165cce62dfa33934ec976a278baf2e9efe"

DEPENDS = "python3-pkgconfig-native python3-pycparser-native tpm2-tss"

RDEPENDS:${PN} = "python3-asn1crypto python3-cryptography libtss2"
