package com.mycompany.services;

import com.mycompany.api.Employee;
import com.mycompany.dbaccess.EmployeeDBAccess;

public class EmployeeService {
    private EmployeeDBAccess employeeDBAccess = new EmployeeDBAccess("db-connection.properties");

    public boolean createEmployee(Employee employee){
        return employeeDBAccess.createEmployee(employee);
    }

    public Employee getEmployee(int empId){
        return employeeDBAccess.getEmployee(empId);
    }

    public boolean updateEmployee(Employee employee){
        return employeeDBAccess.updateEmployee(employee);
    }

    public boolean deleteEmployee(int empId){
        return employeeDBAccess.deleteEmployee(empId);
    }

}
