package com.tagalong.model.repository;

import com.tagalong.dto.OnlineStatus;
import com.tagalong.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String username);

    List<Driver> findByOnlineStatusAndIsAvailable(OnlineStatus onlineStatus, Boolean isAvailable);

    List<Driver> findByOnlineStatus(OnlineStatus onlineStatus);

    String HAVERSINE_FORMULA = "(6371 * acos(cos(radians(:latitudeDriverFrom)) * cos(radians(s.longitudeDriverFrom)) *" +
            " cos(radians(s.longitudeDriverFrom) - radians(:longitudeDriverFrom)) + sin(radians(:latitudeDriverFrom)) * sin(radians(s.latitudeDriverFrom))))";
    @Query("SELECT s FROM Driver s WHERE " + HAVERSINE_FORMULA + " < :distance ORDER BY " + HAVERSINE_FORMULA + " DESC")
    Driver findStoresWithInDistance(@Param("latitudeDriverFrom") double latitude, @Param("longitudeDriverFrom") double longitude, @Param("distance") double distanceWithInKM);

}
