sed -i "s/^.*requiretty/#Defaults requiretty/" /etc/sudoers

echo "METRIC=10" >> /etc/sysconfig/network-scripts/ifcfg-eth0

cat > /etc/hosts << EOF
127.0.0.1	localhost
::1			localhost
EOF

hostnamectl set-hostname localhost


yum install -y epel-release
yum clean all

yum -y install \
	kernel-devel-$(uname -r) kernel-headers-$(uname -r) \
	gcc make gcc-c++ zlib-devel openssl-devel \
	readline-devel sqlite-devel perl wget dkms nfs-utils bzip2 \
	iputils libsysfs nc python36-devel sdparm syslinux sysstat \
	vim htop mc curl telnet net-tools lsof nano drpm sshpass chrony \
	sudo python3-pycurl git openssh-clients python3-libselinux \
	zip unzip python3-PyMySQL

alternatives --set python /usr/bin/python3

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

# Disable firewalld
systemctl stop firewalld
systemctl disable firewalld
