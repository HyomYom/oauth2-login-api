package com.hyomyang.oauth2login.oauth2.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public String great(String name){
     return "Hello " + name;
    }
}
