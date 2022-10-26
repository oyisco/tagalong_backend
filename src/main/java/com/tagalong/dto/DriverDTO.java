package com.tagalong.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tagalong.model.Vehicle;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DriverDTO {
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean verified = false;
    private Vehicle vehicle;
}
