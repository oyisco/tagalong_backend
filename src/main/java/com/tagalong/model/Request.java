package com.tagalong.model;

import lombok.*;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request")
public class Request extends Auditable {
    private String userEmail;
    private String driverEmail;
    private String status;
    private Double latitudePassengerFrom;
    private Double longitudePassengerFrom;
    private Double latitudePassengerTo;
    private Double longitudePassengerTo;
    private LocalDateTime timeOfPickUp;
    private String pickUpAddress;
    private String dropOffAddress;
    private LocalDateTime dropOffTime;
    private String duration;
    private String distance;
    private double amountPaid;





}
