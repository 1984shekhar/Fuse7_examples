package com.redhat.support.examples;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component("routes")
public class MyRouteBuilder extends RouteBuilder {

    public static final String HEADER_CHAR = "header_char";

    @Override
    public void configure() throws Exception {

        int simulatedProcessing = 10000;

        from("amqp-consumer:queue:{{jms.consumer.queue}}?concurrentConsumers=5" +
                "&acknowledgementModeName=CLIENT_ACKNOWLEDGE")
            .log("received message from amqp, delaying for " + simulatedProcessing + " milliseconds..." )
	        .delay(simulatedProcessing)
            .log("complete..." );

    }

}
