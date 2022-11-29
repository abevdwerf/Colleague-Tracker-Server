package com.fontysio.colleaguetracker.notification.services;

import com.fontysio.colleaguetracker.login.User;
import com.fontysio.colleaguetracker.notification.Notification;

public interface INotificationService_Status {
    boolean SendNotification(User user, Notification notification);
}
