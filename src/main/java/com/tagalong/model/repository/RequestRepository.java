package com.tagalong.model.repository;

import com.tagalong.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request>  findByEmail(String username);

    List<Request>  findByUserIdAndDriverId(Long userId, Long driverId);

}
