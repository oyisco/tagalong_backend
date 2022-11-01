package com.tagalong.dto;

import com.google.maps.model.Distance;
import com.google.maps.model.Duration;
import lombok.Data;

@Data
public class MatcherDto {
   private String message;
   private Object driver;
   private Object passenger;
   private String duration;
   private String  distance;




}
