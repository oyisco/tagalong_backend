package com.tagalong.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@EqualsAndHashCode(callSuper = false)
@Entity
@Data
@NoArgsConstructor
@Table(name = "photos")
public class Photo {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(length = 200)
    private String email;

    @NotBlank
    @Column(length = 90000)
    private String image;

}
