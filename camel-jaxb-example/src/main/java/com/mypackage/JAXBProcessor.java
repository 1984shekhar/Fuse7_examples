package com.mypackage;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class JAXBProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		 PurchaseOrder order = new PurchaseOrder();
	        order.setName("Chandra SHekhar");
	        order.setPrice(100);
	        order.setAmount(20);
	        
	        exchange.getIn().setBody(order);
	}

}
