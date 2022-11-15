package com.fontysio.colleaguetracker.macaddress;

import javax.persistence.*;

@Entity(name = "mac_address")
public class MACAddress {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="mac_address_id_gen")
    @SequenceGenerator(
            name = "mac_address_id_gen",
            sequenceName = "mac_address_sequence",
            allocationSize = 1
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "address_value",
            nullable = false
    )
    private String addressValue;

    @Column(
            name = "user_id",
            nullable = false
    )
    private Long userID;

    public MACAddress(String addressValue, Long userID) {
        this.addressValue = addressValue;
        this.userID = userID;
    }

    protected MACAddress() {

    }

    public Long getId() {
        return id;
    }

    public String getAddressValue() {
        return addressValue;
    }

    public Long getUserID() {
        return userID;
    }
}
