package com.fontysio.colleaguetracker.notification;

public class Notification {

    public enum Priority {
        normal,
        high
    }

    public Notification(String body, String title, Priority priority, String sound) {
        this.body = body;
        this.title = title;
        this.priority = priority;
        this.sound = sound;
    }

    private String body;
    private String title;
    private Priority priority;
    private String sound;

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getSound() {
        return sound;
    }
}
