package org.example;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;




public class SimpleListExample implements Processor {
	
	Logger logger = Logger.getLogger(this.getClass());
	private ArrayList<String> listString = new ArrayList<String>();

	public ArrayList<String> getListString() {
		return listString;
	}

	public void setListString(ArrayList<String> listString) {
		this.listString = listString;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		logger.info("Within Processor");
		logger.info("List-------> "+this.getListString()+"----ListSize---------->"+this.getListString().size());
	}
	

}
