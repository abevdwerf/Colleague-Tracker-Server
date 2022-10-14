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
    private String email;

    @Column(
            name = "external_id",
            unique = true,
            nullable = false
    )
    private String externalID;

    @Column(name = "enabled")
    private boolean enabled;

    protected User() {}

    public User(String externalID) {
        super();
        this.enabled=false;
        this.externalID = externalID;
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

    public String getExternalID() {
        return externalID;
    }
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
}
