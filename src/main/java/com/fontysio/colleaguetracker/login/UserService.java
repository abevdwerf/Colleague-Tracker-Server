package com.fontysio.colleaguetracker.login;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    private final GooglePublicKeysManager publicKeysManager = new GooglePublicKeysManager(new NetHttpTransport(), new GsonFactory());
    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(publicKeysManager)
            .setAudience(Collections.singletonList("733509514183-sa302u6f1fhqc7a93j789daqmgr63ckv.apps.googleusercontent.com"))
            .build();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getExternalID (String idTokenString) throws GoogleIDTokenInvalidException {
        return getGooglePayload(idTokenString).getSubject();
    }
    public Payload getGooglePayload(String idTokenString) throws GoogleIDTokenInvalidException {

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (Exception e) {
            throw new GoogleIDTokenInvalidException();
        }

        if (idToken == null) {
            throw new GoogleIDTokenInvalidException();
        }

        Payload payload = idToken.getPayload();
        return payload;
    }

    public boolean registerNewUser(Payload payload) {
        try {
            Long userID = getUserID(payload.getSubject());
            return false;
        } catch (UserNotRegisteredException e) {}

        userRepository.save(new User(payload.getSubject(), (String) payload.get("given_name"), (String) payload.get("family_name")));

        try {
            Long userID = getUserID(payload.getSubject());
            return true;
        } catch (UserNotRegisteredException e) {
            String error = "User ID with external ID: " + payload.getSubject() + "was empty after creating new user that externalID";
            throw new RuntimeException(error);
        }
    }

    public Long getUserID(String externalID) throws UserNotRegisteredException {
        User user = userRepository.getUserByExternalID(externalID);
        if (user != null) {
            return user.getId();
        }
        throw new UserNotRegisteredException();
    }

    public User getUser(String externalID) throws UserNotRegisteredException {
        User user = userRepository.getUserByExternalID(externalID);
        if (user != null) {
            return user;
        }
        throw new UserNotRegisteredException();
    }

    public User getUserByExternalID(String externalID) {
        User user = userRepository.getUserByExternalID(externalID);
        if (user != null) {
            return user;
        }
        return null;
    }
    @Override
    public void createVerificationToken(User user, String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }
    @Override
    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }
    public boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    public void saveRegisteredUser(final User user) {
        userRepository.save(user);
    }
}
