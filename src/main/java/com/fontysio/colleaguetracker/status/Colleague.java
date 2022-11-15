package com.fontysio.colleaguetracker.status;

public class Colleague {
    public Colleague(String firstName, String lastName, StatusObject.Status status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
    }

    private String firstName;
    private String lastName;
    private StatusObject.Status status;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public StatusObject.Status getStatus() {
        return status;
    }
}
