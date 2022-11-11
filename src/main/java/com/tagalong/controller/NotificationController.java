package com.tagalong.controller;


import com.tagalong.dto.NotificationRequestDto;
import com.tagalong.dto.SubscriptionRequestDto;
import com.tagalong.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

//    @PostMapping("/subscribe")
//    public void subscribeToTopic(@RequestBody SubscriptionRequestDto subscriptionRequestDto) {
//        notificationService.subscribeToTopic(subscriptionRequestDto);
//    }
//
//    @PostMapping("/unsubscribe")
//    public void unsubscribeFromTopic(SubscriptionRequestDto subscriptionRequestDto) {
//        notificationService.unsubscribeFromTopic(subscriptionRequestDto);
//    }

    @PostMapping("/token")
    public String sendPnsToDevice(@RequestBody NotificationRequestDto notificationRequestDto) {
        return notificationService.sendPnsToDevice(notificationRequestDto);
    }
//
//    @PostMapping("/topic")
//    public String sendPnsToTopic(@RequestBody NotificationRequestDto notificationRequestDto) {
//        return notificationService.sendPnsToTopic(notificationRequestDto);
//    }
<<<<<<< HEAD


=======
>>>>>>> origin/main
}
