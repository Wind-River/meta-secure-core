SUMMARY = "A PKCS#11 interface for AES KMS"
DESCRIPTION = "aws-kms-pkcs11 defines a provider for performing pkcs11 signing operations using AWS KMS"
HOMEPAGE = "https://github.com/tpm2-software/tpm2-pkcs11"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6baf08cd8be1559ca9cb84c1fe0509b"

DEPENDS = "\
    aws-sdk-cpp \
    curl \
    json-c \
    libp11 \
    openssl \
    p11-kit \
"

SRC_URI = "\
    git://github.com/JackOfMostTrades/aws-kms-pkcs11.git;branch=master;protocol=https \
    file://0001-locate-libs-with-pkg-config.patch \
"
SRCREV = "48fbe02bb31dcbe05e605f4dcb939165297c8d20"

S = "${WORKDIR}/git"

inherit pkgconfig

EXTRA_OEMAKE += "AWS_SDK_PATH=${STAGING_LIBDIR_NATIVE}/../"

do_install() {
   make ${EXTRA_OEMAKE} DESTDIR="${D}" install
}

FILES:${PN} += "\
    ${libdir}/pkcs11/* \
"

BBCLASSEXTEND = "native nativesdk"
