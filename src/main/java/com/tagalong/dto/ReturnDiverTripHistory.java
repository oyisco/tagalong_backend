package com.tagalong.dto;

import com.tagalong.model.Driver;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReturnDiverTripHistory {
    private LocalDateTime timeOfPickUp;
    private String pickUpAddress;
    private String dropOffAddress;
    private LocalDateTime dropOffTime;
    private Object driver;
    private double amountPaid;

}
