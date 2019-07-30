package com.mycompany.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.security.authentication.DeferredAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.B64Code;

public class ExtendBasicAuthenticator extends BasicAuthenticator{
	
	@Override
    public Authentication validateRequest(ServletRequest req, ServletResponse res, boolean mandatory) throws ServerAuthException
    {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        String credentials = request.getHeader(HttpHeader.AUTHORIZATION.asString());

        try
        {
            if (!mandatory)
                return new DeferredAuthentication(this);

            if (credentials != null)
            {
                int space=credentials.indexOf(' ');
                if (space>0)
                {
                    String method=credentials.substring(0,space);
                    if ("basic".equalsIgnoreCase(method))
                    {
                        credentials = credentials.substring(space+1);
                        credentials = B64Code.decode(credentials, StandardCharsets.ISO_8859_1);
                        int i = credentials.indexOf(':');
                        if (i>0)
                        {
                            String username = credentials.substring(0,i);
                            String password = credentials.substring(i+1);

                            UserIdentity user = login (username, password, request);
                            if (user!=null)
                            {
                                return new UserAuthentication(getAuthMethod(),user);
                            }
                        }
                    }
                }
            }

            if (DeferredAuthentication.isDeferred(response))
                return Authentication.UNAUTHENTICATED;
            String responseToClient= "HELLO ALL, CUSTOM RESPONSE";
            //response.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), "basic realm=\"" + _loginService.getName() + '"');
            response.getWriter().write(responseToClient);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            response.getWriter().flush();
            response.getWriter().close();
            return Authentication.SEND_CONTINUE;
        }
        catch (IOException e)
        {
            throw new ServerAuthException(e);
        }
        
        
    }


}
