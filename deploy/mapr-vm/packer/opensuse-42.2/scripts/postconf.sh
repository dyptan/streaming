#!/bin/bash


# Create mapr users and add it to sudoers
useradd -m -u 5000 -U -G users,root mapr
echo "mapr:mapr" | chpasswd

echo "mapr ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers.d/mapr
echo "Defaults:mapr !requiretty" >> /etc/sudoers.d/mapr
chmod 0440 /etc/sudoers.d/mapr

useradd -m -u 5001 -U -G users mapruser1
echo "mapruser1:mapruser1" | chpasswd
useradd -m -u 5002 -U -G users mapruser2
echo "mapruser2:mapruser2" | chpasswd


# Add right ssh files to login without password
for user in root mapr
do
	home=$(eval echo ~$user)
	mkdir -p $home/.ssh/
	cp /tmp/authorized_keys.template $home/.ssh/authorized_keys
	cp /tmp/id_rsa.template $home/.ssh/id_rsa
	chmod 700 $home/.ssh/
	chmod 644 $home/.ssh/authorized_keys
	chmod 400 $home/.ssh/id_rsa
	chown -R $user:$user $home/.ssh/
done


# Make ssh faster by not waiting on DNS
echo "UseDNS no" >> /etc/ssh/sshd_config


# Flush network interfaces cache
# Otherwise eth0 would be linked to interface of build machine
# and network interfaces in vagants starts from eth1
echo '#' > /etc/udev/rules.d/70-persistent-net.rules


date > /etc/vagrant_box_build_time
