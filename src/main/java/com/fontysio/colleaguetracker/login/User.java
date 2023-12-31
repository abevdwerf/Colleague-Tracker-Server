package com.fontysio.colleaguetracker.login;

import com.fontysio.colleaguetracker.status.StatusObject;

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
    private String email;

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
            nullable = true
    )
    private String firstName;

    @Column(
            name="last_name",
            nullable = true
    )
    private String lastName;

    protected User() {}

    public User(String externalID, String firstName, String lastName) {
        super();
        this.enabled=false;
        this.externalID = externalID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(final String email) {
        this.email = email;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public String getExternalID() {
        return externalID;
    }
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }

}
