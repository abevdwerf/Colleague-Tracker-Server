package com.fontysio.colleaguetracker.login;

import com.fontysio.colleaguetracker.StatusResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import jdk.jshell.Snippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

@RestController
//@RequestMapping(path = "api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    ApplicationEventPublisher eventPublisher;

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

    @PostMapping( "/verificate")
    public String verificate(@RequestHeader String IdToken, @RequestBody String emailAddress, HttpServletRequest request){
        Optional<String> optExternalID = userService.getExternalID(IdToken);
        if (optExternalID.isEmpty()) {
            return "JWT token not valid";
        }
        String externalID = optExternalID.get();

        EmailValidator validator = new EmailValidator();
        if (!validator.isValid(emailAddress)) {
            return "invalid email-address";
        }

        if(userService.emailExists(emailAddress)) {
            return "There is an account with that email address: " + emailAddress;
        }

        try {
            User user = userService.getUserByExternalID(externalID);
            user.setEmail(emailAddress);
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        } catch (RuntimeException ex) {
            return ex.toString();
        }
        return "mail sent succesfully";
    }

    @GetMapping("/confirmVerification")
    public String confirmVerification(@RequestParam("token") final String token) {

        final VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            return "Verification token invalid";
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "Expired token";
        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        return "verified";
    }

    @GetMapping( "/hello")
    public String sayHello(){
        System.out.println("hello");
        return "hello";
    }
}
