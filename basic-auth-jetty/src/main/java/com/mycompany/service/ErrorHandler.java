package com.mycompany.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletResponse;

import org.apache.camel.component.jetty.JettyRestHttpBinding;
import org.apache.camel.http.common.DefaultHttpBinding;

public class ErrorHandler extends JettyRestHttpBinding {

  @Override
  public void doWriteExceptionResponse(Throwable exception, HttpServletResponse response) throws IOException {
     System.out.println("Exception cause: "+exception.getCause());
	  if (exception instanceof TimeoutException) {
        response.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
        response.getWriter().write("Continuation timed out...");            
    } else {
      super.doWriteExceptionResponse(exception, response);
    }
  }
}