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

    @Column(name = "enabled")
    private boolean enabled;




    @Column(
            name="first_name",
            nullable = false
    )
    private String firstName;

    @Column(
            name="last_name",
            nullable = false
    )
    private String lastName;

    protected User() {}

    public User(String externalID, String firstName, String lastName) {
        this.externalID = externalID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public String getExternalID() {
        return externalID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

}
