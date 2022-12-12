package com.fontysio.colleaguetracker.mail;

import com.fontysio.colleaguetracker.StatusResponse;
import com.fontysio.colleaguetracker.login.*;
import com.fontysio.colleaguetracker.login.services.IUserService_Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Objects;

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
    public StatusResponse verify(@RequestHeader String IdToken, @RequestBody String emailAddress, HttpServletRequest request) throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        System.out.println(emailAddress);

        String externalID = userService.getExternalID(IdToken);
        User user = userService.getUser(externalID);

//        EmailValidator validator = new EmailValidator();
//        if (!validator.isValid(emailAddress)) {
//            return new StatusResponse(HttpStatus.UNAUTHORIZED.value(), "invalid email-address") ;
//        }

        if (userService.isVerified(user)) {
            return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "This account is already verified");
        }

        if(userService.emailExists(emailAddress)) {
            User mailUser = userService.getUserByEmail(emailAddress);
            if (Objects.equals(user.getExternalID(), mailUser.getExternalID())) {
                if (userService.isVerified(user)) {
                    return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "This account is already verified");
                }
            } else {
                return new StatusResponse(HttpStatus.UNAUTHORIZED.value(), "There is already an account with that email address: " + emailAddress);
            }
        }

        try {
            user.setEmail(emailAddress);
            String appUrl = request.getContextPath();
            emailService.sendConformationEmail(user, request.getLocale(), appUrl);
        } catch (RuntimeException ex) {
            return new StatusResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.toString()) ;
        }
        return new StatusResponse(HttpStatus.OK.value(), "mail sent succesfully") ;
    }


}
