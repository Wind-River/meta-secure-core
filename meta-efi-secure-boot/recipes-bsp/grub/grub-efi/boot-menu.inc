# Note the initrd command becomes not working if the command for
# loading image is changed to the chainloader command instead of
# the linux command.

menuentry "Sample EFI boot" --unrestricted {
    savedefault
    set fallback=1
    search --no-floppy --label --set=root boot
    linux /bzImage root=LABEL=root ro rootwait
    initrd /initrd
}
