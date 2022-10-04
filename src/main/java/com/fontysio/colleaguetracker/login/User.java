package com.fontysio.colleaguetracker.login;

import javax.persistence.*;

@Entity(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="user_id_gen")
    @SequenceGenerator(
            name = "user_id_gen",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(
            name = "external_id",
            unique = true,
            nullable = false
    )
    private String externalID;

    protected User() {}

    public User(String externalID) {
        this.externalID = externalID;
    }

    public Long getId() {
        return id;
    }

    public String getExternalID() {
        return externalID;
    }
}
