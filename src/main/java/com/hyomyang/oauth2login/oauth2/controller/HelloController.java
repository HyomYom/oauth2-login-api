package com.hyomyang.oauth2login.oauth2.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequestMapping("/api/hello")
public class HelloController {
    @GetMapping
    public String sayHello(){
        return "Hello Spring Boot!";
    }
}
