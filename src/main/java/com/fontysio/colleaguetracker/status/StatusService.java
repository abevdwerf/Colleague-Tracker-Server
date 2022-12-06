package com.fontysio.colleaguetracker.status;

import com.fontysio.colleaguetracker.login.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public boolean setStatus(StatusObject.Status status, String beginTime, String expirationTime, User user){
        Optional<StatusObject> test = statusRepository.findById(user.getId());
        StatusObject statusObject;
        if (test.isEmpty()){
            statusObject = new StatusObject(status, expirationTime, beginTime, user);
        } else {
            statusObject = statusRepository.getStatusObjectByUser(user);
            statusObject.setStatus(status);
            statusObject.setExpirationTime(expirationTime);
            statusObject.setBeginTime(beginTime);
        }
        isStatusNotActive(statusObject);
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
    public void resetAllStatusToNotDetected() {
        for (StatusObject status: statusRepository.findAll()) {
            status.setDetectedAtOffice(false);
            statusRepository.save(status);
        }
    }

    public void changeIsDetectedAtOffice(User user, Boolean isDetected) {
        Optional<StatusObject> test = statusRepository.findById(user.getId());
        StatusObject statusObject;
        if (test.isEmpty()){
            return;
        } else {
            statusObject = statusRepository.getStatusObjectByUser(user);
            statusObject.setDetectedAtOffice(isDetected);
        }
        statusRepository.save(statusObject);
    }

    public boolean isStatusNotActive(StatusObject statusObject){
        Long currentDate = System.currentTimeMillis();

        if (statusObject.getBeginTime() == null || statusObject.getExpirationTime() == null) {

            return true;
        } else {
            Long beginDate = Long.parseLong(statusObject.getBeginTime()) * 1000;
            Long expirationDate = Long.parseLong(statusObject.getExpirationTime()) * 1000;

            boolean isNotActive = currentDate < beginDate || currentDate > expirationDate;
            if (currentDate > expirationDate) {
                statusObject.setBeginTime(null);
                statusObject.setExpirationTime(null);
            }
            statusObject.setActive(!isNotActive);
            statusRepository.save(statusObject);
            return isNotActive;
        }

    }

    public StatusObject getStatus(User user) throws NoStatusFoundException {
        StatusObject statusObject = statusRepository.getStatusObjectByUser(user);
        if (statusObject != null) {
            if (isStatusNotActive(statusObject)) {
                statusObject.setStatus(StatusObject.Status.Unknown);
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
            if (!Objects.equals(user.getId(), currentUser.getId()) && user.isEnabled()) {
                hasUserStatus(user, statusList, colleagueList);
            }
        }
        return Collections.unmodifiableList(colleagueList);
    }
    private void hasUserStatus(User user, List<StatusObject> statusList , List<Colleague> colleagueList) {
        for (StatusObject status:statusList) {
            if (user.getId() == status.getUser().getId()) {
                colleagueList.add(new Colleague(user.getFirstName(), user.getLastName(), status.getStatus(), user.getExternalID()));
                return;
            }
        }
        colleagueList.add(new Colleague(user.getFirstName(), user.getLastName(), StatusObject.Status.Unknown, user.getExternalID()));
    }


    public List<Long> needToSetStatus(List<User> users) {
        List<Long> needToSetStatusList = new ArrayList<>();
        for (User user:users) {
            boolean userHasStatus = false;
            if (user.isEnabled()) {
                for (StatusObject status:statusRepository.findAll()) {
                    if (user.getId() == status.getUser().getId()) {
                        userHasStatus = true;
                        if (status.getBeginTime() == null || status.getExpirationTime() == null) {
                            needToSetStatusList.add(user.getId());
                            break;
                        } else {
                            if (isStatusNotActive(status)) {
                                if (status.getBeginTime() == null) {
                                    needToSetStatusList.add(user.getId());
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!userHasStatus) {
                    needToSetStatusList.add(user.getId());
                }
            }
        }
        return needToSetStatusList;
    }
}
