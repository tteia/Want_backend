package com.example.want;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class WantApplication {

    public static void main(String[] args) {
        SpringApplication.run(WantApplication.class, args);
    }

}
