package com.tagalong.model.repository;

import com.tagalong.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByDriverEmailAndUserEmailAndStatus(String status,String userEmail, String username);

    List<Request>  getRequestByDriverEmailAndStatus(String driverEmail, String status);

    //@Query("SELECT r FROM request r WHERE r.driver_email = ?1 OR r.user_email = ?2")
    List<Request>  findByDriverEmailOrUserEmail(String email1, String email2);


}
