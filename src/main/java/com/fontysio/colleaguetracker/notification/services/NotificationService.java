package com.fontysio.colleaguetracker.notification.services;

import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.notification.FcmToken;
import com.fontysio.colleaguetracker.notification.FcmTokenRepository;
import com.fontysio.colleaguetracker.notification.Notification;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {

    private final FcmTokenRepository fcmTokenRepository;

    @Autowired
    public NotificationService(FcmTokenRepository fcmTokenRepository) {
        this.fcmTokenRepository = fcmTokenRepository;
    }

    public boolean SetFCM(FcmToken fcmToken) {
        if (fcmTokenRepository.existsFcmTokenByFcmToken(fcmToken.getFcmToken())) {
            FcmToken oldFcmToken = fcmTokenRepository.findByFcmToken(fcmToken.getFcmToken());
            if (Objects.equals(oldFcmToken.getUserId(), fcmToken.getUserId())) {
                return false;
            } else {
                fcmTokenRepository.delete(oldFcmToken);
            }
        }
        fcmTokenRepository.save(fcmToken);
        return true;
    }

    public boolean SendNotification(User user, Notification notification) {
        List<FcmToken> fcmTokens = fcmTokenRepository.findAllByUserId(user.getId());

        for (FcmToken token : fcmTokens) {
            String jsonString = new JSONObject()
                    .put("to", token.getFcmToken())
                    .put("collapse_key", "type_a")
                    .put("notification", new JSONObject()
                            .put("title", notification.getTitle())
                            .put("body", notification.getBody())
                            .put("sound", notification.getSound())
                            .put("content_available", true)
                            .put("priority", notification.getPriority().toString()))
                    .put("data", new JSONObject()
                            .put("title", notification.getTitle())
                            .put("body", notification.getBody())
                            .put("sound", notification.getSound())
                            .put("content_available", true)
                            .put("priority", notification.getPriority().toString()))
                    .toString();


            ResponseEntity<String> response;
            try {
                String url = "https://fcm.googleapis.com/fcm/send";
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                headers.add("Authorization", "key=AAAAZjCVRVI:APA91bGXTkJtUhTI4bxyc_s0ugl83xms1hGhdmUj9Tw-1A8KqEjVP9VeWyk_GDohZ1hTtbhLRZnkZKfZrv5yY_zDI5VkmYdFfEFeX0-FEoCvZK6SbNvTDCnqzj_ceLQi5hjjZX3jvg1M");
                HttpEntity request = new HttpEntity(jsonString, headers);
                response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request,
                        String.class
                );

                if (response.getStatusCode() == HttpStatus.OK) {

                } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST || response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    fcmTokenRepository.delete(token);
                } else {
                    System.out.println(response.getStatusCode());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }
        return true;

    }
}