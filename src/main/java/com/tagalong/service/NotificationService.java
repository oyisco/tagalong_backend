package com.tagalong.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.tagalong.dto.NotificationRequestDto;
import com.tagalong.dto.SubscriptionRequestDto;

import com.tagalong.model.user.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

import java.rmi.server.UID;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class NotificationService {

    @Value("${app.firebase-config}")
    private String firebaseConfig;

    private FirebaseApp firebaseApp;

    @PostConstruct
    private void initialize() {
        try {

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfig).getInputStream())).build();

            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
                System.out.println(firebaseApp);
            }
        } catch (IOException e) {
            log.error("Create FirebaseApp Error", e);
        }
    }


    public void subscribeToTopic(SubscriptionRequestDto subscriptionRequestDto) {
        try {
            FirebaseMessaging.getInstance(firebaseApp).subscribeToTopic(subscriptionRequestDto.getTokens(),
                    subscriptionRequestDto.getTopicName());
        } catch (FirebaseMessagingException e) {
            log.error("Firebase subscribe to topic fail", e);
        }
    }

    public void unsubscribeFromTopic(SubscriptionRequestDto subscriptionRequestDto) {
        try {
            FirebaseMessaging.getInstance(firebaseApp).unsubscribeFromTopic(subscriptionRequestDto.getTokens(),
                    subscriptionRequestDto.getTopicName());
        } catch (FirebaseMessagingException e) {
            log.error("Firebase unsubscribe from topic fail", e);
        }
    }

    public String sendPnsToDevice(NotificationRequestDto notificationRequestDto) {
        Message message = Message.builder()

                .setToken(notificationRequestDto.getFcmToken())

                .setNotification(new Notification(notificationRequestDto.getTitle(), notificationRequestDto.getBody()))
                .putData("content", notificationRequestDto.getTitle())
                .putData("body", notificationRequestDto.getBody())
                .build();
        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send firebase notification", e);
        }

        return response;
    }


    public String sendPnsToDevice2(NotificationRequestDto notificationRequestDto, User user) {
//        Map<String , String> userMap = new HashMap<>();
//        String userId = user.getEmail();
//        userMap.put("passengerId", userId);
        Message message = Message.builder()
                .setToken(notificationRequestDto.getFcmToken())
                .setNotification(new Notification(notificationRequestDto.getTitle(), notificationRequestDto.getBody()))
                .putData("content", notificationRequestDto.getTitle())
                .putData("body", notificationRequestDto.getBody())
                .putData("passengerId", user.getEmail())
                .build();
        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Fail to send firebase notification", e);
        }

        return response;
    }

//
//    public String sendPnsToTopic(NotificationRequestDto notificationRequestDto) {
//        Message message = Message.builder()
//                .setTopic(notificationRequestDto.getFcmToken())
//
//    public String sendPnsToTopic(NotificationRequestDto notificationRequestDto) {
//        Message message = Message.builder()
//                .setTopic(notificationRequestDto.getDeviceId())
//
//                .setNotification(new Notification(notificationRequestDto.getTitle(), notificationRequestDto.getBody()))
//                .putData("content", notificationRequestDto.getTitle())
//                .putData("body", notificationRequestDto.getBody())
//                .build();
//
//        String response = null;
//        try {
//            FirebaseMessaging.getInstance().send(message);
//        } catch (FirebaseMessagingException e) {
//            log.error("Fail to send firebase notification", e);
//        }
//
//        return response;
//    }


    //@PostConstruct
//    public void get() {
//        NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
//        notificationRequestDto.setTitle("Tagalong");
//        notificationRequestDto.setBody("am here waiting ");
//        notificationRequestDto.setFcmToken("ctWkNNiKR4uvHOTYjhkY6p:APA91bGtT6VQDnOpmZATG9LcnHRqdE2QDOwrR2TtlIjInse9NNhAOTnMiz-OJIvUP20EoMEh0ApU63aQl_RZsm0r7ZsidCauDBOSSWKedbw_xmfB8Hz3z0o-rUhXfkRhiCdcs_TKIlCY");
//        sendPnsToDevice2(notificationRequestDto, "oidris@fhi360.org");
//
//    }

    public  String sendPnsToDevice2(NotificationRequestDto notificationRequestDto) {
        Map<String , String> userMap = new HashMap<>();
        String userId = "oidris@fhi360.org";
        userMap.put("passengerId", userId);
        Message message = Message.builder()
                .setToken(notificationRequestDto.getFcmToken())
                .setNotification(new Notification(notificationRequestDto.getTitle(), notificationRequestDto.getBody()))
                .putData("content", notificationRequestDto.getTitle())
                .putData("body", notificationRequestDto.getBody())
            //    .putAllData(userMap)
                .build();
        String response = null;
        try {
            response = FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            //   log.error("Fail to send firebase notification", e);
        }

        return response;
    }
//    private void sendVerificationCode(String number) {
//        // this method is used for getting
//        // OTP on user phone number.
//
//        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseApp)
//                .setPhoneNumber(number)            // Phone number to verify
//                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
//                .setActivity(this)                 // Activity (for callback binding)
//                .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
//                .build();
//        PhoneAuthProvider.verifyPhoneNumber(options);
//
//    }



}
