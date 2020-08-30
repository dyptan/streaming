#!/usr/bin/env bash
usage="Usage: configure_kerberos.sh <cluster name>\
  fqdn - new or existing \
 root/admin password: sets to 123456"
if [ $# -le 0 ]; then
  echo $usage
  exit 1
fi


FQDN=`hostname -f`
HOSTNAME=`echo $FQDN | awk -F "." ' {print $1} '` 
DOMAIN=`echo $FQDN | awk -F "." '{for (i=2; i<NF; i++) printf $i "."; print $NF}' `
DOMAINH=`echo $DOMAIN | awk '{print toupper($0)}'`
CLUSTER_NAME=$1
apt-get install -y unzip
javaH=`which java`
javaH=`ls -l $javaH | awk -F "->" ' {print $2} '`
PARAMETER1=`ls -l $javaH | awk -F "->" ' {print $2} ' | grep oracle`
if [ -n "$PARAMETER1" ]
  then
    DIR_JAVA=`dirname $PARAMETER1`
    wget https://www.dropbox.com/s/sjvm2kcnqt1zbux/UnlimitedJCEPolicyJDK7.zip?dl=0
    unzip UnlimitedJCEPolicyJDK7.zip\?dl\=0 
    cp UnlimitedJCEPolicy/* $DIR_JAVA/../lib/security
fi

#change hostname
echo "127.0.1.1 $FQDN $HOSTNAME" >> /etc/hosts
echo "$HOSTNAME" > /etc/hostname
service hostname start

export DEBIAN_FRONTEND=noninteractive
apt-get -y install krb5-kdc krb5-admin-server krb5-user libpam-krb5 libpam-ccreds auth-client-config ntp haveged

#edit /etc/krb5.conf
echo -e "[logging]\n default = FILE:/var/log/krb5libs.log\n kdc = FILE:/var/log/krb5kdc.log\n admin_server = FILE:/var/log/kadmind.log\n [libdefaults]\ndefault_realm = $DOMAINH \n dns_lookup_realm = false\n dns_lookup_kdc = false\n ticket_lifetime = 72h\n forwardable = true\n [realms]\n $DOMAINH = {\nkdc = $FQDN\nadmin_server = $FQDN\ndefault_domain = $DOMAIN\nacl_file = /etc/krb5kdc/kadm5.acl\n}\n[domain_realm]\n .$DOMAIN = $DOMAINH\n $DOMAIN = $DOMAINH" > /etc/krb5.conf

# Create KDC database

kdb5_util create -s -r $DOMAINH -P 12345

echo "*/admin@$DOMAINH    *"  > /etc/krb5kdc/kadm5.acl

#create principals for admin user
echo "addprinc -pw 123456 root/admin@$DOMAINH" | kadmin.local

#Restart Kerberos server and KDC

sudo /etc/init.d/krb5-kdc restart
sudo /etc/init.d/krb5-admin-server restart
#create principal for mapr user
echo "addprinc -randkey mapr/$CLUSTER_NAME" | kadmin -p root/admin -w 123456
echo "addprinc -randkey mapr/$FQDN" | kadmin -p root/admin -w 123456
#create principal for HTTP user
echo "addprinc -randkey HTTP/$FQDN" | kadmin -p root/admin -w 123456
#create keytab for mapr user
echo "ktadd -k /opt/mapr/conf/mapr.keytab mapr/$CLUSTER_NAME@$DOMAINH" | kadmin -p root/admin -w 123456
echo "ktadd -k /opt/mapr/conf/mapr.keytab mapr/$FQDN@$DOMAINH" | kadmin -p root/admin -w 123456
#create keytab for HTTP user
echo "ktadd -k /opt/mapr/conf/mapr.keytab HTTP/$FQDN@$DOMAINH" | kadmin -p root/admin -w 123456
sudo /etc/init.d/krb5-kdc restart
sudo /etc/init.d/krb5-admin-server restart
#configure sh
#/opt/mapr/server/configure.sh -C localhost -Z localhost -N $CLUSTER_NAME -RM localhost -HS localhost -f --create-user -F /mapr-disks/disks.list -secure -genkeys -kerberosEnable

#kinit -kt /opt/mapr/conf/mapr.keytab mapr/$CLUSTER_NAME@$DOMAINH

#maprlogin kerberos
