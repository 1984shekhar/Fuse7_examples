package com.mypackage1;

import java.util.Date;

public class HelloServiceImpl implements HelloService {

	@Override
	public String helloAll() {
		
       return new Date()+"";
	}

}
