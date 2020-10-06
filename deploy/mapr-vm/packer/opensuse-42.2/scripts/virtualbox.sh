#!/bin/sh

VERSION_FILE=/tmp/.vbox_version
if [ -e $VERSION_FILE ]; then
    zypper install -y virtualbox-guest-tools
fi
