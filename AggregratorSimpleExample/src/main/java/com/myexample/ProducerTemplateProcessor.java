package com.myexample;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;

public class ProducerTemplateProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		
		ProducerTemplate template = exchange.getContext().createProducerTemplate();
		template.sendBodyAndHeader("direct:start","A","myId","1");
		Thread.sleep(1000);
		template.sendBodyAndHeader("direct:start","B","myId","1");
		Thread.sleep(1000);
		template.sendBodyAndHeader("direct:start","F","myId","2");
		Thread.sleep(1000);
		template.sendBodyAndHeader("direct:start","C","myId","1");
		Thread.sleep(1000);
		template.sendBodyAndHeader("direct:start","G","myId","2");
		Thread.sleep(1000);
		template.sendBodyAndHeader("direct:start","H","myId","2");
	}

}
