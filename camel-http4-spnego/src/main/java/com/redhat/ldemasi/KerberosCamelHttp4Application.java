package com.redhat.ldemasi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class KerberosCamelHttp4Application {

    /**
     * A main method to start this application.
     *
     *         -Djava.security.auth.login.config=/home/ldemasi/login2.conf
     *         -Djava.security.krb5.conf=/home/ldemasi/krb5.conf
     *         -Dsun.security.krb5.debug=true
     *         -Dsun.security.jgss.debug=true
     *         -Djavax.security.auth.useSubjectCredsOnly=false
     */
    public static void main(String[] args) {
        System.setProperty("java.security.auth.login.config", "/home/chandrashekhar/login.conf");
        System.setProperty("java.security.krb5.conf", "/home/chandrashekhar/krb5.conf");
        System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
        System.setProperty("sun.security.krb5.debug", "true");
        System.setProperty("sun.security.jgss.debug", "true");
        Security.setProperty("auth.login.defaultCallbackHandler", "com.redhat.ldemasi.KerberosCallBackHandler");
        SpringApplication.run(KerberosCamelHttp4Application.class, args);
    }

}
