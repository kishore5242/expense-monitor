package com.kishore.em;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
public class ExpenseMonitorApplication {

    public static void main(String[] args) throws IOException, ParseException {
        SpringApplication.run(ExpenseMonitorApplication.class, args);
    }


}
