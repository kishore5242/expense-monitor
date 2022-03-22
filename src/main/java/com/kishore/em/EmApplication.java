package com.kishore.em;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.IOException;
import java.text.ParseException;

@SpringBootApplication
public class EmApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EmApplication.class);
    }

    public static void main(String[] args) throws IOException, ParseException {
        SpringApplication.run(EmApplication.class, args);
    }


}
