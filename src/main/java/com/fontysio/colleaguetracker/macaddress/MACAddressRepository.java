package com.fontysio.colleaguetracker.macaddress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MACAddressRepository extends JpaRepository<MACAddress, Long> {
    Optional<MACAddress> findByUserIDAndAddressValue(Long userID, String addressValue);
}
