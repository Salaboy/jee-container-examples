/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.ws.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author salaboy
 */
@Path("/")
public class RestResource {

    @GET
    @Produces("text/plain")
    public String get() {
        // Prove we can use an external dependency and weird JDK classes.
        return "done";
    }
}
