package com.mycompany.cxf.soap.endpoint;

import org.apache.log4j.Logger;

import com.mycompany.cxf.soap.model.Person;

public class TestResponse {

	private Logger logger = Logger.getLogger(this.getClass());
	
	public Person getPerson(String id) {
		/*System.out.println(String.valueOf(obj));*/
		logger.info("Within getPerson with id: "+id);
		Person p = new Person();
		if(id.equals("1")){
		p.setAge(29);
		p.setId(1);
		p.setName("Chandra Shekhar");
		
		}else{
			p.setAge(30);
			p.setId(2);
			p.setName("Other");
		}
		return p;
	}

}