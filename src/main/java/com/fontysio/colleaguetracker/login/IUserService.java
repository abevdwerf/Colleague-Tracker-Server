package com.fontysio.colleaguetracker.login;

public interface IUserService {
    void createVerificationToken(User user, String token);
    VerificationToken getVerificationToken(String VerificationToken);
    User getUserByExternalID(String externalID);
    boolean emailExists(final String email);
}
