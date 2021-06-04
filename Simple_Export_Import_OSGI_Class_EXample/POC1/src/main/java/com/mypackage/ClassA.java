package com.mypackage;

import org.apache.log4j.Logger;

public class ClassA {
	
Logger logger = Logger.getLogger(this.getClass());
	public void testMethod() {
		System.out.println("sysout within classA");
		logger.info("...................logger within classA.............................");
	}

}
