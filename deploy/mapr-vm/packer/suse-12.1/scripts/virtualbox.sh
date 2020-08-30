#!/bin/sh

VERSION_FILE=/tmp/.vbox_version
if [ -e $VERSION_FILE ]; then
    # Hack on SuSE to install Guest Additions
    sed -i 's/^allow_unsupported_modules.*$/allow_unsupported_modules 1/' /etc/modprobe.d/10-unsupported-modules.conf

    mount -o loop /tmp/VBoxGuestAdditions_$(cat $VERSION_FILE).iso /mnt
    sh /mnt/VBoxLinuxAdditions.run
    umount /mnt
    rm -rf /tmp/VBoxGuestAdditions_*.iso
fi
