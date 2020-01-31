package com.mycompany.camel;


public class AddOperation {


	public Integer addition(Object[] objs) {
		// TODO Auto-generated method stub
		System.out.println("value 1 :: "+objs[0]);
		System.out.println("value 2 :: "+objs[1]);
		return (Integer)objs[0]+(Integer)objs[1];
	}

}
