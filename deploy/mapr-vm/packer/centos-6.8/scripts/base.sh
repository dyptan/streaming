sed -i "s/^.*requiretty/#Defaults requiretty/" /etc/sudoers

sed -i -e "/HWADDR/d" -e "/UUID/d" /etc/sysconfig/network-scripts/ifcfg-eth0
echo "METRIC=10" >> /etc/sysconfig/network-scripts/ifcfg-eth0

cat > /etc/hosts << EOF
127.0.0.1	localhost
::1			localhost
EOF

#hostnamectl set-hostname localhost
hostname "localhost"
sed -i "s/HOSTNAME=.*/HOSTNAME=localhost/g" /etc/sysconfig/network


mv /tmp/epel.repo /etc/yum.repos.d/epel.repo
yum clean all

# CentOS 6.8 bug 0010930
yum upgrade -y nss-softokn

yum -y install \
	kernel-devel-$(uname -r) kernel-headers-$(uname -r) \
	gcc make gcc-c++ zlib-devel openssl-devel \
	readline-devel sqlite-devel perl wget dkms nfs-utils bzip2 \
	vim nano deltarpm mc htop top curl lsof telnet sshpass ntp \
	sudo python-pycurl git openssh-clients libselinux-python \
	zip unzip MySQL-python


# Create mapr users and add it to sudoers
useradd -m -u 5000 -U -G users,root,wheel mapr
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

# Disable firewall
service iptables stop
service ip6tables stop
chkconfig iptables off
chkconfig ip6tables off

#NTP config
ntpdate 0.rhel.pool.ntp.org
service ntpd restart


echo "cleaning up udev rules"
rm /etc/udev/rules.d/70-persistent-net.rules
mkdir /etc/udev/rules.d/70-persistent-net.rules
rm -rf /dev/.udev/
rm /lib/udev/rules.d/75-persistent-net-generator.rules
