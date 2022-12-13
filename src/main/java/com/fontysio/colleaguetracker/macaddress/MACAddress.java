package com.fontysio.colleaguetracker.macaddress;

import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity(name = "mac_address")
@AllArgsConstructor
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

    @Column(
            name = "label",
            nullable = true
    )
    private String label;

    public MACAddress(String addressValue, Long userID, String label) {
        this.addressValue = addressValue;
        this.userID = userID;
        this.label = label;
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

    public String getLabel() {
        return label;
    }
}
