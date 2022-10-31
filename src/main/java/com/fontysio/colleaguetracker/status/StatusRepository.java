package com.fontysio.colleaguetracker.status;

import com.fontysio.colleaguetracker.login.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<StatusObject, Long> {
    StatusObject getStatusObjectByUser(User user);
}
