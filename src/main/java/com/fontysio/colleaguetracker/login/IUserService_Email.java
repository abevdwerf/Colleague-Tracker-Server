package com.fontysio.colleaguetracker.login;

import com.fontysio.colleaguetracker.mail.VerificationToken;
import org.springframework.stereotype.Component;

@Component
public interface IUserService_Email {

    String getExternalID(String idToken) throws GoogleIDTokenInvalidException;

    User getUser(String externalID) throws UserNotRegisteredException;

    void updateUser(User user);

    boolean emailExists(String emailAddress);
}
