package com.fontysio.colleaguetracker.macaddress;

import com.fontysio.colleaguetracker.StatusResponse;
import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;
import com.fontysio.colleaguetracker.login.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MACAddressController {
    private final MACAddressService macAddressService;
    private final UserService userService;

    @Autowired
    public MACAddressController(MACAddressService _macAddressService, UserService _userService) {
        macAddressService = _macAddressService;
        userService = _userService;
    }

    @PostMapping("/add-mac-address")
    public StatusResponse AddMACAddress(@RequestHeader String idToken, @RequestParam(name = "macAddress") String addressValue)
            throws GoogleIDTokenInvalidException, UserNotRegisteredException, MACAddressAlreadyPresentWithUserException
    {
        User user = userService.getUser(userService.getExternalID(idToken));
        if (macAddressService.AddMACAddressToUser(new MACAddress(addressValue, user.getId()))){
            return new StatusResponse(HttpStatus.OK.value(), "Added MACAddress to user successfully");
        } else {
            return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "Adding MACAddress to user failed");
        }
    }

    //TODO deleteMACAddress(macAddressID)
    //TODO changeMACAddress(macAddressID, newMacAddress)
    //TODO getAllMACAddress()
}
