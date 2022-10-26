package com.tagalong.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class Document {
    private String DriverLicenseDocument;
    private String VehicleLicenseDocument;
    private String personalPhoto;
}
