package com.gnosis.dataexportor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.gnosis.dataexportor", "com.gnosis.common"})
public class DataExportorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataExportorApplication.class, args);
    }
}