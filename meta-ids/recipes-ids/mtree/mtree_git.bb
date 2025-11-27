SUMMARY = "BSD directory hierarchy mapping tool"
DESCRIPTION = "mtree compares a file hierarchy against a specification, creates a specification for a file hierarchy, or modifies a specification."

SECTION = "utils"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=bb19ea4eac951288efda4010c5c669a8"

PV = "1.0.4+git"

SRC_URI = "git://github.com/archiecobbs/mtree-port.git;branch=master;protocol=https \
           file://mtree-getlogin.patch \
           file://0001-compat-glibc-2.33.patch \
           file://0002-Fix-bug-with-timestamps-after-2038-issue-16.patch \
           "
SRCREV = "9b3ca63a04634446b16f615d1a23da0e2451848b"

DEPENDS = "openssl"

inherit autotools
