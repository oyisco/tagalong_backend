package com.tagalong.dto;

import lombok.Data;

@Data
public class StartRideDto {
    private String driverEmail;
    private String passengerEmail;
    private String pickUpAddress;
}
