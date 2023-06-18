SUMMARY = "The utility to manipulate machines owner keys which managed in shim"
HOMEPAGE = "https://github.com/lcp/mokutil"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "openssl efivar keyutils virtual/crypt"

PV = "0.6.0+git${SRCPV}"

SRC_URI = "git://github.com/lcp/mokutil.git;branch=master;protocol=https \
          "

SRCREV = "ae59d89de763054e1724925e6a58b227fe97fa86"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

COMPATIBLE_HOST = '(i.86|x86_64|arm|aarch64).*-linux'

FILES:${PN} += "${datadir}/bash-completion/*"

RDEPENDS:${PN} = "openssl efivar keyutils"
