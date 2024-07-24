package com.example.want.test.service;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class www {
    @GetMapping
    public String www() {
        return "www";
    }

}
