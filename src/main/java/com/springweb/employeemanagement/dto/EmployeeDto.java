package com.springweb.employeemanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springweb.employeemanagement.annotations.EmployeeRoleValidation;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto implements Serializable {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name can only contain letters and spaces")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 65, message = "Age must be at most 65")
    private Integer age;

    @NotNull(message = "Date of joining is required")
    @PastOrPresent(message = "Date of joining must be in the past or today")
    private LocalDate dateOfJoining;

    @NotNull(message = "Active status must be specified")
    @AssertTrue(message = "Employee should be true")
    @JsonProperty("isActive")
    private Boolean isActive;

    @NotNull(message = "Salary cannot be null")
    @Positive(message = "Salary of Employee should be positive")
    @Digits(integer = 5, fraction = 2, message = "The salary can be in the form XXX.XX")
    @DecimalMax(value = "100000.99", message = "Salary cannot exceed 100,000.99")
    @DecimalMin(value = "100.50", message = "Salary must be at least 100.50")
    private Double salary;

    @NotBlank(message = "Role of the employee cannot be blank")
    @EmployeeRoleValidation
    private String role;
}
