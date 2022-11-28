package com.fontysio.colleaguetracker.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {
    boolean existsFcmTokenByFcmToken(String fcmToken);
    FcmToken findByFcmToken(String fcmToken);

    List<FcmToken> findAllByUserId(Long userId);
}
