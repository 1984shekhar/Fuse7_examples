package com.mycompany.camel;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(name = "AdditionService", targetNamespace = "https://my.test.com/services/AdditionService")
public interface AddInterface {
	
	@WebMethod(action = "https://my.test.com/services/addition")
	Integer addition(@WebParam(name = "val1")Integer val1, @WebParam(name = "val2")Integer val2);
}
