package com.fontysio.colleaguetracker.login.services;

import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;
import com.fontysio.colleaguetracker.login.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements IUserService_Email, IUserService_Status {

    private final UserRepository userRepository;

    private final GooglePublicKeysManager publicKeysManager = new GooglePublicKeysManager(new NetHttpTransport(), new GsonFactory());
    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(publicKeysManager)
            .setAudience(Collections.singletonList("438901753170-4s3sdluu5ib01dovth3sva1572ecpvkr.apps.googleusercontent.com"))
            .build();

    @Autowired
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
    public User getUserById (Long userId) throws UserNotRegisteredException {
        User user = userRepository.findById(userId).get();
        if (user != null) {
            return user;
        }
        throw new UserNotRegisteredException();
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

    public void updateUser(final User user) {
        userRepository.save(user);
   }

    public boolean emailExists(final String email) {
        return userRepository.findByEmail(email) != null;
    }

    public List<User> getAllUsers(){
        return Collections.unmodifiableList(userRepository.findAll());
    }

    public User getUserByEmail (final String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isVerified(User user){
        return user.isEnabled();
    }
}
