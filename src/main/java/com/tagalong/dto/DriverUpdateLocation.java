package com.tagalong.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class DriverUpdateLocation {
    private String driverEmail;
    private double latitudeDriverFrom;
    private double longitudeDriverFrom;
    private double longitudeDriverTo;
    private double latitudeDriverTo;
    private int vehicleSeat;
    private String driverFCMToken;
}
