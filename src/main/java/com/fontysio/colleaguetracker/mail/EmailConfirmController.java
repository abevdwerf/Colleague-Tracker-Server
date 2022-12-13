package com.fontysio.colleaguetracker.mail;

import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.services.IUserService_Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Calendar;

@Controller
@CrossOrigin
@RequestMapping("email")
public class EmailConfirmController {

    private final EmailService emailService;
    private final IUserService_Email userService;

    @Autowired
    public EmailConfirmController(EmailService emailService, IUserService_Email userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping(value = "/confirm", produces = MediaType.TEXT_HTML_VALUE)
    public String confirmVerification(@RequestParam("token") final String token) {


        final VerificationToken verificationToken = emailService.getVerificationToken(token);
        if (verificationToken == null) {
            return "Invalid";
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            return "Expired";
        }

        user.setEnabled(true);
        userService.updateUser(user);
        return "EmailVerified";
    }
}
