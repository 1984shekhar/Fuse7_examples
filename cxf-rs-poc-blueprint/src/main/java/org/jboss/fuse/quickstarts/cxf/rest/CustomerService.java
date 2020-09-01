package org.jboss.fuse.quickstarts.cxf.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class CustomerService {
    @GET
    @Path("/{id}/")
    @Produces( "application/xml" )
    public Customer getCustomer(String id) {
       return null;
    }
}