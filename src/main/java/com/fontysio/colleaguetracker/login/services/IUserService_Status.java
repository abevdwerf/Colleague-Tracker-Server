package com.fontysio.colleaguetracker.login.services;

import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;

public interface IUserService_Status {
    String getExternalID(String idToken) throws GoogleIDTokenInvalidException;
    User getUser(String externalID) throws UserNotRegisteredException;
}
