package com.fontysio.colleaguetracker.macaddress;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class  MACAddressService {
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

    public boolean updateMACAddress(Long macAddressID, String newAddressValue, Long userID) throws MACAddressAlreadyPresentWithUserException {
        Optional<MACAddress> macAddressOptional = macAddressRepository.findByUserIDAndId(userID, macAddressID);
        if (macAddressOptional.isEmpty()) {
            return false;
        }
        Optional<MACAddress> newMACAddressOptional = macAddressRepository.findByUserIDAndAddressValue(userID, newAddressValue);
        if (newMACAddressOptional.isPresent()) {
            throw new MACAddressAlreadyPresentWithUserException();
        }
        MACAddress newMACAddress = new MACAddress(macAddressOptional.get().getId(), newAddressValue, userID);
        macAddressRepository.save(newMACAddress);
        return true;
    }

    public List<Long> getUserIdsByMacAddresses(List<String> macAddresses) {
        List<Long> userIds = new ArrayList<>();
        for (String macAddress:macAddresses) {
            MACAddress foundMacAddress = macAddressRepository.findByAddressValue(macAddress);
            if (foundMacAddress != null) {
                userIds.add(foundMacAddress.getUserID());
            }
        }
        return userIds;

    }

    public List<MACAddress> getAllMACAddresses(Long userID) {
        return macAddressRepository.findByUserID(userID);
    }
}
