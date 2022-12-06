package com.fontysio.colleaguetracker.wifilog;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class WifiService {
    public List<String> convertCSVtoMac(MultipartFile file) throws IOException {
        List<String> macAddresses = CSVHelper.csvToMacAddresses(file.getInputStream());
        return macAddresses;
    }
}
