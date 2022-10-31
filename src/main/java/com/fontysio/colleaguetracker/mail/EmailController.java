package com.fontysio.colleaguetracker.mail;

import com.fontysio.colleaguetracker.login.*;
import com.fontysio.colleaguetracker.login.services.IUserService_Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

@RestController
@CrossOrigin
@RequestMapping(path = "email")
public class EmailController {

    private final EmailService emailService;
    private final IUserService_Email userService;

    @Autowired
    public EmailController(EmailService emailService, IUserService_Email userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping( "/verify")
    public String verify(@RequestHeader String IdToken, @RequestBody String emailAddress, HttpServletRequest request) {
        String externalID = null;
        try {
            externalID = userService.getExternalID(IdToken);
        } catch (GoogleIDTokenInvalidException e) {
            throw new RuntimeException(e);
        }

        EmailValidator validator = new EmailValidator();
//        if (!validator.isValid(emailAddress)) {
//            return "invalid email-address";
//        }

        if(userService.emailExists(emailAddress)) {
            return "There is already an account with that email address: " + emailAddress;
        }

        try {
            User user = userService.getUser(externalID);
            user.setEmail(emailAddress);
            String appUrl = request.getContextPath();
            emailService.sendConformationEmail(user, request.getLocale(), appUrl);
        } catch (RuntimeException ex) {
            return ex.toString();
        } catch (UserNotRegisteredException e) {
            throw new RuntimeException(e);
        }
        return "mail sent succesfully";
    }

    @GetMapping("/confirm")
    public String confirmVerification(@RequestParam("token") final String token) {

        final VerificationToken verificationToken = emailService.getVerificationToken(token);
        if (verificationToken == null) {
            return "Verification token invalid";
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "Expired token";
        }

        user.setEnabled(true);
        userService.updateUser(user);
        return "verified";
    }
}
