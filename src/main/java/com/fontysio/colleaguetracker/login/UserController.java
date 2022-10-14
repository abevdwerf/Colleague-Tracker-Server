package com.fontysio.colleaguetracker.login;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping( "/get-full-name")
    public String getFullName (@RequestHeader String idToken) throws UserNotRegisteredException, GoogleIDTokenInvalidException {
        String externalID = userService.getExternalID(idToken);
        User user = userService.getUser(externalID);
        return user.getFirstName() + " " + user.getLastName();
    }

    @GetMapping("/register")
    public HttpStatus register (@RequestHeader String idToken) throws GoogleIDTokenInvalidException {
        Payload googlePayload  = userService.getGooglePayload(idToken);
        userService.registerNewUser(googlePayload);
        return HttpStatus.OK;
    }

    @GetMapping( "/hello")
    public String sayHello(){
        System.out.println("hello");
        return "hello";
    }
}
