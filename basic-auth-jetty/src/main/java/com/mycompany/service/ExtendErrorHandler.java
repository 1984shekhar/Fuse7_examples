package com.mycompany.service;

import java.io.IOException;

import javax.servlet.RequestDispatcher;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
public class ExtendErrorHandler extends org.eclipse.jetty.server.Response {
	
	private static final Logger LOG = Log.getLogger(ExtendErrorHandler.class);
	private OutputType _outputType = OutputType.NONE;;
	HttpChannel _channel = super.getHttpChannel();
    public ExtendErrorHandler(HttpChannel channel, HttpOutput out)
    {
        super(channel, out);
    }
	
    @Override
     public void sendError(int code, String message) throws IOException
    {
    	
    	String _reason="";
        if (isIncluding())
            return;

        if (isCommitted())
        {
            if (LOG.isDebugEnabled())
                LOG.debug("Aborting on sendError on committed response {} {}",code,message);
            code=-1;
        }
        else
            resetBuffer();
        

        switch(code)
        {
            case -1:
                super.getHttpChannel().abort(new IOException());
                return;
            case 102:
                sendProcessing();
                return;
            default:
                break;
        }


     
        setContentType(null);
        setCharacterEncoding(null);
        setHeader(HttpHeader.EXPIRES,null);
        setHeader(HttpHeader.LAST_MODIFIED,null);
        setHeader(HttpHeader.CACHE_CONTROL,null);
        setHeader(HttpHeader.CONTENT_TYPE,null);
        setHeader(HttpHeader.CONTENT_LENGTH, null);

        setStatus(code);

        Request request = _channel.getRequest();
        Throwable cause = (Throwable)request.getAttribute(org.eclipse.jetty.server.Dispatcher.ERROR_EXCEPTION);
        if (message==null)
        {    
            _reason=HttpStatus.getMessage(code);
            message=cause==null?_reason:cause.toString();
        }    
        else
            _reason=message;

        // If we are allowed to have a body, then produce the error page.
        if (code != SC_NO_CONTENT && code != SC_NOT_MODIFIED &&
            code != SC_PARTIAL_CONTENT && code >= SC_OK)
        {
            ContextHandler.Context context = request.getContext();
            ContextHandler contextHandler = context == null ? _channel.getState().getContextHandler() : context.getContextHandler();
            request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, code);
            request.setAttribute(RequestDispatcher.ERROR_MESSAGE, message);
            request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());
            request.setAttribute(RequestDispatcher.ERROR_SERVLET_NAME, request.getServletName());
            ErrorHandler error_handler = ErrorHandler.getErrorHandler(_channel.getServer(), contextHandler);
            if (error_handler!=null)
                error_handler.handle(null, request, request, this);
        }
        if (!request.isAsyncStarted())
            closeOutput();
    }


}
