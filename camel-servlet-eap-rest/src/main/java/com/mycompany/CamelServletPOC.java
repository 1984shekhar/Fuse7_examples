package com.mycompany;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

public class CamelServletPOC extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet").contextPath("/parentPath").port(8080);

        rest().get("/testPath").to("direct:printDate");

        from("direct:printDate").log(LoggingLevel.INFO,"incoming request").transform().simple("${date:now:yyyy-MM-dd'T'HH:mm:ss:SSS}");
    }
}
