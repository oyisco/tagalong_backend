package com.tagalong.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.tagalong.dto.OnlineStatus;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driver")
public class Driver extends Auditable {
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Boolean verified = false;
    @Embedded
    private Vehicle vehicle;
    @JsonIgnore
    private Boolean isAvailable = false;
    @JsonIgnore
    private Boolean deleted = false;
    @JsonIgnore
    private OnlineStatus onlineStatus;
    @JsonIgnore
    private double latitudeDriverFrom;
    @JsonIgnore
    private double longitudeDriverFrom;
    @JsonIgnore
    private double latitudeDriverTo;
    @JsonIgnore
    private double longitudeDriverTo;
    @JsonIgnore
    private String vehicleSeat;
}
