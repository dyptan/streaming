apt-get update
apt-get upgrade -y
apt-get install -y \
	linux-headers-$(uname -r) build-essential \
	zlib1g-dev libssl-dev libreadline-gplv2-dev libyaml-dev \
	dkms \
	nfs-common \
	syslinux \
	lsb \
	sdparm \
	python-pycurl python-mysqldb \
	sshpass \
	gcc make g++ libssl-dev libreadline-dev libsqlite3-dev perl sysfsutils netcat python-dev \
	vim mc htop curl git nano wget bzip2 iputils-ping iputils-tracepath telnet net-tools lsof zip unzip ntp

# Require by ansible APT module
apt-get install -y python-apt

# for Hue
apt-get install -y libmysqlclient-dev libmysqlclient18 libsasl2-dev libsasl2-modules-gssapi-mit libssl0.9.8 libxslt1.1

# Disable automatic updates, as it may cause provisioninig issues (apt lock)
cat > /etc/apt/apt.conf.d/99disable-periodic <<EOF
APT::Periodic::Update-Package-Lists "0";
APT::Periodic::Download-Upgradeable-Packages "0";
APT::Periodic::AutocleanInterval "0";
APT::Periodic::Unattended-Upgrade "0";
EOF


# libicu48 for impala on ubuntu 14.04
wget 'http://launchpadlibrarian.net/155496722/libicu48_4.8.1.1-13%2Bnmu1ubuntu1_amd64.deb' -P /tmp
dpkg -i /tmp/libicu48*.deb


# Create mapr users and add it to sudoers
useradd -m -u 5000 -U -G users,root -s /bin/bash mapr
echo "mapr:mapr" | chpasswd

echo "mapr ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers.d/mapr
echo "Defaults:mapr !requiretty" >> /etc/sudoers.d/mapr
chmod 0440 /etc/sudoers.d/mapr

useradd -m -u 5001 -U -G users -s /bin/bash mapruser1
echo "mapruser1:mapruser1" | chpasswd
useradd -m -u 5002 -U -G users -s /bin/bash mapruser2
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


echo "UseDNS no" >> /etc/ssh/sshd_config


date > /etc/vagrant_box_build_time
