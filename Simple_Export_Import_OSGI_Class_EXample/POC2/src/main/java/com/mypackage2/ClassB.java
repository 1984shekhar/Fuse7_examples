package com.mypackage2;

import org.apache.log4j.Logger;

import com.mypackage.ClassA;

public class ClassB {
	
	Logger logger = Logger.getLogger(this.getClass());
	
	public void importMethod() {
		System.out.println("sysout within classB");
		logger.info("logger within classB");
		ClassA classa = new ClassA();
		classa.testMethod();
	}

}
