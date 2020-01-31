package com.mycompany.camel;

import javax.xml.ws.Holder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.message.MessageContentsList;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class PersonProcessor implements Processor {
	 
    private static final Logger LOG = LoggerFactory.getLogger(PersonProcessor.class);
 
    @Override
    public void process(Exchange exchange) throws Exception {
        LOG.info("processing exchange in camel");
 
        BindingOperationInfo boi = (BindingOperationInfo)exchange.getProperty(BindingOperationInfo.class.getName());
        if (boi != null) {
            LOG.info("boi.isUnwrapped" + boi.isUnwrapped());
        }
        // Get the parameters list which element is the holder.
        byte[] payload = exchange.getIn().getBody(byte[].class);
        System.out.println("payload: "+payload);
        MessageContentsList msgList = (MessageContentsList)exchange.getIn().getBody();
        Integer value1 = (Integer)msgList.get(0);
        Integer value2 = (Integer)msgList.get(1);
 
        LOG.info("Complete Arguments...."+value1+" : "+value2);
        exchange.getOut().setBody(new Object[] {value1, value2});
    }

	

 
}
