package com.springweb.employeemanagement.services;

import com.springweb.employeemanagement.dto.EmployeeDto;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(Long employeeId);

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee);

    void deleteEmployee(Long employeeId);

    EmployeeDto updatePartialEmployeeById(Long employeeId, Map<String, Object> update);
}
