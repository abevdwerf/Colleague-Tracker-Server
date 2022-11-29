package com.fontysio.colleaguetracker.status;

import com.fontysio.colleaguetracker.login.User;
import com.google.api.client.util.DateTime;

import javax.persistence.*;

@Entity(name = "status")
public class StatusObject {

    enum Status {
        Home,
        Office,
        Expired,
        Unknown,
    }

    protected StatusObject() {}

    public StatusObject(Status status, String expirationTime, String beginTime, User user) {
        this.status = status;
        this.expirationTime = expirationTime;
        this.beginTime = beginTime;
        this.user = user;
    }

    @Id
    private Long id;
    @Column(name = "status")
    private Status status;
    @Column(name = "expiration_time")
    private String expirationTime;

    @Column(name = "begin_time")
    private String beginTime;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public Status getStatus() {
        return status;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public User getUser() {
        return user;
    }

    public Long getId(){
        return id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }
}
