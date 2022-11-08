package com.fontysio.colleaguetracker.login.services;

import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;
import com.fontysio.colleaguetracker.mail.VerificationToken;
import org.springframework.stereotype.Component;

public interface IUserService_Email {

    String getExternalID(String idToken) throws GoogleIDTokenInvalidException;

    User getUser(String externalID) throws UserNotRegisteredException;

    void updateUser(User user);

    boolean emailExists(String emailAddress);
    boolean isVerified(User user);
    User getUserByEmail (final String email);
}
