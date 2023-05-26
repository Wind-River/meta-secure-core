require ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'linux-yocto-ima.inc', '', d)}
require ${@bb.utils.contains('DISTRO_FEATURES', 'modsign', 'linux-yocto-modsign.inc', '', d)}
