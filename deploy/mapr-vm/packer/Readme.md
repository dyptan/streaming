[Read about packer](https://www.packer.io/docs/)

[Original templates for packer](https://github.com/shiguredo/packer-templates)

---

#### How to build box
Example for Centos 7.2:
```
cd vmlab
packer build --parallel=false packer/centos-7.2/template.json
```
Created box will be stored in directory `packer_build/YYYYMMDD/`.

---

#### Old instruction to build VirtualBox boxes and convert it to QEMU boxes using vagrant-mutate

[Read about vagrant-mutate](https://github.com/sciurus/vagrant-mutate)

How to build box (for example centos 6.7):
```
cd packer/centos-6.7
packer build template.json
```
```
vagrant plugin install vagrant-mutate
```
```
cd vmlab
vagrant mutate packer/centos-6.7/centos-6-7-x64-virtualbox.box libvirt
vagrant box list
vagrant box repackage centos-6-7-x64-virtualbox libvirt 0
mv package.box centos-6-7-x64-libvirt.box
```
