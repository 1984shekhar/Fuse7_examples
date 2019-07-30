package com.mycompany.service;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.Subject;
import javax.servlet.ServletRequest;

import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Credential;

public class HardcodedLoginService implements LoginService {
    private final Map<String, Boolean> users = new ConcurrentHashMap<String, Boolean>();
    // matches what is in the constraint object in the spring config
    private final String[] ACCESS_ROLE = new String[] { "rolename" };	
    private IdentityService identityService = new DefaultIdentityService();
    
	@Override
	public IdentityService getIdentityService() {
		return identityService;
	}
	@Override
	public String getName() {
		return "";
	}

	@Override
	public void logout(UserIdentity arg0) {
		
	}

	@Override
	public void setIdentityService(IdentityService arg0) {
	     this.identityService = arg0;
		
	}

	@Override
	public boolean validate(UserIdentity user) {
		if (users.containsKey(user.getUserPrincipal().getName()))
            return true;

        return false;	
	}

	@Override
	public UserIdentity login(String username, Object creds, ServletRequest arg2) {
		UserIdentity user = null;
		boolean validUser = "user".equals(username) && "password".equals(creds);
		if (validUser) {
			Credential credential = (creds instanceof Credential)?(Credential)creds:Credential.getCredential(creds.toString());

		    Principal userPrincipal = new MappedLoginService.KnownUser(username,credential);
		    Subject subject = new Subject();
		    subject.getPrincipals().add(userPrincipal);
		    subject.getPrivateCredentials().add(creds);
		    subject.setReadOnly();
		    user=identityService.newUserIdentity(subject,userPrincipal, ACCESS_ROLE);
		    users.put(user.getUserPrincipal().getName(), true);
		}
	    return (user != null) ? user : null;
	}
	
}
