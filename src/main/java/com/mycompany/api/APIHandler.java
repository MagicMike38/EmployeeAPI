package com.mycompany.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.model.Employee;
import com.mycompany.services.AbstractEmployeeService;
import com.mycompany.services.AbstractKakfaService;
import com.mycompany.services.EmployeeKafkaService;
import com.mycompany.services.EmployeeService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Path("/api")
public class APIHandler {


    private static final ObjectMapper mapper = new ObjectMapper();
    private final AbstractEmployeeService employeeService;
    private final AbstractKakfaService<Employee> employeeKafkaService;

    String propFileName = "application.properties";
    InputStream inputStream;
    Properties props;

    private void initialize() throws IOException {
        inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        props = new Properties();
        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
    }

    public APIHandler() throws IOException {
        initialize();
        employeeService = new EmployeeService(props);
        employeeKafkaService = new EmployeeKafkaService(props);
    }

    @Path("/employee")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(Employee employee) {
        ObjectNode json = mapper.createObjectNode();
        try{
            employeeService.createEmployee(employee);
            json.put("Status", "Successful");
        }
        catch (Exception ex){
            System.out.println("Exception Occurred : " + ex.getMessage());
            json.put("Status", "Failure");
            json.put("Reason", ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.status(Response.Status.CREATED).entity(json).build();
    }

    @Path("/employee/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response read(@PathParam("id") Integer employeeId) {
        ObjectNode json = mapper.createObjectNode();
        try{
            Employee employee = employeeService.getEmployee(employeeId);

            json.put("id", employee.getId());
            json.put("name", employee.getName());
            json.put("designation", employee.getDesignation());

            return Response.status(Response.Status.OK).entity(json).build();
        }
        catch (Exception ex){
            System.out.println("Exception Occurred : " + ex.getMessage());
            json.put("Status", "Failure");
            json.put("Reason", ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
    }

    @Path("/employee")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(Employee employee) {
        ObjectNode json = mapper.createObjectNode();
        try{
            employeeService.updateEmployee(employee);
            json.put("Status", "Successful");
        }
        catch (Exception ex){
            System.out.println("Exception Occurred : " + ex.getMessage());
            json.put("Status", "Failure");
            json.put("Reason", ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.status(Response.Status.CREATED).entity(json).build();
    }

    @Path("employee/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") Integer empId) {
        ObjectNode json = mapper.createObjectNode();
        try{
            employeeService.deleteEmployee(empId);
            json.put("Status", "Successful");
        }
        catch (Exception ex){
            System.out.println("Exception Occurred : " + ex.getMessage());
            json.put("Status", "Failure");
            json.put("Reason", ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.status(Response.Status.OK).entity(json).build();
    }

    @Path("/publish")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publish(Employee employee) {
        ObjectNode json = mapper.createObjectNode();
        try{
            employeeKafkaService.publish(employee);
            json.put("Status", "Successful");
        }
        catch (Exception ex){
            System.out.println("Exception Occurred : " + ex.getMessage());
            json.put("Status", "Failure");
            json.put("Reason", ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.status(Response.Status.CREATED).entity(json).build();
    }

    @Path("/consume")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response consume() {
        ObjectNode json = mapper.createObjectNode();
        try{
            employeeKafkaService.consume();
            json.put("Status", "Successful");
        }
        catch (Exception ex){
            System.out.println("Exception Occurred : "+ex.getMessage());
            json.put("Status", "Failure");
            json.put("Reason", ex.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(json).build();
        }
        return Response.status(Response.Status.CREATED).entity(json).build();
    }
}