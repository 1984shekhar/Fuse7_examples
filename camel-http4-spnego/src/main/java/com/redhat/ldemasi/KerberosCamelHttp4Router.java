package com.redhat.ldemasi;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpClientConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import static org.apache.camel.http.common.HttpMethods.GET;
import static org.apache.camel.Exchange.HTTP_METHOD;

@Component
public class KerberosCamelHttp4Router extends RouteBuilder {

    @Override
    public void configure() {
        from("timer:trigger?repeatCount=1")
                .setHeader(HTTP_METHOD, constant(GET))
                .to("http4:mysite.example.com?httpClientConfigurer=#kerberosHttpClientConfigurer")
                .to("log:out");
    }

    @Bean
    HttpClientConfigurer kerberosHttpClientConfigurer(){
        return new KerberosSPNEGOConfigurer();
    }

}
