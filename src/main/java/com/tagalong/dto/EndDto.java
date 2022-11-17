package com.tagalong.dto;

import lombok.Data;

@Data
public class EndDto {
    String driverEmail;
    String passengerEmail;
    String dropOffAddress;
    double amountPaid;
}
