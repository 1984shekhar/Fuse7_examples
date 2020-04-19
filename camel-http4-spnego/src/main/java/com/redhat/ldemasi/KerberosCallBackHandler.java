package com.redhat.ldemasi;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;
import java.util.Properties;

public  class KerberosCallBackHandler implements CallbackHandler {

    public KerberosCallBackHandler() {}

    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {

        Properties prop = new Properties();
        prop.load(KerberosCallBackHandler.class.getClassLoader().getResourceAsStream("application.properties"));

        String user= prop.getProperty("kerberos.spnego.username");
        String password = prop.getProperty("kerberos.spnego.password");

        for (Callback callback : callbacks) {

            if (callback instanceof NameCallback) {
                NameCallback nc = (NameCallback) callback;
                nc.setName(user);
            } else if (callback instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback) callback;
                pc.setPassword(password.toCharArray());
            } else {
                throw new UnsupportedCallbackException(callback, "Unknown Callback");
            }
        }
    }
}

