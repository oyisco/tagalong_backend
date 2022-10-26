package com.tagalong.controller;


import com.tagalong.dto.OnlineStatus;
import com.tagalong.exception.CarAlreadyInUseException;
import com.tagalong.exception.ConstraintsViolationException;
import com.tagalong.exception.EntityNotFoundException;
import com.tagalong.model.Driver;
import com.tagalong.service.DriverService;
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
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;


    @GetMapping("/{driverId}")
    public ResponseEntity<Driver> getDriver(@Valid @PathVariable long driverId) throws EntityNotFoundException {
        return ResponseEntity.ok(driverService.find(driverId));
    }


    @PostMapping
    public ResponseEntity<Driver> createDriver(@Valid @RequestBody Driver driver) throws ConstraintsViolationException {

        return ResponseEntity.ok(this.driverService.create(driver));
    }


    @DeleteMapping("/{driverId}")
    public void deleteDriver(@PathVariable long driverId) throws EntityNotFoundException {
        driverService.delete(driverId);
    }


    @PutMapping("/{driverId}")
    public void updateLocation(
            @Valid @PathVariable long driverId, @RequestParam double longitude, @RequestParam double latitude, OnlineStatus onlineStatus)
            throws ConstraintsViolationException, EntityNotFoundException {
        driverService.updateLocation(driverId, longitude, latitude, onlineStatus);
    }


    @GetMapping
    public ResponseEntity<List<Driver>> findDriversByStatus(@RequestParam OnlineStatus onlineStatus)
            throws ConstraintsViolationException, EntityNotFoundException {
        return ResponseEntity.ok(driverService.find(onlineStatus));
    }






}
