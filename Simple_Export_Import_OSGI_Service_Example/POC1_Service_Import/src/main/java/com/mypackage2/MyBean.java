package com.mypackage2;

import org.apache.log4j.Logger;

import com.mypackage1.HelloService;

public class MyBean {
	
	Logger logger = Logger.getLogger(this.getClass());
	private HelloService helloService;
	
	public HelloService getHelloService() {
		return helloService;
	}

	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}

	public void printDate() {
		logger.info("Print Date: "+ helloService.helloAll());
	}

}
