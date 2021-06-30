package com.mycompany.dbaccess;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mycompany.dao.Employee;
import org.bson.Document;

public class EmployeeDBAccess {

    private MongoClient client;
    private MongoCollection mongoCollection;

    public EmployeeDBAccess(String propFileName){
        //Read from file
        String dbType = "mongo";
        String db = "details";
        String hostname = "127.0.0.1";
        int port = 27017;

        //factory pattern?
        //if(dbType.equals("mongo")){
        client = new MongoClient(hostname, port);
        var database = client.getDatabase(db);
        mongoCollection = client.getDatabase(db).getCollection("employees");
        System.out.println("Creating collection and db");
        //}
    }

    public boolean createEmployee(Employee employee){
        Document document = new Document();
        document.append("_id", employee.getId());
        document.append("employeeName", employee.getName());
        document.append("employeeDesignation", employee.getDesignation());

        client.getDatabase("details")
                .getCollection("employees").insertOne(document);
        return true;
    }

    public Employee getEmployee(int employeeId){
        FindIterable<Document> findIterable = mongoCollection.find(Filters.eq("_id", employeeId));
        for(Document doc : findIterable) {
            return new Employee(doc.getInteger("_id"),
                    doc.getString("employeeName"),
                    doc.getString("employeeDesignation"));
        }
        return null;
    }

    public boolean updateEmployee(Employee employee){
        mongoCollection.updateOne(new Document("_id", employee.getId()),
                new Document("$set", new Document("employeeDesignation", employee.getDesignation())
                        .append("employeeName", employee.getName())));
        return true;
    }

    public boolean deleteEmployee(int employeeId) {
        mongoCollection.deleteOne(new Document("_id", employeeId));
        return true;
    }
}
