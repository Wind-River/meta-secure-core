SUMMARY= "A collection of programs that provide support \
for TPM based attestation using the TPM quote mechanism. \
"
DESCRIPTION = "\
The TPM Quote Tools is a collection of programs that provide support \
for TPM based attestation using the TPM quote mechanism.  The manual \
page for tpm_quote_tools provides a usage overview. \
\
TPM Quote Tools has been tested with TrouSerS on Linux and NTRU on \
Windows XP.  It was ported to Windows using MinGW and MSYS. \
"
SECTION = "security/tpm"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ec30b01163d242ecf07d9cd84e3611f"

DEPENDS = "libtspi tpm-tools"

PV = "1.0.4+git"

SRC_URI = "\
    git://git.code.sf.net/p/tpmquotetools/tpm-quote-tools;branch=master \
"
SRCREV = "820084a6cbf15b4a7810414bfb9a5885de6679bf"

S = "${WORKDIR}/git"

inherit autotools
