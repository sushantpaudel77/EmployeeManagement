package com.springweb.employeemanagement.services;

import com.springweb.employeemanagement.dto.EmployeeDto;
import com.springweb.employeemanagement.entities.EmployeeEntity;
import com.springweb.employeemanagement.advice.customexceptions.DuplicateResourceException;
import com.springweb.employeemanagement.advice.customexceptions.InvalidDataException;
import com.springweb.employeemanagement.advice.customexceptions.ResourceNotFoundException;
import com.springweb.employeemanagement.repositories.EmployeeRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private static final String CACHE_NAME = "employees";

    private EmployeeDto convertToDto(EmployeeEntity entity) {
        return modelMapper.map(entity, EmployeeDto.class);
    }

    private EmployeeEntity convertToEntity(EmployeeDto dto) {
        return modelMapper.map(dto, EmployeeEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }


    @Cacheable(cacheNames = CACHE_NAME, key = "#employeeId")
    @Transactional(readOnly = true)
    @Override
    public EmployeeDto getEmployeeById(Long employeeId) {
        log.info("Fetching employee with ID: {}", employeeId);
        EmployeeEntity employeeEntity = findByEmployeeIdOrThrow(employeeId);
        log.info("Successfully fetched employee with ID: {}", employeeId);
        return convertToDto(employeeEntity);
    }

    @CachePut(cacheNames = CACHE_NAME, key = "#result.id")
    @Transactional
    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {

        log.info("Creating new employee with email: {}", employeeDto.getEmail());
        validateNewEmployee(employeeDto);

        EmployeeEntity employeeEntity = convertToEntity(employeeDto);
        EmployeeEntity savedEmployee = employeeRepository.save(employeeEntity);
        log.info("Successfully created new employee with ID: {}", savedEmployee.getId());

        return convertToDto(savedEmployee);
    }

    @CachePut(cacheNames = CACHE_NAME, key = "#employeeId")
    @Transactional
    @Override
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto updatedEmployee) {
        log.info("Start updating employee with ID: {}", employeeId);

        EmployeeEntity existingEmployee = findByEmployeeIdOrThrow(employeeId);
        log.debug("Fetched existing employee details: {}", existingEmployee);

        // checks email uniqueness if changed
        validateEmailForUpdate(existingEmployee, updatedEmployee);
        log.info("Attempted to update email for employee with ID: {}", employeeId);

        updateEmployeeFields(existingEmployee, updatedEmployee);
        log.debug("Employee fields updated for employee ID: {}", employeeId);

        EmployeeEntity savedEmployee = employeeRepository.save(existingEmployee);
        log.info("Employee update successful for ID: {}. Persisted employee: {}", employeeId, savedEmployee);

        EmployeeDto resultDto = convertToDto(savedEmployee);
        log.debug("Converted saved employee entity to DTO: {}", resultDto);

        log.info("Completed updating employee with ID: {}", employeeId);
        return resultDto;
    }

    @Transactional
    @Override
    public void deleteEmployee(Long employeeId) {
        employeeRepository.findById(employeeId)
                .ifPresentOrElse(employeeRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Employee with the ID " + employeeId + " not found");
                        });
    }

    @Transactional
    @Override
    public EmployeeDto updatePartialEmployeeById(Long employeeId, Map<String, Object> updates) {
        EmployeeEntity employee = findByEmployeeIdOrThrow(employeeId);

        if (updates.containsKey(EmployeeField.NAME.getKey()) && updates.get(EmployeeField.NAME.getKey()) != null) {
            employee.setName((String) updates.get(EmployeeField.NAME.getKey()));
        }

        if (updates.containsKey(EmployeeField.EMAIL.getKey()) && updates.get(EmployeeField.EMAIL.getKey()) != null) {
            String newEmail = (String) updates.get(EmployeeField.EMAIL.getKey());
            validateEmailForUpdate(employee, newEmail);
            employee.setEmail(newEmail);
        }

        if (updates.containsKey(EmployeeField.AGE.getKey()) && updates.get(EmployeeField.AGE.getKey()) != null) {
            employee.setAge((Integer) updates.get(EmployeeField.AGE.getKey()));
        }

        if (updates.containsKey(EmployeeField.IS_ACTIVE.getKey()) && updates.get(EmployeeField.IS_ACTIVE.getKey()) != null) {
            employee.setIsActive((Boolean) updates.get(EmployeeField.IS_ACTIVE.getKey()));
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
        existingEmployee.setDateOfJoining(updatedEmployee.getDateOfJoining());
        existingEmployee.setRole(updatedEmployee.getRole());
        existingEmployee.setSalary(updatedEmployee.getSalary());
    }

    // Helper enum
    @Getter
    private enum EmployeeField {
        NAME("name"),
        EMAIL("email"),
        AGE("age"),
        IS_ACTIVE("isActive");

        private final String key;

        EmployeeField(String key) {
            this.key = key;
        }
    }
}

