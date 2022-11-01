package com.tagalong.controller;


import com.tagalong.dto.*;
import com.tagalong.exception.CarAlreadyInUseException;
import com.tagalong.exception.ConstraintsViolationException;
import com.tagalong.exception.EntityNotFoundException;
import com.tagalong.model.Driver;
import com.tagalong.service.DriverService;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * All operations with a driver will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;


    @GetMapping("/get-driver/{driverId}")
    public ResponseEntity<Driver> getDriver(@Valid @PathVariable long driverId, @RequestHeader("Authorization") String Authorization) throws EntityNotFoundException {
        return ResponseEntity.ok(driverService.find(driverId));
    }


    @PostMapping("/create-driver")
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody Driver driver) throws ConstraintsViolationException {

        return ResponseEntity.ok(this.driverService.create(driver));
    }


    @DeleteMapping("/delete-driver/{driverId}")
    public void deleteDriver(@PathVariable long driverId, @RequestHeader("Authorization") String Authorization) throws EntityNotFoundException {
        driverService.delete(driverId);
    }


    @PutMapping
    public void updateUpdateInformation(@RequestBody DriverUpdateLocation updateDriverLocation, @RequestHeader("Authorization") String Authorization) {
        driverService.updateLocation(updateDriverLocation);
    }


    @GetMapping
    public ResponseEntity<List<Driver>> findDriversByStatus(@RequestParam OnlineStatus onlineStatus, @RequestHeader("Authorization") String Authorization) {
        return ResponseEntity.ok(driverService.find(onlineStatus));
    }

    @GetMapping("/accept-request")
    public void acceptRequest(@RequestParam String driverEmail, @RequestHeader("Authorization") String Authorization) {


        driverService.acceptRequest(driverEmail);
    }

    @GetMapping("/reject-request")
    public void rejectRequest(@RequestParam String driverEmail, @RequestHeader("Authorization") String Authorization) {

        driverService.rejectRequest(driverEmail);
    }

    @PostMapping("/start-ride")
    public void startRide(@RequestBody StartRideDto startRideDto, @RequestHeader("Authorization") String Authorization) {

        driverService.startRide(startRideDto);
    }

    @PostMapping("/end-ride")
    public void endRide(@RequestBody EndDto endDto, @RequestHeader("Authorization") String Authorization) {

        driverService.endRide(endDto);
    }

    @GetMapping("/trip-history")
    public void tripHistory(@RequestParam String email, @RequestHeader("Authorization") String Authorization) {
        driverService.tripHistory(email);
    }


}
