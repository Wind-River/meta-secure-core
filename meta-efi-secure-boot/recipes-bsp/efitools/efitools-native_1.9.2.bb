require efitools.inc

inherit native

EXTRA_OEMAKE:append = " \
    INCDIR_PREFIX='${STAGING_DIR_NATIVE}' \
    CRTPATH_PREFIX='${STAGING_DIR_NATIVE}' \
"
