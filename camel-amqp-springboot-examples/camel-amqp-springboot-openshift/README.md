#Steps to install AMQ 7.4 on Openshift
oc new-project amq-demo

echo '{"kind": "ServiceAccount", "apiVersion": "v1", "metadata": {"name": "amq-service-account"}}' | oc create -f -

oc policy add-role-to-user view system:serviceaccount:amq-demo:amq-service-account

keytool -genkey -alias broker -keyalg RSA -keystore broker.ks

keytool -export -alias broker -keystore broker.ks -file broker_cert

keytool -genkey -alias client -keyalg RSA -keystore client.ks

keytool -import -alias broker -keystore client.ts -file broker_cert

oc create secret generic amq-app-secret --from-file=broker.ks

oc secrets add sa/amq-service-account secret/amq-app-secret

#Basic non ssl non persistence template
oc new-app --template=amq-broker-74-basic \
   -p AMQ_PROTOCOL=openwire,amqp,stomp,mqtt,hornetq \
   -p AMQ_QUEUES=demoQueue \
   -p AMQ_ADDRESSES=demoTopic \
   -p AMQ_USER=admin \
   -p AMQ_PASSWORD=admin \






# Spring Boot, Camel and ActiveMQ QuickStart

This quickstart shows how to connect a Spring-Boot application to an A-MQ xPaaS message broker and use JMS messaging between two Camel routes using OpenShift.

### Building

The example can be built with

    mvn clean install

### Running the example in OpenShift

It is assumed that:
- OpenShift platform is already running, if not you can find details how to [Install OpenShift at your site](https://docs.openshift.com/container-platform/3.3/install_config/index.html).
- Your system is configured for Fabric8 Maven Workflow, if not you can find a [Get Started Guide](https://access.redhat.com/documentation/en/red-hat-jboss-middleware-for-openshift/3/single/red-hat-jboss-fuse-integration-services-20-for-openshift/)
- The Red Hat JBoss A-MQ xPaaS product should already be installed and running on your OpenShift installation, one simple way to run a A-MQ service is following the documentation of the A-MQ xPaaS image for OpenShift related to the `amq63-basic` template.

Then the following command will package your app and run it on OpenShift:

    mvn fabric8:deploy

To list all the running pods:

    oc get pods

Then find the name of the pod that runs this quickstart, and output the logs from the running pods with:

    oc logs <name of pod>

You can also use the openshift [web console](https://docs.openshift.com/enterprise/3.1/getting_started/developers/developers_console.html#tutorial-video) to manage the
running pods, and view logs and much more.


# Create configmap
```
[cpandey@cpandey camel-amqp-springboot-openshift]$ oc get service -n amq-demo
NAME                 TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)     AGE
broker-amq-amqp      ClusterIP   172.30.139.253   <none>        5672/TCP    2h
broker-amq-jolokia   ClusterIP   172.30.53.5      <none>        8161/TCP    2h
broker-amq-mqtt      ClusterIP   172.30.114.143   <none>        1883/TCP    2h
broker-amq-stomp     ClusterIP   172.30.165.76    <none>        61613/TCP   2h
broker-amq-tcp       ClusterIP   172.30.188.16    <none>        61616/TCP   2h
[cpandey@cpandey camel-amqp-springboot-openshift]$ 

[cpandey@cpandey camel-amqp-springboot-openshift]$ cat application.properties 
service.host=172.30.139.253
amqp.servicePort=5672

oc create configmap spring-boot-camel-amq-config --from-env-file=./application.properties
```


# Check Statistics
```
oc project amq-demo
[cpandey@cpandey spring-boot-camel-config-archetype]$ oc get pods

NAME                 READY     STATUS    RESTARTS   AGE

broker-amq-1-pjwv6   1/1       Running   0          2h

[cpandey@cpandey spring-boot-camel-config-archetype]$ 

[cpandey@cpandey spring-boot-camel-config-archetype]$ oc rsh broker-amq-1-pjwv6

sh-4.2$ pwd

/home/jboss

sh-4.2$ cd broker/bin

sh-4.2$ ./artemis queue stat

OpenJDK 64-Bit Server VM warning: If the number of processors is expected to increase from one, then you should configure the number of parallel GC threads appropriately using -XX:ParallelGCThreads=N

|NAME                     |ADDRESS                  |CONSUMER_COUNT |MESSAGE_COUNT |MESSAGES_ADDED |DELIVERING_COUNT |MESSAGES_ACKED |

|DLQ                      |DLQ                      |0              |0             |0              |0                |0              |

|ExpiryQueue              |ExpiryQueue              |0              |0             |0              |0                |0              |

|activemq.management.6e278d1d-3f1f-4f8f-86d1-ffb8b72214c8|activemq.management.6e278d1d-3f1f-4f8f-86d1-ffb8b72214c8|1              |0             |0              |0                |0              |

|demoQueue                |demoQueue                |0              |0             |0              |0                |0              |

|incomingOrders           |incomingOrders           |1              |0             |168            |0                |168            |

sh-4.2$ 

```
