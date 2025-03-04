# Don't build with pulseaudio support for native builds since no
# pulseaudio-native exists. For some reason, pulsaudio is backfilled
# into DSITRO_FEATURES for class-native
PACKAGECONFIG:remove:class-native = "pulseaudio"

BBCLASSEXTEND = "native"
