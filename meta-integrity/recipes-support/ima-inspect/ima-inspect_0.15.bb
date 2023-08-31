SUMMARY = "Tool to get a human-readable representation of IMA file attributes"
DESCRIPTION = "ima_inspect is a small program that allows to give a human-readable \
representation of the contents of the extended attributes (xattrs) that the Linux IMA \
security subsystem creates and manages for files."
HOMEPAGE = "https://github.com/mgerstner/ima-inspect"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a23a74b3f4caf9616230789d94217acb"

DEPENDS = "attr ima-evm-utils tclap"

SRC_URI = "git://github.com/mgerstner/ima-inspect.git;branch=master;protocol=https"
SRCREV = "2e248ce53728f5b2bfc34a934a19636b84f8eb88"

S = "${WORKDIR}/git"

inherit autotools pkgconfig
