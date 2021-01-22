1. Run SpringBoot application.
- mvn clean package
- java -Djavax.net.debug=ssl,handshake -jar target/hello1-0.0.1-SNAPSHOT.jar 
- 2 way ssl configuration in SpringBoot App.

{code}
[cpandey@cpandey hello1]$ cat ./src/main/resources/application.properties
server.port=8444

security.require-ssl=true

server.ssl.key-store-type=JKS
server.ssl.key-store=classpath:server2.jks
server.ssl.key-store-password=secret

server.ssl.trust-store=classpath:server1_trust.jks
server.ssl.trust-store-password=secret
server.ssl.client-auth=NEED
[cpandey@cpandey hello1]$ 

{code}

2. Now build war file for deploying EAP 7.2.7 + Red Hat Fuse 7.7. 
- mvn clean package
- cp target/camel-test-spring.war [EAP_HOME]/standalone/deployments/
- Here Keystore and truststore are set in httpEAP/src/main/webapp/META-INF/jboss-camel-context.xml
{code}
   <camel:sslContextParameters id="sslCtxParmsEV">
        <camel:keyManagers keyPassword="secret">
            <camel:keyStore password="secret" resource="/home/cpandey/Downloads/mycerts/client1.jks"/>
        </camel:keyManagers>
        <camel:trustManagers>
            <camel:keyStore password="secret" resource="/home/cpandey/Downloads/mycerts/client1_trust.jks"/>
        </camel:trustManagers>
    </camel:sslContextParameters>
{code}

3. Start EAP
- [EAP_HOME]/bin/standalone.sh

4. Check EAP logs.
17:06:32,484 INFO  [testoutput] (Camel (camelContext-fe2f82c3-3ec1-4d5d-a0f4-c090e22de7eb) thread #1 - timer://tester) Response is: Hello World!

5. So 2 way ssl works when keystore and truststore are set as camel's sslContextParameters.
