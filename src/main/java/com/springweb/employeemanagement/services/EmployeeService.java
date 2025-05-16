package com.springweb.employeemanagement.services;

import com.springweb.employeemanagement.dto.EmployeeDto;
import com.springweb.employeemanagement.entities.EmployeeEntity;
import com.springweb.employeemanagement.exception.DuplicateResourceException;
import com.springweb.employeemanagement.exception.InvalidDataException;
import com.springweb.employeemanagement.exception.ResourceNotFoundException;
import com.springweb.employeemanagement.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    private EmployeeDto convertToDto(EmployeeEntity entity) {
        return modelMapper.map(entity, EmployeeDto.class);
    }

    private EmployeeEntity convertToEntity(EmployeeDto dto) {
        return modelMapper.map(dto, EmployeeEntity.class);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(Long employeeId) {
        EmployeeEntity employeeEntity = findByEmployeeIdOrThrow(employeeId);
        return convertToDto(employeeEntity);
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        validateNewEmployee(employeeDto);

        EmployeeEntity employeeEntity = convertToEntity(employeeDto);
        EmployeeEntity savedEmployee = employeeRepository.save(employeeEntity);

        return convertToDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee) {
        EmployeeEntity existingEmployee = findByEmployeeIdOrThrow(employeeId);

        // checks email uniqueness if changed
        validateEmailForUpdate(existingEmployee, updatedEmployee);
        updateEmployeeFields(existingEmployee, updatedEmployee);

        EmployeeEntity savedEmployee = employeeRepository.save(existingEmployee);
        return convertToDto(savedEmployee);
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        employeeRepository.findById(employeeId)
                .ifPresentOrElse(employeeRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Employee with the ID " + employeeId + " not found");
                        });
    }

    @Transactional
    public EmployeeDto updatePartialEmployeeById(Long employeeId, Map<String, Object> updates) {
        EmployeeEntity employee = findByEmployeeIdOrThrow(employeeId);

        if (updates.containsKey("name") && updates.get("name") != null) {
            employee.setName((String) updates.get("name"));
        }

        if (updates.containsKey("email") && updates.get("email") != null) {
            String newEmail = (String) updates.get("email");
            validateEmailForUpdate(employee, newEmail);
            employee.setEmail(newEmail);
        }

        if (updates.containsKey("age") && updates.get("age") != null) {
            employee.setAge((Integer) updates.get("age"));
        }

        if (updates.containsKey("isActive") && updates.get("isActive") != null) {
            employee.setIsActive((Boolean) updates.get("isActive"));
        }

        EmployeeEntity savedEmployee = employeeRepository.save(employee);

        return convertToDto(savedEmployee);
    }

    // Helper Method
    private EmployeeEntity findByEmployeeIdOrThrow(Long employeeId) {
        if (employeeId == null) {
            throw new InvalidDataException("Employee ID cannot be null");
        }
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
    }

    private void validateNewEmployee(EmployeeDto employeeDto) {
        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            throw new DuplicateResourceException("Employee with email " + employeeDto.getEmail() + " already exists");
        }
    }

    private void validateEmailForUpdate(EmployeeEntity existingEmployee, EmployeeDto updateEmployee) {
        validateEmailForUpdate(existingEmployee, updateEmployee.getEmail());
    }

    private void validateEmailForUpdate(EmployeeEntity existingEmployee, String newEmail) {
        if (!existingEmployee.getEmail().equals(newEmail) &&
                employeeRepository.existsByEmail(newEmail)) {
            throw new DuplicateResourceException("Email " + newEmail + " is already in use");
        }
    }

    private void updateEmployeeFields(EmployeeEntity existingEmployee, EmployeeDto updatedEmployee) {
        existingEmployee.setName(updatedEmployee.getName());
        existingEmployee.setEmail(updatedEmployee.getEmail());
        existingEmployee.setAge(updatedEmployee.getAge());
        existingEmployee.setIsActive(updatedEmployee.getIsActive());
    }
}

