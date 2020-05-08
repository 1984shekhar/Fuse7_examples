package com.redhat.support.examples;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component("routes")
public class MyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("timer://mytimer?repeatCount=10&period=1")
                .log("sending message to amqp" )
                .setHeader("JMSExpiration", constant(System.currentTimeMillis() + 1000))
                .to("amqp-producer:queue:{{jms.producer.queue}}");

    }

}
