package com.tagalong.model.repository;

import com.tagalong.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByDriverEmailAndUserEmailAndStatus(String status, String userEmail, String username);

    List<Request> getRequestByDriverEmailAndStatus(String driverEmail, String status);

    @Query("SELECT r FROM Request  r WHERE r.driverEmail = ?1 and (r.status = ?2 OR r.status = ?3)")
    List<Request> getRequestByDriverEmailAndStatuss(String email,String status1, String status2);


    List<Request> findByDriverEmailOrUserEmail(String email1, String email2);

    Request getRequestByDriverEmailAndUserEmailAndStatus(String driverEmail, String passengerEmail, String status);

}
