package com.tagalong.model;

import com.tagalong.model.Document;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Data
@Embeddable
public class Vehicle {
    private String plate;
    private String number;
    private String color;
    private String manufacturer;
    private String model;
    private String year;
    @Embedded
    private Document document;

}
