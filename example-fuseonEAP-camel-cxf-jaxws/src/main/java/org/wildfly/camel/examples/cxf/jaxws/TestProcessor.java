package org.wildfly.camel.examples.cxf.jaxws;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class TestProcessor implements Processor {

	@Override
	public void process(Exchange arg0) throws Exception {
		// TODO Auto-generated method stub
		Object[] arrayParam = new Object[2];
		arrayParam[0]= "hey";
		arrayParam[1]= "hello";
		arg0.getIn().setBody(arrayParam);
	}

}
