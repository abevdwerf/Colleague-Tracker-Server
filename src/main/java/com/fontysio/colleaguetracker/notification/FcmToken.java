package com.fontysio.colleaguetracker.notification;

import javax.persistence.*;

@Entity
public class FcmToken {

    public FcmToken(Long userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "fcm_token", nullable = false)
    private String fcmToken;

    protected FcmToken() {

    }


    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}
