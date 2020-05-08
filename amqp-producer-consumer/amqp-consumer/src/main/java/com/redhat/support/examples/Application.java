/**
 * Copyright 2005-2016 Red Hat, Inc.
 * <p>
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.redhat.support.examples;

import org.apache.camel.component.amqp.AMQPComponent;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.apache.qpid.jms.JmsDestination;
import org.apache.qpid.jms.policy.JmsDefaultPrefetchPolicy;
import org.apache.qpid.jms.policy.JmsDefaultRedeliveryPolicy;
import org.apache.qpid.jms.policy.JmsRedeliveryPolicy;
import org.messaginghub.pooled.jms.JmsPoolConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;

import javax.net.ssl.SSLContext;

@SpringBootApplication(exclude = JmsAutoConfiguration.class)
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application {

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean(name = "amqp-consumer")
    AMQPComponent amqpComponentProducer() {


        JmsConnectionFactory qpid = new JmsConnectionFactory();
        qpid.setRemoteURI(environment.getProperty("jms.consumer.url"));
        qpid.setUsername(environment.getProperty("jms.consumer.user"));
        qpid.setPassword(environment.getProperty("jms.consumer.password"));
        qpid.setClientID(environment.getProperty("jms.consumer.clientId"));

        JmsDefaultPrefetchPolicy policy = new JmsDefaultPrefetchPolicy();
        policy.setMaxPrefetchSize(0);
        qpid.setPrefetchPolicy(policy);

        JmsPoolConnectionFactory poolingFactory = new JmsPoolConnectionFactory();
        poolingFactory.setUseAnonymousProducers(false);
        poolingFactory.setConnectionFactory(qpid);
        return new AMQPComponent(poolingFactory);
    }

}
