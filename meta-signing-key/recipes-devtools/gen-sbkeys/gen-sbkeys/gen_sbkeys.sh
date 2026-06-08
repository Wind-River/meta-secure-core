#!/bin/bash
#
# SPDX-License-Identifier: MIT
#
#
# Set up UEFI Secure Boot keys. Generate keys and certificates, convert them
# into EFI Signature Lists, and sign them. By managing these keys, you can
# control what is considered trusted on your system.

set -eux

KEYS_PATH=${1:-./}
SUBJECT="/CN=OpenEmbedded/"

# The number used is just a GUID random number that has not special meaning.
# GUID (Globally Unique Identifier) is associated with the signature list. GUIDs
# in this context are used to uniquely identify the owner or the purpose of the
# keys within the EFI environment. This GUID can be used to distinguish
# different lists or purposes within the UEFI firmware settings
GUID="11111111-2222-3333-4444-123456789abc"

if [ ! -d "${KEYS_PATH}" ]; then
    mkdir -p "${KEYS_PATH}"
fi

if [ -f "${KEYS_PATH}"/PK.crt ]; then
    exit 0
fi

# Platform Key (PK): The root key in Secure Boot, which authorizes changes to
# the KEK
openssl req -x509 -sha256 -newkey rsa:2048 -subj "${SUBJECT}" \
    -keyout "${KEYS_PATH}"/PK.key -out "${KEYS_PATH}"/PK.crt \
    -nodes -days 3650
cert-to-efi-sig-list -g ${GUID} \
    "${KEYS_PATH}"/PK.crt "${KEYS_PATH}"/PK.esl
sign-efi-sig-list -c "${KEYS_PATH}"/PK.crt -k "${KEYS_PATH}"/PK.key \
    "${KEYS_PATH}"/PK "${KEYS_PATH}"/PK.esl "${KEYS_PATH}"/PK.auth

# Key Exchange Key (KEK): Allows for updates to the db and dbx lists
#
# db and dbx: Control lists for allowed and disallowed executable files and
# drivers
for key in KEK db dbx; do
    openssl req -x509 -sha256 -newkey rsa:2048 -subj "${SUBJECT}" \
        -keyout "${KEYS_PATH}"/${key}.key -out "${KEYS_PATH}"/${key}.crt \
        -nodes -days 3650
    cert-to-efi-sig-list -g ${GUID} \
        "${KEYS_PATH}"/${key}.crt "${KEYS_PATH}"/${key}.esl
    sign-efi-sig-list -c "${KEYS_PATH}"/PK.crt -k "${KEYS_PATH}"/PK.key \
        "${KEYS_PATH}"/${key} "${KEYS_PATH}"/${key}.esl "${KEYS_PATH}"/${key}.auth
done
