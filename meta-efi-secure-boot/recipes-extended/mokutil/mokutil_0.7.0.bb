SUMMARY = "The utility to manipulate machines owner keys which managed in shim"
HOMEPAGE = "https://github.com/lcp/mokutil"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "openssl efivar keyutils virtual/crypt"

SRC_URI = "git://github.com/lcp/mokutil.git;branch=master;protocol=https \
          "

SRCREV = "c361087100fbb6955f32a9f364dee21b24724fb4"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

COMPATIBLE_HOST = '(i.86|x86_64|arm|aarch64).*-linux'

FILES:${PN} += "${datadir}/bash-completion/*"

RDEPENDS:${PN} = "openssl efivar keyutils"
