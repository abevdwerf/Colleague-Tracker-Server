package com.fontysio.colleaguetracker.status;

import com.fontysio.colleaguetracker.login.User;
import com.google.api.client.util.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public boolean setStatus(StatusObject.Status status, String expirationTime, User user){
        Optional<StatusObject> test = statusRepository.findById(user.getId());
        StatusObject statusObject;
        if (test.isEmpty()){
            statusObject = new StatusObject(status, expirationTime, user);
        } else {
            statusObject = statusRepository.getStatusObjectByUser(user);
            statusObject.setStatus(status);
            statusObject.setExpirationTime(expirationTime);
        }
        statusRepository.save(statusObject);


        try {
            getStatus(user);
            return true;
        } catch (NoStatusFoundException e) {
            String error = "Status for User with ID: " + user.getId() + "was empty after creating new status";
            throw new RuntimeException(error);
        } /*catch (StatusExpiredException e) {
            String error = "Status for User with ID: " + user.getId() + "was already expired after creating new status";
            throw new RuntimeException(error);
        }*/

    }

    public boolean isStatusExpired(StatusObject statusObject){
        Long currentDate = System.currentTimeMillis();
        Long expirationDate = Long.parseLong(statusObject.getExpirationTime()) * 1000;

//        SimpleDateFormat sdf;
//        sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//        sdf.setTimeZone(TimeZone.getTimeZone("CEST"));
//        String textNow = sdf.format(currentDate);
//        String textExpiration = sdf.format(expirationDate);
        return currentDate > expirationDate;
    }

    public StatusObject getStatus(User user) throws NoStatusFoundException {
        StatusObject statusObject = statusRepository.getStatusObjectByUser(user);
        if (statusObject != null) {
            if (isStatusExpired(statusObject)) {
                statusObject.setStatus(StatusObject.Status.Expired);
                statusRepository.save(statusObject);
            }
            return statusObject;
        } else {
            throw new NoStatusFoundException();
        }
    }

    public List<Colleague> getAllColleagues(List<User> users, User currentUser){
        List<Colleague> colleagueList = new ArrayList<>();
        List<StatusObject> statusList = statusRepository.findAll();
        for (User user:users) {
            if (user.getId() != currentUser.getId() && user.isEnabled()) {
                hasUserStatus(user, statusList, colleagueList);
            }
        }
        return Collections.unmodifiableList(colleagueList);
    }
    private void hasUserStatus(User user, List<StatusObject> statusList , List<Colleague> colleagueList) {
        for (StatusObject status:statusList) {
            if (user.getId() == status.getUser().getId()) {
                colleagueList.add(new Colleague(user.getFirstName(), user.getLastName(), status.getStatus(), user.getId()));
                return;
            }
        }
        colleagueList.add(new Colleague(user.getFirstName(), user.getLastName(), StatusObject.Status.Unknown, user.getId()));
    }
}
