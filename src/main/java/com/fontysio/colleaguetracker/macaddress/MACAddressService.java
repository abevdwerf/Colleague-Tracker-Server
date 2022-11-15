package com.fontysio.colleaguetracker.macaddress;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MACAddressService {
    private final MACAddressRepository macAddressRepository;

    public MACAddressService(MACAddressRepository macAddressRepository) {
        this.macAddressRepository = macAddressRepository;
    }

    public boolean AddMACAddressToUser(MACAddress macAddress) throws MACAddressAlreadyPresentWithUserException {
        if (macAddress.getAddressValue() == null || macAddress.getUserID() == null) {
            return false;
        }
        Optional<MACAddress> optionalMACAddress = macAddressRepository
                .findByUserIDAndAddressValue(macAddress.getUserID(), macAddress.getAddressValue());

        if (optionalMACAddress.isPresent()) {
            throw new MACAddressAlreadyPresentWithUserException();
        }

        macAddressRepository.save(macAddress);
        return true;
    }
}
