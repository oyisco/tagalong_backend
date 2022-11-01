package com.tagalong.dto;

import lombok.Data;

@Data
public class GeolocationDto {
    private String email;
    private Double latitudePassengerFrom;
    private Double longitudePassengerFrom;
    // private String driverFCMToken;
    private String passengerFCMToken;
    private Double latitudePassengerTo;
    private Double longitudePassengerTo;


}
