package com.fontysio.colleaguetracker.status;

public class Colleague {
    public Colleague(String firstName, String lastName, StatusObject status, String id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.id = id;
    }

    private String firstName;
    private String lastName;
    private StatusObject status;
    private String id;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public StatusObject getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}
