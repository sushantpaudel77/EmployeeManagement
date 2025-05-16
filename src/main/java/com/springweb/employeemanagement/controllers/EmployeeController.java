package com.springweb.employeemanagement.controllers;

import com.springweb.employeemanagement.dto.EmployeeDto;
import com.springweb.employeemanagement.services.EmployeeService;
import com.springweb.employeemanagement.services.EmployeeServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployee() {
        List<EmployeeDto> allEmployees = employeeService.getAllEmployees();
        return ResponseEntity.ok(allEmployees);
    }

    @GetMapping(path = "/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long employeeId) {
        EmployeeDto employeeData = employeeService.getEmployeeById(employeeId);
        return new ResponseEntity<>(employeeData, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> createNewEmployee(@RequestBody @Valid EmployeeDto employeeEntity) {
        EmployeeDto employee = employeeService.createEmployee(employeeEntity);
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long employeeId,
                                                      @RequestBody @Valid EmployeeDto employee) {
        EmployeeDto employeeEntity = employeeService.updateEmployee(employeeId, employee);
        return ResponseEntity.ok(employeeEntity);
    }

    @DeleteMapping(path = "/{employeeId}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(path = "/{employeeId}")
    public ResponseEntity<EmployeeDto> updatePartialEmployeeById(@PathVariable Long employeeId,
                                                                 @RequestBody Map<String, Object> updates) {
        EmployeeDto employeeDto = employeeService.updatePartialEmployeeById(employeeId, updates);
        return ResponseEntity.ok(employeeDto);
    }
}
