package com.fontysio.colleaguetracker.mail;

import com.fontysio.colleaguetracker.login.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class EmailService {
    private final MessageSource messages;
    private final JavaMailSender mailSender;
    private final VerificationTokenRepository tokenRepository;

    @Value("${SERVER_IP_ADDRESS}")
    private String serverAddress;

    @Autowired
    private Environment environment;

    private final String serverPort = environment.getProperty("server.port");

    @Autowired
    public EmailService(MessageSource messages, JavaMailSender mailSender, VerificationTokenRepository verificationTokenRepository) {
        this.messages = messages;
        this.mailSender = mailSender;
        this.tokenRepository = verificationTokenRepository;

    }

    public void createVerificationToken(User user, String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    public void sendConformationEmail(User user, Locale locale, String appUrl){

        String token = UUID.randomUUID().toString();
        createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "IO Employee Registration Confirmation";
        String confirmationUrl
                = appUrl + "/email/confirm?token=" + token;
        String message = messages.getMessage("message.regSuccLink", null, "You registered successfully. To confirm your registration, please click on the below link.", locale);

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setFrom(environment.getProperty("spring.mail.username"));

        email.setText(message + "\r\n" + serverAddress + serverPort + confirmationUrl);
        mailSender.send(email);
    }



}
