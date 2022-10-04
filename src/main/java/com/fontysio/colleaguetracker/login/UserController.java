package com.fontysio.colleaguetracker.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping( "/login")
    public String getSomeData(@RequestHeader String IdToken){
        Optional<String> optExternalID = userService.getExternalID(IdToken);
        if (optExternalID.isEmpty()) {
            return "JWT token not valid";
        }
        String externalID = optExternalID.get();
        Optional<Long> userID = userService.getUserID(externalID);
        if (userID.isEmpty()) {
            userID = userService.registerNewUser(externalID);
        }
        if (!userID.isPresent()) {
            return "something went wrong";
        }
        return "your user ID is: " + userID.get();
    }

    @GetMapping( "/hello")
    public String sayHello(){
        System.out.println("hello");
        return "hello";
    }
}
