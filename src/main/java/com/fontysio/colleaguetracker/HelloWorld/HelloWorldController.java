package com.fontysio.colleaguetracker.HelloWorld;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/Hello")
    public String sayHello(){
        return "Hello there";
    }
}
