1. Generated ssh keys with :

ssh-keygen -m PEM -t rsa
ssh-keygen -t rsa

2. Copied content of id_rsa.pub to ~/.ssh/authorized_keys with command

ssh-copy-id cpandey@192.168.1.10


3. In configuration file jboss-eap-7.2.8/modules/system/layers/fuse/org/apache/sshd/main/module.xml add third dependency

<module name="org.bouncycastle"/>

4. Check EAP logs for any error, also if everthing works fine then there should be empty file created in home directory of user.

[cpandey@cpandey ~]$ ls -ltr |grep 123
-rw-rw-r--.  1 cpandey cpandey     0 Jan 14 15:59 12345.txt
[cpandey@cpandey ~]$ 
