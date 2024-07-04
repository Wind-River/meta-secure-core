SUMMARY = "Software stack for TPM2."
DESCRIPTION = "OSS implementation of the TCG TPM2 Software Stack (TSS2) "
SECTION = "tpm"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=500b2e742befc3da00684d8a1d5fd9da"

SRC_URI = "https://github.com/tpm2-software/${BPN}/releases/download/${PV}/${BPN}-${PV}.tar.gz \
           file://0001-Do-not-create-user-and-group.patch \
          "

SRC_URI[sha256sum] = "e5f9b6055c29cb8d653ec7576853ff3863aa65dbd9cf4b3638ae8e8e7ce968ea"

UPSTREAM_CHECK_URI = "https://github.com/tpm2-software/${BPN}/releases"

DEPENDS = "openssl"

inherit autotools pkgconfig systemd useradd

PACKAGECONFIG ??= ""
PACKAGECONFIG[oxygen] = ",--disable-doxygen-doc, "
PACKAGECONFIG[fapi] = "--enable-fapi,--disable-fapi,curl json-c util-linux-libuuid"
PACKAGECONFIG[policy] = "--enable-policy,--disable-policy,curl json-c util-linux-libuuid"

EXTRA_OECONF += "--enable-static --with-udevrulesdir=${nonarch_base_libdir}/udev/rules.d/"
EXTRA_OECONF:remove = " --disable-static"

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "--system tss"
USERADD_PARAM:${PN} = "--system -M -d / -s /bin/false -g tss tss"

PROVIDES = "${PACKAGES}"
PACKAGES = " \
    ${PN} \
    ${PN}-dbg \
    ${PN}-doc \
    libtss2-mu \
    libtss2-mu-dev \
    libtss2-mu-staticdev \
    libtss2-tcti-device \
    libtss2-tcti-device-dev \
    libtss2-tcti-device-staticdev \
    libtss2-tcti-mssim \
    libtss2-tcti-mssim-dev \
    libtss2-tcti-mssim-staticdev \
    libtss2 \
    libtss2-dev \
    libtss2-staticdev \
"

FILES:libtss2-tcti-device = "${libdir}/libtss2-tcti-device.so.*"
FILES:libtss2-tcti-device-dev = " \
    ${includedir}/tss2/tss2_tcti_device.h \
    ${libdir}/pkgconfig/tss2-tcti-device.pc \
    ${libdir}/libtss2-tcti-device.so"
FILES:libtss2-tcti-device-staticdev = "${libdir}/libtss2-tcti-device.*a"

FILES:libtss2-tcti-mssim = "${libdir}/libtss2-tcti-mssim.so.*"
FILES:libtss2-tcti-mssim-dev = " \
    ${includedir}/tss2/tss2_tcti_mssim.h \
    ${libdir}/pkgconfig/tss2-tcti-mssim.pc \
    ${libdir}/libtss2-tcti-mssim.so"
FILES:libtss2-tcti-mssim-staticdev = "${libdir}/libtss2-tcti-mssim.*a"

FILES:libtss2-mu = "${libdir}/libtss2-mu.so.*"
FILES:libtss2-mu-dev = " \
    ${includedir}/tss2/tss2_mu.h \
    ${libdir}/pkgconfig/tss2-mu.pc \
    ${libdir}/libtss2-mu.so"
FILES:libtss2-mu-staticdev = "${libdir}/libtss2-mu.*a"

FILES:libtss2 = "${libdir}/libtss2*so.*"
FILES:libtss2-dev = " \
    ${includedir} \
    ${libdir}/pkgconfig \
    ${libdir}/libtss2*so"
FILES:libtss2-staticdev = "${libdir}/libtss*a"

FILES:${PN} = " \
    ${libdir}/udev \
    ${nonarch_base_libdir}/udev \
    ${sysconfdir}/tmpfiles.d \
    ${sysconfdir}/tpm2-tss \
    ${sysconfdir}/sysusers.d"
