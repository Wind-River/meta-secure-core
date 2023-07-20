require ${@bb.utils.contains('DISTRO_FEATURES', 'ima', '${BPN}-ima.inc', '', d)}
require ${@bb.utils.contains('DISTRO_FEATURES', 'modsign', '${BPN}-modsign.inc', '', d)}
