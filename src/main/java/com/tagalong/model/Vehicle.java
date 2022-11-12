package com.tagalong.model;

import com.tagalong.model.Document;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Data
@Embeddable
public class Vehicle {
    @Column(length = 90000)
    private String plate;
    @Column(length = 90000)
    private String number;
    @Column(length = 90000)
    private String color;
    @Column(length = 90000)
    private String manufacturer;
    @Column(length = 90000)
    private String model;
    @Column(length = 90000)
    private String year;
    @Embedded
    private Document document;

}
