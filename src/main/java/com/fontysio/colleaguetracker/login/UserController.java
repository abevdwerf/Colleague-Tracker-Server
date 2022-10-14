package com.fontysio.colleaguetracker.login;

import org.springframework.beans.factory.annotation.Autowired;
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
