package com.mypackage;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class SimpleProcessor implements Processor {
	
	Logger logger = Logger.getLogger(this.getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		
         String bodyText = (String)exchange.getIn().getBody();
         logger.info("Within Processor : body...."+bodyText);
         exchange.getIn().setBody("Modified Hello ALL", String.class);
         exchange.getIn().setHeader("NewHeader", "NewValue");
	}

}
