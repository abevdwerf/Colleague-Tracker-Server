package com.fontysio.colleaguetracker.status;

import com.fontysio.colleaguetracker.StatusResponse;
import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;
import com.fontysio.colleaguetracker.login.services.IUserService_Status;
import com.google.api.client.util.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "status")
public class StatusController {
    private final StatusService statusService;
    private final IUserService_Status userService;

    @Autowired
    public StatusController(StatusService statusService, IUserService_Status userService) {
        this.statusService = statusService;
        this.userService = userService;
    }

    @PostMapping("/set")
    public StatusResponse setStatus(@RequestHeader String idToken, @RequestParam StatusObject.Status status, @RequestParam String expirationTime) throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        User user = userService.getUser(userService.getExternalID(idToken));
        if (statusService.setStatus(status, expirationTime, user)){
            return new StatusResponse(HttpStatus.OK.value(), "Status changed successfully");
        } else {
            return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "Status change failed");
        }
    }

    @GetMapping("/get")
    public StatusObject getStatus(@RequestHeader String idToken) throws GoogleIDTokenInvalidException, UserNotRegisteredException, NoStatusFoundException, StatusExpiredException {
        User user = userService.getUser(userService.getExternalID(idToken));
        return statusService.getStatus(user);
    }

    @GetMapping("/get-all-colleagues")
    public List<Colleague> getAllColleagues(@RequestHeader String idToken) throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        User currentUser = userService.getUser(userService.getExternalID(idToken));
        List<User> users = userService.getAllUsers();
        return statusService.getAllColleagues(users, currentUser);
    }


}
