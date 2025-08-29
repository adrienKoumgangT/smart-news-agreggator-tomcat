package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.hello;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/hello-world")
@Tag(name = "Hello", description = "API operation related to Hello")
public class HelloResource {

    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }


}