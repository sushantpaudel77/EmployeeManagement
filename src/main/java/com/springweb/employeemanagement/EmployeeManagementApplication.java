package com.springweb.employeemanagement;

import com.springweb.employeemanagement.services.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class EmployeeManagementApplication implements CommandLineRunner {

	private final DataService dataService;

	@Value("${my.variable}")
	private String myVariable;

	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("The data service is: "+ dataService.getData());

		System.out.println("My variable: "+ myVariable);
	}
}
