RAM = 10 * 1024
CPU = 4 

# ---- User configuration variables ----

# Ram size, number of cpus and playbook per node.
# DO NOT COMMENT LINES! Set 'skip' => true instead!
INSTANCES = [
  {:skip => false, :values => [RAM, CPU, 'nodes.yml']}, # node1
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node2
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node3
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node4
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node5
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node6
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node7
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node8
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node9
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node10
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node11
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node12
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node13
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node14
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node15
  {:skip => true, :values => [RAM, CPU, 'nodes.yml']}, # node16
]

# :-) Available values: 'private_network', 'public_network'
NETWORK_TYPE = 'private_network'

# :-) Other network settings
NETWORK_HOST = 'node%{index}.cluster.com'
NETWORK_IP = '192.168.33.%{index + 10}/24'
NETWORK_MAC = '0a:00:27:ab:00:%{ "%02d" % index }'

# :-) Repository proxy URL. Set empty value to disable proxy usage (i.e. on QA nodes).
# PROXY_URL = 'repo.mapr:3142/'
PROXY_URL = ''

# :-) BTW, you can set up workes with different OS then primary node. You only need
# run vagrant several times with slight modification of 'skip' parameters in INSTANCES
# Allowed values are: ubuntu1404, ubuntu1604, ubuntu1804, 
# centos68, centos72, centos73, centos76, centos77, centos78, centos81, 
# suse121, suse122, suse124, suse125, suse151
# centos68vault, centos72vault, centos73vault, opensuse422 (not supported)
OS = 'centos76'

# :-) Allowed values are: none, maprsasl, kerberos
SECURITY = 'maprsasl'

# :-) Allowed values are: 311, 401, 402, 410, 500, 510, 520, 521, 522, 600, 601, 610
# Also available with Artifactory: 620
MAPR_VERSION = 610

# :-) Allowed values are:
# nil (determine by MAPR_VERSION),
# ECOALL for ecosystem-all, ECO3 for ecosystem, ECO4 for ecosystem-4.x, ECO5 for ecosystem-5.x,
# MEP10 for MEP-1.0, MEP100 for MEP-1.0.0, MEP11 for MEP-1.1, MEP110 for MEP-1.1.0, MEP111 for MEP-1.1.1, MEP112 for MEP-1.1.2, MEP113 for MEP-1.1.3 MEP114 for MEP-1.1.4,
# MEP20 for MEP-2.0, MEP200 for MEP-2.0.0, MEP201 for MEP-2.0.1, MEP202 for MEP-2.0.2, MEP203 for MEP-2.0.3,
# MEP30 for MEP-3.0, MEP300 for MEP-3.0.0, MEP301 for MEP-3.0.1, MEP302 for MEP-3.0.2, MEP303 for MEP-3.0.3, MEP303 for MEP-3.0.4, MEP303 for MEP-3.0.5,
# MEP40 for MEP-4.0, MEP400 for MEP-4.0.0, MEP41 for MEP-4.1, MEP410 for MEP-4.1.0, MEP411 for MEP-4.1.1, MEP412 for MEP-4.1.2, MEP413 for MEP-4.1.3, MEP414 for MEP-4.1.4
# MEP50 for MEP-5.0, MEP500 for MEP-5.0.0, MEP501 for MEP-5.0.1, MEP502 for MEP-5.0.2,MEP503 for MEP-5.0.3, MEP504 for MEP-5.0.4, MEP505 for MEP-5.0.5
# MEP60 for MEP-6.0, MEP600 for MEP-6.0.0, MEP601 for MEP-6.0.1, MEP602 for MEP-6.0.2, 
# MEP61 for MEP-6.1, MEP610 for MEP-6.1.0, MEP611 for MEP-6.1.1
# MEP620 for MEP-6.2.0
# Also available with Artifactory:
# MEP603 for MEP-6.0.3,
# MEP612 for MEP-6.1.2,
# MEP621 for MEP-6.2.1,
# MEP630 for MEP-6.3.0, MEP631 for MEP-6.3.1
# MEP70 for MEP-7.0, MEP700 for MEP-7.0.0,
# MEPHD3 for MEP-on-hadoop-3
MAPR_ECO_VERSION = 'MEP61'

# MapRFS disk sizes in Gb. There will be created two disks per machine with this size
# Disks will be created under the path /var/lib/libvirt/images for libvirt OR
# under ./tmp directory inside Vagrantfile parent directory for virtualbox
MAPRFS_DISK_SIZE = 100

# Should VMLab install MapR core patch package or not
USE_MAPR_CORE_PATCH = false

# Don't touch this unless vagrant says so or you understand what you are doing
DEFAULT_INTERFACE_MANUAL = nil

# Change cache mode for the guest machine
# Allowed values are: writethrough, writeback, none, unsafe
CACHE_MODE = 'writethrough'

# Enable folder sync to the guest machine in /_host directory
# Allowed values are: nil (disable sync), 'rsync' (copy files to guest on startup),
# 'nfs' (realtime file sync), 'nfs4' (same as 'nfs' but supports file locking mechanism)
SYNC_TYPE = nil
SYNC_HOST_PATH = './sync_node%{index}'

# :-) Port forwarding configuration
PORT_FORWARDING = [
  { :instance => 1, :guest =>  8088, :host =>  8088 }, # RM UI
  { :instance => 1, :guest =>  8090, :host =>  8090 }, # RM UI / secure
  { :instance => 1, :guest => 18088, :host => 18088 }, # HS UI
  { :instance => 1, :guest => 18090, :host => 18090 }, # HS UI / secure
  { :instance => 1, :guest =>  8042, :host =>  8042 }, # NM UI / 1st node
  { :instance => 1, :guest =>  8044, :host =>  8044 }, # NM UI / 1st node / secure
  { :instance => 2, :guest =>  8042, :host =>  8242 }, # NM UI / 2nd node
  { :instance => 2, :guest =>  8044, :host =>  8244 }, # NM UI / 2nd node / secure
  { :instance => 3, :guest =>  8042, :host =>  8342 }, # NM UI / 3rd node
  { :instance => 3, :guest =>  8044, :host =>  8344 }, # NM UI / 3rd node / secure
  { :instance => 1, :guest =>  4040, :host =>  4040 }, # Spark UI
  { :instance => 1, :guest => 18080, :host => 18080 }, # Spark HS
  { :instance => 1, :guest => 18480, :host => 18480 }, # Spark HS / secue
  { :instance => 1, :guest =>  8888, :host =>  8888 }, # Hue
]

# Use remote libvirt host for VMs. If not provided, connect to local
LIBVIRT_HOST = nil
LIBVIRT_USER = nil
