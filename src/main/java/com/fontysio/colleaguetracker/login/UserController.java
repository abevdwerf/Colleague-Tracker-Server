package com.fontysio.colleaguetracker.login;

import com.fontysio.colleaguetracker.StatusResponse;
import com.fontysio.colleaguetracker.mail.EmailValidator;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

@RestController
@CrossOrigin
@RequestMapping(path = "api/user")
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
    public StatusResponse register (@RequestHeader String idToken) throws GoogleIDTokenInvalidException {
        Payload googlePayload  = userService.getGooglePayload(idToken);
        if (!userService.registerNewUser(googlePayload)) {
            return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "User is already registered");
        }
        return new StatusResponse(HttpStatus.OK.value(), "User registered successfully");
    }
}
