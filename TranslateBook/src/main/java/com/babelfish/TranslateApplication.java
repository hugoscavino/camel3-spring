package com.babelfish;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan(basePackages="com.babelfish")
public class TranslateApplication {
    public static void main(String[] args) {
        SpringApplication.run(TranslateApplication.class, args);
    }

}