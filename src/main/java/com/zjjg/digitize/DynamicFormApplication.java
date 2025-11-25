package com.zjjg.digitize;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zjjg.digitize.mapper")
public class DynamicFormApplication {
    public static void main(String[] args) {
        SpringApplication.run(DynamicFormApplication.class, args);
    }
}