SUMMARY = "Signing tools for PE-COFF binaries"
DESCRIPTION = "Signing tools for PE-COFF binaries. \
Compliant with the PE and Authenticode specifications. \
(These serve a similar purpose to Microsoft's SignTool.exe, except for Linux.)"
HOMEPAGE = "https://github.com/rhboot/pesign"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI = "git://github.com/rhboot/pesign.git;protocol=https;name=sbsigntools;branch=main \
           file://0001-src-Makefile-disable-build-manpage.patch \
          "

SRCREV = "227435af461f38fc4abeafe02884675ad4b1feb4"
PV = "116+git${SRCPV}"

COMPATIBLE_HOST = "(i.86|x86_64|arm|aarch64).*-linux"

inherit pkgconfig systemd useradd

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --home /run/pesign --no-create-home --shell /sbin/nologin -g pesign pesign"
GROUPADD_PARAM:${PN} = "--system pesign"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "pesign.service"
SYSTEMD_AUTO_ENABLE = "disable"

DEPENDS = "popt efivar nspr nss util-linux-libuuid"

S = "${WORKDIR}/git"

do_install() {
    install -d -m 700 ${D}/etc/pki/pesign
    install -d -m 755 ${D}/${bindir}
    install -m 755 ${B}/src/authvar ${D}/${bindir}
    install -m 755 ${B}/src/pesign ${D}/${bindir}
    install -m 755 ${B}/src/pesum ${D}/${bindir}
    install -m 755 ${B}/src/client ${D}/${bindir}/pesign-client
    install -m 755 ${B}/src/efikeygen ${D}/${bindir}
    install -m 755 ${B}/src/pesigcheck ${D}/${bindir}
    install -d -m 700 ${D}/etc/pesign
    install -m 600 ${B}/src/pesign-users ${D}/etc/pesign/users
    install -m 600 ${B}/src/pesign-groups ${D}/etc/pesign/groups

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d -m 755 ${D}/${systemd_unitdir}/system
        install -m 644 ${B}/src/pesign.service ${D}/${systemd_unitdir}/system

        install -d ${D}/${sysconfdir}/tmpfiles.d
        install -m 644 ${B}/src/tmpfiles.conf ${D}/${sysconfdir}/tmpfiles.d/pesign.conf
    fi
}
