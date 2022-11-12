package com.tagalong.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
public class Document {
    @Column(length = 90000)
    private String DriverLicenseDocument;
    @Column(length = 90000)
    private String VehicleLicenseDocument;
    @Column(length = 90000)
    private String personalPhoto;
}
