package com.fontysio.colleaguetracker.status;

import com.fontysio.colleaguetracker.StatusResponse;
import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;
import com.fontysio.colleaguetracker.login.services.IUserService_Status;
import com.fontysio.colleaguetracker.notification.Notification;
import com.fontysio.colleaguetracker.notification.services.INotificationService_Status;
import com.google.api.client.util.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "status")
@EnableScheduling
public class StatusController {
    private final StatusService statusService;
    private final IUserService_Status userService;
    private final INotificationService_Status notificationService;

    @Autowired
    public StatusController(StatusService statusService, IUserService_Status userService, INotificationService_Status notificationService) {
        this.statusService = statusService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @PostMapping("/set")
    public StatusResponse setStatus(@RequestHeader String idToken, @RequestParam StatusObject.Status status, @RequestParam String expirationTime, @RequestParam String beginTime) throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        User user = userService.getUser(userService.getExternalID(idToken));
        if (statusService.setStatus(status, beginTime, expirationTime, user)){
            return new StatusResponse(HttpStatus.OK.value(), "Status changed successfully");
        } else {
            return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "Status change failed");
        }
    }

    @GetMapping("/get")
    public StatusObject getStatus(@RequestHeader String idToken) throws GoogleIDTokenInvalidException, UserNotRegisteredException, NoStatusFoundException {
        User user = userService.getUser(userService.getExternalID(idToken));
        return statusService.getStatus(user);
    }

    @GetMapping("/get-all-colleagues")
    public List<Colleague> getAllColleagues(@RequestHeader String idToken) throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        User currentUser = userService.getUser(userService.getExternalID(idToken));
        List<User> users = userService.getAllUsers();
        return statusService.getAllColleagues(users, currentUser);
    }

    @Scheduled(cron = "0 30 9 * * MON-FRI")
    private void sendReminderMessages() throws UserNotRegisteredException {
        List<Long> allNotifiedUsers = statusService.needToSetStatus(userService.getAllUsers());
        for (Long userId:allNotifiedUsers) {
            User user = userService.getUserById(userId);
            notificationService.SendNotification(user, new Notification("You haven't set your status for today yet, please do so that your colleagues know where you are.", "Status reminder", Notification.Priority.high, "default"));
        }
    }

}
