package com.tagalong.dto;

import lombok.Data;

@Data
public class NotificationRequestDto {

    private String deviceId;
    private String title;
    private String body;
}
