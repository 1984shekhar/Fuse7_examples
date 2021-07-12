 keytool -genkey -dname "CN=localhost, OU=Engineering, O=Progress, ST=Co. Dublin, C=IE" -validity 365 -alias CertAlias -keypass CertPassword -keystore CertName.jks -storepass CertPassword

 
 <groupId>com.mycompany</groupId>
  <artifactId>camel-cxf-ssl</artifactId>
  <version>1.0</version>
  
  bundle:install -s mvn:com.mycompany/camel-cxf-ssl/1.0
  
  
  	javax.jws;version="[0,3)",javax.xml.ws;version="[0,3)",
