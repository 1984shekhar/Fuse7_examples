This POC can be tested as:
- In FUSE_HOME/etc/system.properties:
```
adi.ws.port=8080
```
- install application as
```
karaf@root()> bundle:install -s mvn:poc/cxf-rest/1.0
Bundle ID: 245


karaf@root()> cxf:list-endpoints 
Name            │ State   │ Address                                          │ BusID
────────────────┼─────────┼──────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
CustomerService │ Started │ http://0.0.0.0:8080/rest/Finance/StudentECAF/1.0 │ poc.cxf-rest-cxf407282075
karaf@root()> 
```
- Test application using curl
```
[cpandey@cpandey log]$ curl -v http://0.0.0.0:8080/rest/Finance/StudentECAF/1.0/123
* About to connect() to 0.0.0.0 port 8080 (#0)
*   Trying 0.0.0.0...
* Connected to 0.0.0.0 (0.0.0.0) port 8080 (#0)
> GET /rest/Finance/StudentECAF/1.0/123 HTTP/1.1
> User-Agent: curl/7.29.0
> Host: 0.0.0.0:8080
> Accept: */*
> 
< HTTP/1.1 200 OK
< Accept: */*
< breadcrumbId: ID-cpandey-pnq-csb-1598948577393-13-1
< User-Agent: curl/7.29.0
< id: 123
< Date: Tue, 01 Sep 2020 09:31:07 GMT
< Connection: keep-alive
< Content-Type: application/xml
< Content-Length: 0
< Host: 0.0.0.0:8080
< 
* Connection #0 to host 0.0.0.0 left intact
[cpandey@cpandey log]$
```
