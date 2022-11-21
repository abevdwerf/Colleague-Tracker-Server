package com.fontysio.colleaguetracker.macaddress;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MACAddressService {
    private final MACAddressRepository macAddressRepository;

    public MACAddressService(MACAddressRepository macAddressRepository) {
        this.macAddressRepository = macAddressRepository;
    }

    public boolean addMACAddressToUser(MACAddress macAddress) throws MACAddressAlreadyPresentWithUserException {
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

    public boolean deleteMACAddressFromUser(Long macAddressID, Long userID) {
        return macAddressRepository.deleteByIdAndUserID(macAddressID, userID) > 0;
    }

    public boolean updateMACAddress(String oldAddressValue, String newAddressValue, Long userID) {
        Optional<MACAddress> macAddressOptional = macAddressRepository.findByUserIDAndAddressValue(userID, oldAddressValue);
        if (macAddressOptional.isEmpty()) {
            return false;
        }
        MACAddress newMACAddress = new MACAddress(macAddressOptional.get().getId(), newAddressValue, userID);
        macAddressRepository.save(newMACAddress);
        return true;
    }

    public List<MACAddress> getAllMACAddresses(Long userID) {
        return macAddressRepository.findByUserID(userID);
    }
}
