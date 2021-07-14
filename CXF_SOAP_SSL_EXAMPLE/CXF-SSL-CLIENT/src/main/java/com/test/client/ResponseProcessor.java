package com.test.client;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

import com.mycompany.cxf.soap.model.Person;

public class ResponseProcessor implements Processor {

	Logger logger = Logger.getLogger(this.getClass());
	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Person person = exchange.getIn().getBody(Person.class);
		logger.info("Name:"+ person.getName());
		
	    logger.info("person details:"+ person.toString());
	}

}
