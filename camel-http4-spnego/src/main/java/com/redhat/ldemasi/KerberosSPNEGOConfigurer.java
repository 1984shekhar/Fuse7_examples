package com.redhat.ldemasi;

import org.apache.camel.component.http4.HttpClientConfigurer;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.security.Principal;

import static org.apache.http.client.config.AuthSchemes.SPNEGO;


public class KerberosSPNEGOConfigurer implements HttpClientConfigurer {



    public void configureHttpClient(HttpClientBuilder clientBuilder) {

            // Get the credentials from the JAAS configuration rather than here
            final Credentials useJaasCreds = new Credentials() {
                public String getPassword() {
                    return null;
                }

                public Principal getUserPrincipal() {
                    return null;
                }
            };

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(null, -1, null), useJaasCreds);
            Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder
                    .<AuthSchemeProvider>create()
                    .register(SPNEGO, new SPNegoSchemeFactory(true, false))
                    .build();

            clientBuilder
                    .setDefaultAuthSchemeRegistry(authSchemeRegistry)
                    .setDefaultCredentialsProvider(credsProvider)
                    .build();
    }

}
