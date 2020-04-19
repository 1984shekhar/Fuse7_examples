# Example of using Apache Camel HTTP4 component with SPNEGO/Kerberos Authentication

This is the demonstration of authentication against kerberos-enabled Apache HTTPD
with camel-http4 component and Java's Krb5LoginModule. 

## Prerequisites

This instructions are for setting up a kerberos server and Apache HTTPD on a Fedora 31 Server

### Kerberos
Install Kerberos and httpd packages
```
# dnf install krb5-libs krb5-server krb5-workstation mod_auth_gssapi httpd 
```
Edit the /etc/krb5.conf and /var/kerberos/krb5kdc/kdc.conf to reflect your realm name and domain to realm mappings. 

For example:

```
# cat  /etc/krb5.conf

# To opt out of the system crypto-policies configuration of krb5, remove the
# symlink at /etc/krb5.conf.d/crypto-policies which will not be recreated.
includedir /etc/krb5.conf.d/

[logging]
 default = FILE:/var/log/krb5libs.log
 kdc = FILE:/var/log/krb5kdc.log
 admin_server = FILE:/var/log/kadmind.log

[libdefaults]
 dns_lookup_realm = false
 ticket_lifetime = 24h
 renew_lifetime = 7d
 forwardable = true
 rdns = false
 default_realm = EXAMPLE.COM
 default_ccache_name = KEYRING:persistent:%{uid}
 allow_weak_crypto = true

[realms]
 EXAMPLE.COM = {
  kdc = kerberos.example.com:88
  admin_server = kerberos.example.com:749
 }

[domain_realm]
 .example.com = EXAMPLE.COM
 example.com = EXAMPLE.COM

[kdc]                                      
 profile = /var/kerberos/krb5kdc/kdc.conf  

[pam]
 debug = false
 ticket_lifetime = 36000
 renew_lifetime = 36000
 forwardable = true
 krb4_convert = false

```

```
# cat /var/kerberos/krb5kdc/kdc.conf
[kdcdefaults]
 kdc_ports = 88
 kdc_tcp_ports = 88
 kdc_ports = 88                                                                 
 acl_file = /var/kerberos/krb5kdc/kadm5.acl                                     
 dict_file = /usr/dict/words                                                    
 admin_keytab = /var/kerberos/krb5kdc/kadm5.keytab    

[realms]
 EXAMPLE.COM = {
  master_key_type = aes256-cts                                              
  database_name = /var/kerberos/krb5kdc/principal                               
  admin_keytab = /var/kerberos/krb5kdc/kadm5.keytab                             
  supported_enctypes = aes256-cts:normal aes128-cts:normal arcfour-hmac:normal camellia256-cts:normal camellia128-cts:normal
  kadmind_port = 749                                                            
  acl_file = /var/kerberos/krb5kdc/kadm5.acl                                    
  dict_file = /usr/dict/words
 }

```
Edit the /var/kerberos/krb5kdc/kadm5.acl to determine which principals have access to the kerberos database

```
# cat /var/kerberos/krb5kdc/kadm5.acl
*/admin@EXAMPLE.COM	*
```
Create the kerberos database using the kdb5_util command:

```
# kdb5_util create -s 
Loading random data
Initializing database '/var/kerberos/krb5kdc/principal' for realm 'EXAMPLE.COM',
master key name 'K/M@EXAMPLE.COM'
You will be prompted for the database Master Password.
It is important that you NOT FORGET this password.
Enter KDC database master key: 
Re-enter KDC database master key to verify: 
```

Start the Kerberos services:
```
# service krb5kdc start 
Redirecting to /bin/systemctl start krb5kdc.service
# service kadmin start
Redirecting to /bin/systemctl start kadmin.service
```
Add Kerberos principal for user1:
```
# kadmin.local 
Authenticating as principal root/admin@EXAMPLE.COM with password.
kadmin.local:  addprinc user1
WARNING: no policy specified for user1@EXAMPLE.COM; defaulting to no policy
Enter password for principal "user1@EXAMPLE.COM": 
Re-enter password for principal "user1@EXAMPLE.COM": 
Principal "user1@EXAMPLE.COM" created.
kadmin.local:  quit
```

Add Kerberos service principal for httpd

```
# kadmin.local 
kadmin.local:  addprinc HTTP/mysite.example.com
WARNING: no policy specified for HTTP/mysite.example.com@EXAMPLE.COM; defaulting to no policy
Enter password for principal "HTTP/mysite.example.com@EXAMPLE.COM": 
Re-enter password for principal "HTTP/mysite.example.com@EXAMPLE.COM": 
Principal "HTTP/mysite.example.com@EXAMPLE.COM" created.
```
```
kadmin.local:  ktadd -k /etc/httpd/krb5.keytab HTTP/mysite.example.com
Entry for principal HTTP/mysite.example.com with kvno 2, encryption type aes256-cts-hmac-sha1-96 added to keytab WRFILE:/etc/httpd/krb5.keytab.
Entry for principal HTTP/mysite.example.com with kvno 2, encryption type aes128-cts-hmac-sha1-96 added to keytab WRFILE:/etc/httpd/krb5.keytab.
Entry for principal HTTP/mysite.example.com with kvno 2, encryption type arcfour-hmac added to keytab WRFILE:/etc/httpd/krb5.keytab.
Entry for principal HTTP/mysite.example.com with kvno 2, encryption type camellia256-cts-cmac added to keytab WRFILE:/etc/httpd/krb5.keytab.
Entry for principal HTTP/mysite.example.com with kvno 2, encryption type camellia128-cts-cmac added to keytab WRFILE:/etc/httpd/krb5.keytab.
kadmin.local:  quit
```
Change the ownership of /etc/httpd/krb5.keytab to  user apache
```
# chown apache.apache /etc/httpd/krb5.keytab
```
Test you can get a ticket:
```
# kinit user1
Password for user1@EXAMPLE.COM: 
```
Prove you have a ticket:
```
# klist
Ticket cache: KEYRING:persistent:0:krb_ccache_OgCNJp6
Default principal: user1@EXAMPLE.COM

Valid starting       Expires              Service principal
04/10/2020 23:21:42  04/11/2020 23:21:42  krbtgt/EXAMPLE.COM@EXAMPLE.COM

```
### Apache HTTPD

Create a VirtualHost for test authentication 
```
# cat /etc/httpd/conf.d/mysite.example.com.conf

<VirtualHost *:80>
    ServerAdmin webmaster@example.com
    DocumentRoot /var/www/mysite.example.com/html
    ServerName mysite.example.com
    ErrorLog logs/mysite.example.com-error_log
    CustomLog logs/mysite.example.com-access_log common

   <Location "/">
        AuthType GSSAPI
        AuthName "Kerberos Login"
        GssapiCredStore keytab:/etc/httpd/krb5.keytab
        GssapiNameAttributes AUTHIND auth-indicators
        Require valid-user
        ErrorDocument 401 /error/error.html
   </Location>
</VirtualHost>

```
Create index.html for a successful authentication
```html
# cat /var/www/mysite.example.com/html/index.html 
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
<html>
<head>
    <title>Congrats!!!</title>
    <meta charset="UTF-8">
</head>
<body>
<div class="container">
    <div class="page-header">
        <h1>You've been Authenticated! Enjoy!</h1>
    </div>
</div>
</body>
</html>
```

Create error.html for a unsuccessful authentication
```html
# cat /var/www/mysite.example.com/html/error/error.html 
<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">
<html><head>
<title>401 Unauthorized</title>
</head><body>
<h1>Unauthorized</h1>
<p>This server could not verify that you
are authorized to access the document
requested.  Either you supplied the wrong
credentials (e.g., bad password), or your
browser doesn't understand how to supply
the credentials required.</p>
<p>Additionally, a 401 Unauthorized
error was encountered while trying to use an ErrorDocument to handle the
request.</p>
</body></html>
```
```
Add an entry on /etc/hosts for kerberos.example.com and mysite.example.com on both client and server machine like:

192.168.122.194 mysite.example.com kerberos.example.com

OR

# if setup is in localhost
127.0.0.1 mysite.example.com kerberos.example.com
```
```
#start httpd service
systemctl status httpd.service
```
## Run the example
Ajust the location of `krb5.conf` and `login.conf` in `KerberosCamelHttp4Application.java`

```java
        System.setProperty("java.security.auth.login.config", "/home/ldemasi/login.conf");
        System.setProperty("java.security.krb5.conf", "/home/ldemasi/krb5.conf");
```


Now that you have everything setup, build and run the sample:

```shell script
$ mvn clean package
$ java -jar target/camel-http4-spnego-1.0-SNAPSHOT.jar
 ```
In the log you should see 

```
2020-04-11 01:15:23.015 DEBUG 3366 --- [timer://trigger] o.a.camel.component.http4.HttpProducer   : Executing http GET method: http://mysite.example.com
2020-04-11 01:15:23.029 DEBUG 3366 --- [timer://trigger] o.a.h.client.protocol.RequestAddCookies  : CookieSpec selected: default
2020-04-11 01:15:23.038 DEBUG 3366 --- [timer://trigger] o.a.h.client.protocol.RequestAuthCache   : Auth cache not set in the context
2020-04-11 01:15:23.039 DEBUG 3366 --- [timer://trigger] h.i.c.PoolingHttpClientConnectionManager : Connection request: [route: {}->http://mysite.example.com:80][total kept alive: 0; route allocated: 0 of 20; total allocated: 0 of 200]
2020-04-11 01:15:23.060 DEBUG 3366 --- [timer://trigger] h.i.c.PoolingHttpClientConnectionManager : Connection leased: [id: 0][route: {}->http://mysite.example.com:80][total kept alive: 0; route allocated: 1 of 20; total allocated: 1 of 200]
2020-04-11 01:15:23.062 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Opening connection {}->http://mysite.example.com:80
2020-04-11 01:15:23.065 DEBUG 3366 --- [timer://trigger] .i.c.DefaultHttpClientConnectionOperator : Connecting to mysite.example.com/192.168.122.194:80
2020-04-11 01:15:23.067 DEBUG 3366 --- [timer://trigger] .i.c.DefaultHttpClientConnectionOperator : Connection established 192.168.122.1:58302<->192.168.122.194:80
2020-04-11 01:15:23.067 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Executing request GET / HTTP/1.1
2020-04-11 01:15:23.067 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Target auth state: UNCHALLENGED
2020-04-11 01:15:23.068 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Proxy auth state: UNCHALLENGED
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> GET / HTTP/1.1
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> breadcrumbId: ID-ldemasi-remote-csb-1586560518063-0-1
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> firedTime: Sat Apr 11 01:15:22 CEST 2020
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> Host: mysite.example.com
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> Connection: Keep-Alive
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> User-Agent: Apache-HttpClient/4.5.6 (Java/1.8.0_232)
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> Accept-Encoding: gzip,deflate
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "GET / HTTP/1.1[\r][\n]"
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "breadcrumbId: ID-ldemasi-remote-csb-1586560518063-0-1[\r][\n]"
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "firedTime: Sat Apr 11 01:15:22 CEST 2020[\r][\n]"
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "Host: mysite.example.com[\r][\n]"
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "Connection: Keep-Alive[\r][\n]"
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "User-Agent: Apache-HttpClient/4.5.6 (Java/1.8.0_232)[\r][\n]"
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "Accept-Encoding: gzip,deflate[\r][\n]"
2020-04-11 01:15:23.070 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "[\r][\n]"
2020-04-11 01:15:23.076 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "HTTP/1.1 401 Unauthorized[\r][\n]"
2020-04-11 01:15:23.076 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Date: Fri, 10 Apr 2020 23:15:22 GMT[\r][\n]"
2020-04-11 01:15:23.076 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Server: Apache/2.4.43 (Fedora) mod_auth_gssapi/1.6.1[\r][\n]"
2020-04-11 01:15:23.076 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "WWW-Authenticate: Negotiate[\r][\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Content-Length: 503[\r][\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Keep-Alive: timeout=5, max=100[\r][\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Connection: Keep-Alive[\r][\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Content-Type: text/html; charset=iso-8859-1[\r][\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "[\r][\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<html><head>[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<title>401 Unauthorized</title>[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "</head><body>[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<h1>Unauthorized</h1>[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<p>This server could not verify that you[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "are authorized to access the document[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "requested.  Either you supplied the wrong[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "credentials (e.g., bad password), or your[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "browser doesn't understand how to supply[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "the credentials required.</p>[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<p>Additionally, a 401 Unauthorized[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "error was encountered while trying to use an ErrorDocument to handle the request.</p>[\n]"
2020-04-11 01:15:23.077 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "</body></html>[\n]"
2020-04-11 01:15:23.083 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << HTTP/1.1 401 Unauthorized
2020-04-11 01:15:23.083 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Date: Fri, 10 Apr 2020 23:15:22 GMT
2020-04-11 01:15:23.083 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Server: Apache/2.4.43 (Fedora) mod_auth_gssapi/1.6.1
2020-04-11 01:15:23.083 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << WWW-Authenticate: Negotiate
2020-04-11 01:15:23.083 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Content-Length: 503
2020-04-11 01:15:23.083 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Keep-Alive: timeout=5, max=100
2020-04-11 01:15:23.083 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Connection: Keep-Alive
2020-04-11 01:15:23.083 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Content-Type: text/html; charset=iso-8859-1
2020-04-11 01:15:23.088 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Connection can be kept alive for 5000 MILLISECONDS
2020-04-11 01:15:23.088 DEBUG 3366 --- [timer://trigger] o.a.http.impl.auth.HttpAuthenticator     : Authentication required
2020-04-11 01:15:23.088 DEBUG 3366 --- [timer://trigger] o.a.http.impl.auth.HttpAuthenticator     : mysite.example.com:80 requested authentication
2020-04-11 01:15:23.088 DEBUG 3366 --- [timer://trigger] o.a.h.i.c.TargetAuthenticationStrategy   : Authentication schemes in the order of preference: [Negotiate, Kerberos, NTLM, CredSSP, Digest, Basic]
2020-04-11 01:15:23.097 DEBUG 3366 --- [timer://trigger] org.apache.http.impl.auth.SPNegoScheme   : Received challenge '' from the auth server
2020-04-11 01:15:23.098 DEBUG 3366 --- [timer://trigger] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for Kerberos authentication scheme not available
2020-04-11 01:15:23.098 DEBUG 3366 --- [timer://trigger] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for NTLM authentication scheme not available
2020-04-11 01:15:23.098 DEBUG 3366 --- [timer://trigger] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for CredSSP authentication scheme not available
2020-04-11 01:15:23.098 DEBUG 3366 --- [timer://trigger] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for Digest authentication scheme not available
2020-04-11 01:15:23.098 DEBUG 3366 --- [timer://trigger] o.a.h.i.c.TargetAuthenticationStrategy   : Challenge for Basic authentication scheme not available
2020-04-11 01:15:23.098 DEBUG 3366 --- [timer://trigger] o.a.http.impl.auth.HttpAuthenticator     : Selected authentication options: [NEGOTIATE]
2020-04-11 01:15:23.099 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Executing request GET / HTTP/1.1
2020-04-11 01:15:23.099 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Target auth state: CHALLENGED
2020-04-11 01:15:23.099 DEBUG 3366 --- [timer://trigger] o.a.http.impl.auth.HttpAuthenticator     : Generating response to an authentication challenge using Negotiate scheme
2020-04-11 01:15:23.100 DEBUG 3366 --- [timer://trigger] org.apache.http.impl.auth.SPNegoScheme   : init mysite.example.com
Java config name: /home/ldemasi/krb5.conf
Loaded from Java config
Search Subject for SPNEGO INIT cred (<<DEF>>, sun.security.jgss.spnego.SpNegoCredElement)
No Subject
Search Subject for Kerberos V5 INIT cred (<<DEF>>, sun.security.jgss.krb5.Krb5InitCredential)
No Subject
Debug is  true storeKey false useTicketCache true useKeyTab false doNotPrompt false ticketCache is null isInitiator true KeyTab is null refreshKrb5Config is true principal is null tryFirstPass is false useFirstPass is false storePass is false clearPass is false
Refreshing Kerberos configuration
Java config name: /home/ldemasi/krb5.conf
Loaded from Java config
>>> KdcAccessibility: reset
>>> KdcAccessibility: reset
Acquire TGT from Cache
>>>KinitOptions cache name is KCM:
Principal is null
null credentials from Ticket Cache
                [Krb5LoginModule] user entered username: user1

Using builtin default etypes for default_tkt_enctypes
default etypes for default_tkt_enctypes: 18 17 16 23 1 3.
>>> KrbAsReq creating message
>>> KrbKdcReq send: kdc=kerberos.example.com UDP:88, timeout=30000, number of retries =3, #bytes=148
>>> KDCCommunication: kdc=kerberos.example.com UDP:88, timeout=30000,Attempt =1, #bytes=148
>>> KrbKdcReq send: #bytes read=650
>>> KdcAccessibility: remove kerberos.example.com:88
>>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
>>> KrbAsRep cons in KrbAsReq.getReply user1
principal is user1@EXAMPLE.COM
Commit Succeeded 

Found ticket for user1@EXAMPLE.COM to go to krbtgt/EXAMPLE.COM@EXAMPLE.COM expiring on Sun Apr 12 01:15:22 CEST 2020
Entered Krb5Context.initSecContext with state=STATE_NEW
Service ticket not found in the subject
>>> Credentials acquireServiceCreds: same realm
Using builtin default etypes for default_tgs_enctypes
default etypes for default_tgs_enctypes: 18 17 16 23 1 3.
>>> CksumType: sun.security.krb5.internal.crypto.RsaMd5CksumType
>>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
>>> KrbKdcReq send: kdc=kerberos.example.com UDP:88, timeout=30000, number of retries =3, #bytes=652
>>> KDCCommunication: kdc=kerberos.example.com UDP:88, timeout=30000,Attempt =1, #bytes=652
>>> KrbKdcReq send: #bytes read=657
>>> KdcAccessibility: remove kerberos.example.com:88
>>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
>>> KrbApReq: APOptions are 00100000 00000000 00000000 00000000
>>> EType: sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType
Krb5Context setting mySeqNumber to: 764770798
Created InitSecContextToken:
0000: 01 00 6E 82 02 46 30 82   02 42 A0 03 02 01 05 A1  ..n..F0..B......
0010: 03 02 01 0E A2 07 03 05   00 20 00 00 00 A3 82 01  ......... ......
0020: 5A 61 82 01 56 30 82 01   52 A0 03 02 01 05 A1 0D  Za..V0..R.......
0030: 1B 0B 45 58 41 4D 50 4C   45 2E 43 4F 4D A2 25 30  ..EXAMPLE.COM.%0
0040: 23 A0 03 02 01 00 A1 1C   30 1A 1B 04 48 54 54 50  #.......0...HTTP
0050: 1B 12 6D 79 73 69 74 65   2E 65 78 61 6D 70 6C 65  ..mysite.example
0060: 2E 63 6F 6D A3 82 01 13   30 82 01 0F A0 03 02 01  .com....0.......
0070: 12 A1 03 02 01 02 A2 82   01 01 04 81 FE E9 23 1D  ..............#.
0080: C5 59 FB 17 A3 81 A3 F8   A9 41 5F 6A 0C C0 7E 84  .Y.......A_j....
0090: 66 42 9A C7 1C BC 7A EC   DA 85 24 9B 3C 18 6F FC  fB....z...$.<.o.
00A0: 16 BA 9C DB 9C 61 5F E3   2F 95 C7 A6 29 DF FA F9  .....a_./...)...
00B0: F1 C4 AD 47 63 FA 56 E4   E7 79 56 9B 44 CC F2 51  ...Gc.V..yV.D..Q
00C0: 96 69 3A 6E D9 E4 9C 0D   D2 35 56 9C 16 E2 8B 85  .i:n.....5V.....
00D0: 81 00 3C 51 84 3A C9 51   75 0C C1 0C 7C 3B 0B F7  ..<Q.:.Qu....;..
00E0: 82 21 98 08 94 39 CF 4E   98 1F 1D 53 4A 79 8A 13  .!...9.N...SJy..
00F0: 9F DF BA 8F BC 74 F9 A5   FF 1D 6C 32 DE 4B 36 38  .....t....l2.K68
0100: EF D3 F4 02 24 7E 9A 47   25 70 ED EB 0A F8 F7 FB  ....$..G%p......
0110: 0B 4A 5E 5C A8 5F 92 BD   AC 3C EC A0 AD CD 8D E6  .J^\._...<......
0120: 74 A1 71 8B 37 25 96 71   25 9F B0 9D 20 33 E0 22  t.q.7%.q%... 3."
0130: 94 C1 EF DE 35 AE 15 09   7E 60 5C B8 28 D3 3E 2A  ....5....`\.(.>*
0140: FE 6F 99 DF AB DD FD 72   11 B2 98 A7 AE B8 9E AA  .o.....r........
0150: 7D 5E 87 2E E1 02 58 45   06 BD 93 91 9E 91 B9 C5  .^....XE........
0160: 9B 3B 51 14 39 04 7E 6F   54 F0 F6 9D DB 02 0C 14  .;Q.9..oT.......
0170: 5D 1E DE 68 37 71 E3 A1   B6 D0 E0 A4 81 CE 30 81  ]..h7q........0.
0180: CB A0 03 02 01 12 A2 81   C3 04 81 C0 2A 90 C0 37  ............*..7
0190: BE 48 27 83 0F 59 B1 96   18 D9 70 81 B1 3A 5B CA  .H'..Y....p..:[.
01A0: 07 1D 0D C7 F1 35 07 50   C2 A0 1B BF 54 B7 87 CD  .....5.P....T...
01B0: E5 E0 1F 68 EB 62 B1 C1   46 C9 BC 9A 50 AA 5A B2  ...h.b..F...P.Z.
01C0: 93 87 11 C6 42 E0 50 E8   38 0C F2 38 21 9D 59 F5  ....B.P.8..8!.Y.
01D0: 99 F7 B3 9D 9C 1A 1D F3   05 D9 20 10 8A F7 E0 B5  .......... .....
01E0: 98 2F D5 3A 9F 8A 26 D1   D0 3E B3 AD 86 AD D2 8B  ./.:..&..>......
01F0: A3 D6 1C FE D4 74 CF 3F   9D 19 18 9A F3 AF 57 DA  .....t.?......W.
0200: CA 61 66 74 F0 95 14 E0   8F BD 1C 57 84 35 48 05  .aft.......W.5H.
0210: B2 4C 29 09 6B 6B CA A9   5E 86 8A 1D 93 83 6F 23  .L).kk..^.....o#
0220: 8D 8D BB 6A BF EC C6 29   03 D4 F6 53 E3 0F FE CD  ...j...)...S....
0230: F7 0A 75 ED BC AB D5 15   25 2D 14 87 A8 C1 3D C3  ..u.....%-....=.
0240: B2 B5 2E CB BF D2 B9 7A   94 DF 4F 2F              .......z..O/

2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.impl.auth.SPNegoScheme   : Sending response 'YIICiAYGKwYBBQUCoIICfDCCAnigDTALBgkqhkiG9xIBAgKhBAMCAXaiggJfBIICW2CCAlcGCSqGSIb3EgECAgEAboICRjCCAkKgAwIBBaEDAgEOogcDBQAgAAAAo4IBWmGCAVYwggFSoAMCAQWhDRsLRVhBTVBMRS5DT02iJTAjoAMCAQChHDAaGwRIVFRQGxJteXNpdGUuZXhhbXBsZS5jb22jggETMIIBD6ADAgESoQMCAQKiggEBBIH+6SMdxVn7F6OBo/ipQV9qDMB+hGZCmsccvHrs2oUkmzwYb/wWupzbnGFf4y+Vx6Yp3/r58cStR2P6VuTneVabRMzyUZZpOm7Z5JwN0jVWnBbii4WBADxRhDrJUXUMwQx8Owv3giGYCJQ5z06YHx1TSnmKE5/fuo+8dPml/x1sMt5LNjjv0/QCJH6aRyVw7esK+Pf7C0peXKhfkr2sPOygrc2N5nShcYs3JZZxJZ+wnSAz4CKUwe/eNa4VCX5gXLgo0z4q/m+Z36vd/XIRspinrrieqn1ehy7hAlhFBr2TkZ6RucWbO1EUOQR+b1Tw9p3bAgwUXR7eaDdx46G20OCkgc4wgcugAwIBEqKBwwSBwCqQwDe+SCeDD1mxlhjZcIGxOlvKBx0Nx/E1B1DCoBu/VLeHzeXgH2jrYrHBRsm8mlCqWrKThxHGQuBQ6DgM8jghnVn1mfeznZwaHfMF2SAQivfgtZgv1TqfiibR0D6zrYat0ouj1hz+1HTPP50ZGJrzr1faymFmdPCVFOCPvRxXhDVIBbJMKQlra8qpXoaKHZODbyONjbtqv+zGKQPU9lPjD/7N9wp17byr1RUlLRSHqME9w7K1Lsu/0rl6lN9PLw==' back to the auth server
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Proxy auth state: UNCHALLENGED
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> GET / HTTP/1.1
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> breadcrumbId: ID-ldemasi-remote-csb-1586560518063-0-1
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> firedTime: Sat Apr 11 01:15:22 CEST 2020
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> Host: mysite.example.com
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> Connection: Keep-Alive
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> User-Agent: Apache-HttpClient/4.5.6 (Java/1.8.0_232)
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> Accept-Encoding: gzip,deflate
2020-04-11 01:15:23.340 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 >> Authorization: Negotiate YIICiAYGKwYBBQUCoIICfDCCAnigDTALBgkqhkiG9xIBAgKhBAMCAXaiggJfBIICW2CCAlcGCSqGSIb3EgECAgEAboICRjCCAkKgAwIBBaEDAgEOogcDBQAgAAAAo4IBWmGCAVYwggFSoAMCAQWhDRsLRVhBTVBMRS5DT02iJTAjoAMCAQChHDAaGwRIVFRQGxJteXNpdGUuZXhhbXBsZS5jb22jggETMIIBD6ADAgESoQMCAQKiggEBBIH+6SMdxVn7F6OBo/ipQV9qDMB+hGZCmsccvHrs2oUkmzwYb/wWupzbnGFf4y+Vx6Yp3/r58cStR2P6VuTneVabRMzyUZZpOm7Z5JwN0jVWnBbii4WBADxRhDrJUXUMwQx8Owv3giGYCJQ5z06YHx1TSnmKE5/fuo+8dPml/x1sMt5LNjjv0/QCJH6aRyVw7esK+Pf7C0peXKhfkr2sPOygrc2N5nShcYs3JZZxJZ+wnSAz4CKUwe/eNa4VCX5gXLgo0z4q/m+Z36vd/XIRspinrrieqn1ehy7hAlhFBr2TkZ6RucWbO1EUOQR+b1Tw9p3bAgwUXR7eaDdx46G20OCkgc4wgcugAwIBEqKBwwSBwCqQwDe+SCeDD1mxlhjZcIGxOlvKBx0Nx/E1B1DCoBu/VLeHzeXgH2jrYrHBRsm8mlCqWrKThxHGQuBQ6DgM8jghnVn1mfeznZwaHfMF2SAQivfgtZgv1TqfiibR0D6zrYat0ouj1hz+1HTPP50ZGJrzr1faymFmdPCVFOCPvRxXhDVIBbJMKQlra8qpXoaKHZODbyONjbtqv+zGKQPU9lPjD/7N9wp17byr1RUlLRSHqME9w7K1Lsu/0rl6lN9PLw==
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "GET / HTTP/1.1[\r][\n]"
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "breadcrumbId: ID-ldemasi-remote-csb-1586560518063-0-1[\r][\n]"
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "firedTime: Sat Apr 11 01:15:22 CEST 2020[\r][\n]"
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "Host: mysite.example.com[\r][\n]"
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "Connection: Keep-Alive[\r][\n]"
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "User-Agent: Apache-HttpClient/4.5.6 (Java/1.8.0_232)[\r][\n]"
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "Accept-Encoding: gzip,deflate[\r][\n]"
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "Authorization: Negotiate YIICiAYGKwYBBQUCoIICfDCCAnigDTALBgkqhkiG9xIBAgKhBAMCAXaiggJfBIICW2CCAlcGCSqGSIb3EgECAgEAboICRjCCAkKgAwIBBaEDAgEOogcDBQAgAAAAo4IBWmGCAVYwggFSoAMCAQWhDRsLRVhBTVBMRS5DT02iJTAjoAMCAQChHDAaGwRIVFRQGxJteXNpdGUuZXhhbXBsZS5jb22jggETMIIBD6ADAgESoQMCAQKiggEBBIH+6SMdxVn7F6OBo/ipQV9qDMB+hGZCmsccvHrs2oUkmzwYb/wWupzbnGFf4y+Vx6Yp3/r58cStR2P6VuTneVabRMzyUZZpOm7Z5JwN0jVWnBbii4WBADxRhDrJUXUMwQx8Owv3giGYCJQ5z06YHx1TSnmKE5/fuo+8dPml/x1sMt5LNjjv0/QCJH6aRyVw7esK+Pf7C0peXKhfkr2sPOygrc2N5nShcYs3JZZxJZ+wnSAz4CKUwe/eNa4VCX5gXLgo0z4q/m+Z36vd/XIRspinrrieqn1ehy7hAlhFBr2TkZ6RucWbO1EUOQR+b1Tw9p3bAgwUXR7eaDdx46G20OCkgc4wgcugAwIBEqKBwwSBwCqQwDe+SCeDD1mxlhjZcIGxOlvKBx0Nx/E1B1DCoBu/VLeHzeXgH2jrYrHBRsm8mlCqWrKThxHGQuBQ6DgM8jghnVn1mfeznZwaHfMF2SAQivfgtZgv1TqfiibR0D6zrYat0ouj1hz+1HTPP50ZGJrzr1faymFmdPCVFOCPvRxXhDVIBbJMKQlra8qpXoaKHZODbyONjbtqv+zGKQPU9lPjD/7N9wp17byr1RUlLRSHqME9w7K1Lsu/0rl6lN9PLw==[\r][\n]"
2020-04-11 01:15:23.341 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 >> "[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "HTTP/1.1 200 OK[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Date: Fri, 10 Apr 2020 23:15:23 GMT[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Server: Apache/2.4.43 (Fedora) mod_auth_gssapi/1.6.1[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "WWW-Authenticate: Negotiate oYG3MIG0oAMKAQChCwYJKoZIhvcSAQICooGfBIGcYIGZBgkqhkiG9xIBAgICAG+BiTCBhqADAgEFoQMCAQ+iejB4oAMCARKicQRvQJCpU+sS6U1ZH9KKQvnlsXTuhOGg2Piej/4QNVyr68fbwCV6NMn6oMr8EA/DJUHSh0y1k9B8syLE8tY+4NnlCmlD9irmvLhsVxigfpHXOgidfqDed2txorTpxQg5nER0nyznOxqMuzU5K2Fvmxxg[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Last-Modified: Fri, 10 Apr 2020 21:57:29 GMT[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "ETag: "115-5a2f6d1cecd95"[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Accept-Ranges: bytes[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Content-Length: 277[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Keep-Alive: timeout=5, max=99[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Connection: Keep-Alive[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "Content-Type: text/html; charset=UTF-8[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "[\r][\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<!DOCTYPE HTML PUBLIC "-//IETF//DTD HTML 2.0//EN">[\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<html>[\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<head>[\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "    <title>Congrats!!!</title>[\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "    <meta charset="UTF-8">[\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "</head>[\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<body>[\n]"
2020-04-11 01:15:23.353 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "<div class="container">[\n]"
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "    <div class="page-header">[\n]"
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "        <h1>You've been Authenticated! Enjoy!</h1>[\n]"
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "    </div>[\n]"
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "</div>[\n]"
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "</body>[\n]"
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.wire                     : http-outgoing-0 << "</html>[\n]"
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << HTTP/1.1 200 OK
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Date: Fri, 10 Apr 2020 23:15:23 GMT
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Server: Apache/2.4.43 (Fedora) mod_auth_gssapi/1.6.1
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << WWW-Authenticate: Negotiate oYG3MIG0oAMKAQChCwYJKoZIhvcSAQICooGfBIGcYIGZBgkqhkiG9xIBAgICAG+BiTCBhqADAgEFoQMCAQ+iejB4oAMCARKicQRvQJCpU+sS6U1ZH9KKQvnlsXTuhOGg2Piej/4QNVyr68fbwCV6NMn6oMr8EA/DJUHSh0y1k9B8syLE8tY+4NnlCmlD9irmvLhsVxigfpHXOgidfqDed2txorTpxQg5nER0nyznOxqMuzU5K2Fvmxxg
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Last-Modified: Fri, 10 Apr 2020 21:57:29 GMT
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << ETag: "115-5a2f6d1cecd95"
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Accept-Ranges: bytes
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Content-Length: 277
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Keep-Alive: timeout=5, max=99
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Connection: Keep-Alive
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] org.apache.http.headers                  : http-outgoing-0 << Content-Type: text/html; charset=UTF-8
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] o.a.http.impl.execchain.MainClientExec   : Connection can be kept alive for 5000 MILLISECONDS
2020-04-11 01:15:23.354 DEBUG 3366 --- [timer://trigger] o.a.http.impl.auth.HttpAuthenticator     : Authentication succeeded
2020-04-11 01:15:23.358 DEBUG 3366 --- [timer://trigger] o.a.camel.component.http4.HttpProducer   : Http responseCode: 200
2020-04-11 01:15:23.364 DEBUG 3366 --- [timer://trigger] h.i.c.PoolingHttpClientConnectionManager : Connection [id: 0][route: {}->http://mysite.example.com:80] can be kept alive for 5.0 seconds
2020-04-11 01:15:23.364 DEBUG 3366 --- [timer://trigger] h.i.c.DefaultManagedHttpClientConnection : http-outgoing-0: set socket timeout to 0
2020-04-11 01:15:23.364 DEBUG 3366 --- [timer://trigger] h.i.c.PoolingHttpClientConnectionManager : Connection released: [id: 0][route: {}->http://mysite.example.com:80][total kept alive: 1; route allocated: 1 of 20; total allocated: 1 of 200]

```
