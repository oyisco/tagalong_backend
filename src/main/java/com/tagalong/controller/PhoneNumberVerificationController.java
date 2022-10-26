package com.tagalong.controller;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "api/phoneNumber")
@Slf4j
public class PhoneNumberVerificationController {

    @GetMapping(value = "/generateTOTP")
    public ResponseEntity<String> generateTOTP(@RequestParam(defaultValue="phone") String phone){
        Twilio.init("AC1cb41c165af7aa907e778ca797142167", "491b5b99c7ee8a6cbf78b7668ad40599");
        try {

            Verification verification = Verification.creator(
                            "VA744925cb58a819cc9ff07587bbff3143",
                            phone, //this is your Twilio verified recipient phone number
                            "sms") // this is your channel type
                    .create();
            System.out.println(verification.getStatus());

            log.info("TOTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());
        }catch (Exception e) {

            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>("Your TOTP has been sent to your verified phone number", HttpStatus.OK);
    }
    @GetMapping("/verifyTOTP")
    public ResponseEntity<?> verifyUserTOTP(@RequestParam(defaultValue="phone") String phone, @RequestParam(defaultValue="code") String code) throws Exception {
        Twilio.init("AC1cb41c165af7aa907e778ca797142167", "491b5b99c7ee8a6cbf78b7668ad40599");

        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(
                            "VA744925cb58a819cc9ff07587bbff3143")
                    .setTo(phone)
                    .setCode(code)
                    .create();

            System.out.println(verificationCheck.getStatus());

        } catch (Exception e) {
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("This user's verification has been completed successfully", HttpStatus.OK);
    }
}
