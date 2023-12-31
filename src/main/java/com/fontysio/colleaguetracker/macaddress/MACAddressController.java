package com.fontysio.colleaguetracker.macaddress;

import com.fontysio.colleaguetracker.StatusResponse;
import com.fontysio.colleaguetracker.login.GoogleIDTokenInvalidException;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.UserNotRegisteredException;
import com.fontysio.colleaguetracker.login.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
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
    public StatusResponse AddMACAddress(@RequestHeader String idToken, @RequestParam(name = "macAddress") String addressValue, @RequestParam(name = "label") String label)
            throws GoogleIDTokenInvalidException, UserNotRegisteredException, MACAddressAlreadyPresentWithUserException
    {
        User user = userService.getUser(userService.getExternalID(idToken));
        if (macAddressService.addMACAddressToUser(new MACAddress(addressValue, user.getId(), label))){
            return new StatusResponse(HttpStatus.OK.value(), "Added MACAddress to user successfully");
        } else {
            return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "Adding MACAddress to user failed");
        }
    }

    @DeleteMapping("/delete-mac-address")
    public StatusResponse DeleteMACAddress(@RequestHeader String idToken, @RequestParam(name = "macAddressID") Long macAddressID) throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        User user = userService.getUser(userService.getExternalID(idToken));
        if (macAddressService.deleteMACAddressFromUser(macAddressID, user.getId())) {
            return new StatusResponse(HttpStatus.OK.value(), "Deleted MACAddress successfully");
        } else {
            return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "MACAddress does not exist with user");
        }
    }

    @PutMapping("change-mac-address")
    public StatusResponse ChangeMACAddress(
            @RequestHeader String idToken,
            @RequestParam(name = "oldMACAddressID") Long macAddressID,
            @RequestParam(name = "newMACAddress") String newAddressValue,
            @RequestParam(name = "newLabel") String newLabel
            ) throws GoogleIDTokenInvalidException, UserNotRegisteredException, MACAddressAlreadyPresentWithUserException {
        User user = userService.getUser(userService.getExternalID(idToken));
        if (macAddressService.updateMACAddress(macAddressID, newAddressValue, user.getId(), newLabel)) {
            return new StatusResponse(HttpStatus.OK.value(), "Updated MACAddress successfully");
        } else {
            return new StatusResponse(HttpStatus.BAD_REQUEST.value(), "MACAddress with user ID not found");
        }
    }

    @GetMapping ("/get-mac-addresses")
    public List<MACAddress> GetAllMACAddresses(@RequestHeader String idToken) throws GoogleIDTokenInvalidException, UserNotRegisteredException {
        User user = userService.getUser(userService.getExternalID(idToken));
        return macAddressService.getAllMACAddresses(user.getId());
    }
}
