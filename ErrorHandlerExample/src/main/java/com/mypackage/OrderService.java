package com.mypackage;

import java.io.IOException;

import org.apache.log4j.Logger;

public class OrderService {
	
	Logger logger = Logger.getLogger(this.getClass());
	public void validate() throws IOException {
		logger.info("Within validate method....");
		throw new IOException();
	}

}
