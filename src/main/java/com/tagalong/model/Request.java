package com.tagalong.model;

import lombok.*;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request")
public class Request extends Auditable {
    private String email;
    private Long userId;
    private Long driverId;
    private String status;

    private Double latitudePassengerFrom;
    private Double longitudePassengerFrom;

    private Double latitudePassengerTo;
    private Double longitudePassengerTo;


}
