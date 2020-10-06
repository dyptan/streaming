#!/bin/sh

VERSION_FILE=/tmp/.vbox_version
if [ -e $VERSION_FILE ]; then
    mount -o loop /tmp/VBoxGuestAdditions_$(cat $VERSION_FILE).iso /mnt
    sh /mnt/VBoxLinuxAdditions.run
    umount /mnt
    rm -rf /tmp/VBoxGuestAdditions_*.iso
fi
