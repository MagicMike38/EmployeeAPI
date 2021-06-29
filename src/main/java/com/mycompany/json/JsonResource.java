package com.mycompany.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.List;

@Path("/api")
public class JsonResource {

    private static final ObjectMapper mapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response hello() {

        // create a JSON string
        ObjectNode json = mapper.createObjectNode();
        json.put("result", "Jersey JSON example using Jackson 2.x");
        return Response.status(Response.Status.OK).entity(json).build();
    }

    // Object to JSON
    @Path("/{name}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Employee hello(@PathParam("name") String name) {
        return new Employee(1, name, "dev");
    }

    // A list of objects to JSON
    @Path("/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Employee> helloList() {
        return Arrays.asList(
                new Employee(1, "mkyong", "dev"),
                new Employee(2, "zilap", "qa")
        );
    }

    @Path("/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Employee employee) {

        ObjectNode json = mapper.createObjectNode();
        json.put("status", "ok");
        return Response.status(Response.Status.CREATED).entity(json).build();

    }

}