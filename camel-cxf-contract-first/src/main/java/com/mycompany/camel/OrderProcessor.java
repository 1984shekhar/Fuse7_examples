package com.mycompany.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class OrderProcessor implements Processor {

	static int counter = 0;
	@Override
	public void process(Exchange exchng) throws Exception {
		System.out.println("Within client Processor");
		// TODO Auto-generated method stub
        String arg1 = "argumentA_"+counter;
        int arg2 = counter;
        String arg3 = "argumentC_"+counter;
        counter++;
        exchng.getOut().setBody(new Object[] {arg1 , arg2 , arg3});
		
	}

}
