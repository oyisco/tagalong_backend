package com.tagalong.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatcherDto2 {
    private String userEmail;
    private String firstName;
    private String lastName;
    private String image;
    private String phoneNumber;
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
