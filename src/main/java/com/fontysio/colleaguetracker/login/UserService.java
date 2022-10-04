package com.fontysio.colleaguetracker.login;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;
    private final GooglePublicKeysManager publicKeysManager = new GooglePublicKeysManager(new NetHttpTransport(), new GsonFactory());
    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(publicKeysManager)
            .setAudience(Collections.singletonList("733509514183-sa302u6f1fhqc7a93j789daqmgr63ckv.apps.googleusercontent.com"))
            .build();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Should return internal externalUserID, or null if:
    // - user does not exist or
    // - jwt token is not valid
    public Optional<String> getExternalID(String idTokenString) {

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException e) {
            System.out.println("An exception was thrown!!");
            return Optional.empty();
        }

        if (idToken == null) {
            System.out.println("Invalid ID token");
            return Optional.empty();
        }

        Payload payload = idToken.getPayload();
        String externalUserID = payload.getSubject();
        return Optional.of(externalUserID);
    }

    public Optional<Long> registerNewUser(String externalID) {
        Optional<Long> userID = getUserID(externalID);
        if (userID.isPresent()) {
            return userID;
        }
        userRepository.save(new User(externalID));
        Optional<Long> newUserID = getUserID(externalID);
        if (newUserID.isEmpty()){
            String error = "User ID with external ID: " + externalID + "was empty after creating new user that externalID";
            throw new RuntimeException(error);
        }
        return newUserID;
    }

    public Optional<Long> getUserID(String externalID) {
        User user = userRepository.getUserByExternalID(externalID);
        if (user != null) {
            return Optional.of(user.getId());
        }
        return Optional.empty();
    }
}
