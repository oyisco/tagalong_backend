package com.tagalong.model.repository;

import com.tagalong.dto.OnlineStatus;
import com.tagalong.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Driver findByEmail(String username);


    List<Driver> findByOnlineStatusAndIsAvailable(OnlineStatus onlineStatus, Boolean isAvailable);

    List<Driver> findByOnlineStatus(OnlineStatus onlineStatus);
}
