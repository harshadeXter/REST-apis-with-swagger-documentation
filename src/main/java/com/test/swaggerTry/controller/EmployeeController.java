package com.test.swaggerTry.controller;

import com.test.swaggerTry.exception.EmployeeNotFoundException;
import com.test.swaggerTry.model.Employee;
import com.test.swaggerTry.repository.EmployeeRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1")
@Api(value="Employee Management System")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @ApiOperation(value = "List down all the employees existing in the system", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    })
    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }


    @ApiOperation(value = "Retrieve specific employee by id")
    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable(value = "id") int employeeId) throws EmployeeNotFoundException {
        ResponseEntity<Employee> success = null;
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isPresent()) {
            success = ResponseEntity.ok().body(employee.get());
        }else
            throw new EmployeeNotFoundException("No Employee can be found for given Id." + employeeId);
        return success;
    }

    @ApiOperation(value = "Add new employee details")
    @PostMapping("/employees/new")
    public Employee createEmployee(@Valid @RequestBody Employee employee){
        return employeeRepository.save(employee);
    }

    @ApiOperation(value = "Update an employee")
    @PostMapping("/employees/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable(value = "id") int id,@Valid @RequestBody Employee empDetails) throws EmployeeNotFoundException {
        ResponseEntity<Employee> success = null;
        Employee emp = null;
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) { emp = employee.get();
           emp.setFirstName(empDetails.getFirstName());
           emp.setLastName(empDetails.getLastName());
           emp.setEmailAddress(empDetails.getEmailAddress());
           employeeRepository.save(emp);
        }else {
            throw new EmployeeNotFoundException("No Employee can be found for given Id." + id);
        }
        return ResponseEntity.ok().body(emp);
    }


    @ApiOperation(value = "Delete an employee")
    @DeleteMapping("/employees/{id}")
    public Map<String, Boolean> deleteEmployee(@PathVariable(value = "id") int employeeId)
            throws EmployeeNotFoundException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found for this id :: " + employeeId));
        employeeRepository.delete(employee);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

}
