

keytool -genkeypair -keyalg RSA -dname "CN=Service, OU=Engineering, O=Red Hat, ST=Dublin, C=IE" -validity 365 -alias service -keypass KeyPass -keystore serviceKeystore.jks -storepass StorePass


karaf@root()> bundle:install -s mvn:com.mycompany/restdslNettyHttp/1.0
