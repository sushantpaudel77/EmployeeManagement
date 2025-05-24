package com.springweb.employeemanagement.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
//@Profile("dev")
public class DataSourceDev implements DataService{
    @Override
    public String getData() {
        return "Data From Dev";
    }
}
