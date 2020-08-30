#!/bin/sh

VERSION_FILE=/tmp/.vbox_version
if [ -e $VERSION_FILE ]; then
    apt-get -y install virtualbox-guest-utils
fi
