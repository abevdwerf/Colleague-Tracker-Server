package com.fontysio.colleaguetracker.notification;


import com.fontysio.colleaguetracker.StatusResponse;
import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;
import com.fontysio.colleaguetracker.login.services.UserService;
import com.fontysio.colleaguetracker.notification.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PostMapping("/fcm/set")
    public StatusResponse SetFCM(@RequestParam(value = "fcmToken")String fcmToken, @RequestHeader String idToken) throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        User user = userService.getUser(userService.getExternalID(idToken));
        if (notificationService.SetFCM(new FcmToken(user.getId(), fcmToken))) {
            return new StatusResponse(HttpStatus.OK.value(), "FCM Token set");
        } else {
            return new StatusResponse(HttpStatus.OK.value(), "FCM Token was already set");
        }
    }

    @PostMapping("/notify")
    public StatusResponse NotifyUser(@RequestHeader String idToken, @RequestParam(value = "notifiedUserId")String notifiedUserId)
            throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        User user = userService.getUser(userService.getExternalID(idToken));
        User notifiedUser = userService.getUser(notifiedUserId);
        notificationService.SendNotification(notifiedUser, new Notification(user.getFirstName() + " needs you for something and is looking for you!", "Someone is looking for you", Notification.Priority.high, "default"));
        return new StatusResponse(HttpStatus.OK.value(), "Notification sent");
    }
}
