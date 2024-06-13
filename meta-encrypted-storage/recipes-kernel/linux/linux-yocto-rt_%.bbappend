include ${@bb.utils.contains('DISTRO_FEATURES', 'luks', 'linux-yocto-luks.inc', '', d)}
