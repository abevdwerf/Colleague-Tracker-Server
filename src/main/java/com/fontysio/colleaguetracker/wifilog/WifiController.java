package com.fontysio.colleaguetracker.wifilog;

import com.fontysio.colleaguetracker.StatusResponse;
import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.login.services.UserService;
import com.fontysio.colleaguetracker.macaddress.MACAddressService;
import com.fontysio.colleaguetracker.status.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("wifilog")
public class WifiController {
    private final WifiService wifiService;
    private final MACAddressService macAddressService;
    private final UserService userService;
    private final StatusService statusService;

    @Autowired
    public WifiController(WifiService wifiService, MACAddressService macAddressService, StatusService statusService, UserService userService) {
        this.wifiService = wifiService;
        this.macAddressService = macAddressService;
        this.statusService = statusService;
        this.userService = userService;
    }

    @PostMapping("/csv/set")
    public StatusResponse addWifiLogs(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (file != null && CSVHelper.hasCSVFormat(file)) {
            try {
                List<String> macAddresses = wifiService.convertCSVtoMac(file);
                if (macAddresses == null) {
                    message = "No People were found at the office";
                    return new StatusResponse(HttpStatus.OK.value(),message);
                }
                List<User> users = new ArrayList<>();
                for(Long userId:macAddressService.getUserIdsByMacAddresses(macAddresses)){
                    users.add(userService.getUserById(userId));
                }
                statusService.resetAllStatusToNotDetected();
                for (User user:users) {
                    statusService.changeIsDetectedAtOffice(user, true);
                }
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return new StatusResponse(HttpStatus.OK.value(),message);
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                throw new RuntimeException(e);
               // return new StatusResponse(HttpStatus.EXPECTATION_FAILED.value(), message);
            }
        }

        message = "Please upload a csv file!";
        return new StatusResponse(HttpStatus.BAD_REQUEST.value(), message);
    }
}
