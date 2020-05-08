setup ftp server:

# Install ftp server
```
sudo yum install vsftpd
```
# Check status
```
sudo systemctl status vsftpd.service
```
# Start if ftp server inactive
```
sudo systemctl start vsftpd.service
```
# create ftpfolder in user's home location.
```
mkdir /home/cpandey/ftpfolder
```
# create csp.txt file
```
[cpandey@cpandey ftpfolder]$ cat csp.txt 
I am camel-ftp poc
hello it's just a poc
[cpandey@cpandey ftpfolder]$ 
```
# Deploy in OpenShift environment:
```
mvn fabric8:deploy
```

# Logs
```
09:33:24.718 [main] INFO  o.a.tomcat.util.net.NioSelectorPool - Using a shared selector for servlet write/read
09:33:24.769 [main] INFO  o.s.b.c.e.t.TomcatEmbeddedServletContainer - Tomcat started on port(s): 8080 (http)
09:33:24.775 [main] INFO  org.mycompany.Application - Started Application in 27.256 seconds (JVM running for 31.833)
09:33:25.574 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpEndpoint - Using FactoryFinder: org.apache.camel.impl.DefaultFactoryFinder@77bc766b
09:33:25.577 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] DEBUG o.a.c.c.file.remote.FtpEndpoint - Parameters for Generic file process strategy {readLockDeleteOrphanLockFiles=true, readLockRemoveOnRollback=true, readLockCheckInterval=5000, readLock=none, readLockRemoveOnCommit=false, readLockTimeout=20000, readLockMarkerFile=true, fastExistsCheck=false, readLockLoggingLevel=DEBUG, readLockMinAge=0, readLockMinLength=1}
09:33:25.578 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] DEBUG o.a.c.c.file.remote.FtpEndpoint - Using Generic file process strategy: org.apache.camel.component.file.strategy.GenericFileNoOpProcessStrategy@7d441d1b
09:33:25.579 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpConsumer - prePollCheck on ftp://cpandey@10.67.116.37:21
09:33:25.579 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - sendNoOp
09:33:25.880 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - getCurrentDirectory()
09:33:26.179 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Current dir: /home/cpandey
09:33:26.180 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpConsumer - doPollDirectory from absolutePath: ftpfolder, dirName: null
09:33:26.180 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - changeCurrentDirectory(ftpfolder)
09:33:26.180 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Changing directory: ftpfolder
09:33:26.481 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpConsumer - Polling directory: ftpfolder
09:33:26.481 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - listFiles()
09:33:28.016 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpConsumer - Found 1 in directory: ftpfolder
09:33:28.016 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpConsumer - FtpFile[name=csp.txt, dir=false, file=true]
09:33:28.018 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - changeCurrentDirectory(/home/cpandey)
09:33:28.018 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Changing directory: /
09:33:28.317 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Changing directory: home
09:33:28.616 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Changing directory: cpandey
09:33:28.918 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] DEBUG o.a.c.c.file.remote.FtpConsumer - Took 3.038 seconds to poll: ftpfolder
09:33:28.946 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] DEBUG o.a.c.c.file.remote.FtpConsumer - Total 1 files to consume
09:33:28.946 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpConsumer - Processing file: RemoteFile[csp.txt]
09:33:28.950 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpConsumer - Retrieving file: ftpfolder/csp.txt from: ftp://cpandey@10.67.116.37/ftpfolder?passiveMode=true&password=xxxxxx
09:33:28.956 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] DEBUG o.a.c.c.f.r.FtpClientActivityListener - Downloading from host: ftp://cpandey@10.67.116.37:21 file: ftpfolder/csp.txt starting  (size: 41 B)
09:33:28.956 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - retrieveFile(ftpfolder/csp.txt)
09:33:28.956 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - getCurrentDirectory()
09:33:29.258 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Current dir: /home/cpandey
09:33:29.258 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - changeCurrentDirectory(ftpfolder)
09:33:29.258 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Changing directory: ftpfolder
09:33:29.559 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Client retrieveFile: csp.txt
09:33:30.765 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - changeCurrentDirectory(/home/cpandey)
09:33:30.766 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Changing directory: /
09:33:31.067 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Changing directory: home
09:33:31.367 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpOperations - Changing directory: cpandey
09:33:31.668 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] DEBUG o.a.c.c.f.r.FtpClientActivityListener - Downloading from host: ftp://cpandey@10.67.116.37:21 file: ftpfolder/csp.txt completed (size: 41 B) (took: 2.716 seconds)
09:33:31.669 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] TRACE o.a.c.c.file.remote.FtpConsumer - Retrieved file: ftpfolder/csp.txt from: ftp://cpandey@10.67.116.37/ftpfolder?passiveMode=true&password=xxxxxx
09:33:31.671 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] DEBUG o.a.c.c.file.remote.FtpConsumer - About to process file: RemoteFile[csp.txt] using exchange: Exchange[]
09:33:31.706 [Camel (MyCamel) thread #2 - ftp://cpandey@10.67.116.37/ftpfolder] INFO  simple-route - Headers>>>{breadcrumbId=ID-camel-ose-springboot-xml-6-wfbfj-1581413591809-0-1, CamelFileAbsolute=false, CamelFileAbsolutePath=ftpfolder/csp.txt, CamelFileLastModified=1581406980000, CamelFileLength=41, CamelFileName=csp.txt, CamelFileNameConsumed=csp.txt, CamelFileNameOnly=csp.txt, CamelFileParent=ftpfolder, CamelFilePath=ftpfolder/csp.txt, CamelFileRelativePath=csp.txt, CamelFtpReplyCode=226, CamelFtpReplyString=226 Transfer complete.
}........Body>>> I am camel-ftp poc
hello it's just a poc

```
