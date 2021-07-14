package com.mycompany.cxf.soap.endpoint;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
 
public class HttpHeaderHandler extends AbstractSoapInterceptor {
 
	Logger logger = Logger.getLogger(this.getClass());
   public HttpHeaderHandler() {
      super(Phase.PRE_PROTOCOL);
      getAfter().add(SAAJInInterceptor.class.getName());
   }
 
   public void handleMessage(SoapMessage message) throws Fault {

 
      //if you want to read more http header messages, just use getÂ method to obtain fromÂ Â HttpServletRequest.
      HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
      if(null!=request){
         String method = request.getMethod();
         logger.info("Method: "+method);
         if(!method.equalsIgnoreCase("POST")){
                //throw new Fault(method + "not allowed");
                 Exception e = new Exception(method+ " not allowed"); 
                 throw new Fault(e);
         }
      }
   }
}