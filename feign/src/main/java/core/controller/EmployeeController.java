package core.controller;

import core.client.EmployeeClient;
import core.model.Employee;

import feign.form.spring.SpringFormEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import feign.Feign;

@RestController
public class EmployeeController {

    private static final String HTTP_FILE_EMPLOYEE_URL = "http://localhost:8081";

    @GetMapping("/employee/{id}")
    public Employee getEmployee(@RequestParam("id") long id) {
        EmployeeClient employeeResource = Feign.builder().encoder(new SpringFormEncoder())
                .target(EmployeeClient.class, HTTP_FILE_EMPLOYEE_URL);
        return employeeResource.getEmployee(id, true);
    }
}
