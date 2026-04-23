package com.gnosis.datamapping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.gnosis.datamapping", "com.gnosis.common"})
public class DatamappingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatamappingApplication.class, args);
    }
}
