package com.tagalong.service;


import com.tagalong.dto.*;
import com.tagalong.exception.ConstraintsViolationException;
import com.tagalong.model.Driver;
import com.tagalong.model.Request;
import com.tagalong.model.repository.DriverRepository;
import com.tagalong.model.repository.RequestRepository;
import com.tagalong.model.repository.UserRepository;
import com.tagalong.model.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service to encapsulate the link between DAO and controller and to have business logic for some driver specific things.
 * <p/>
 */
@Service
@RequiredArgsConstructor
public class DriverService {
    private static org.slf4j.Logger LOG = LoggerFactory.getLogger(DriverService.class);
    private final DriverRepository driverRepository;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final RequestRepository requestRepository;


    public Driver find(Long driverId) throws EntityNotFoundException {
        return findDriverChecked(driverId);
    }


    /**
     * Creates a new driver.
     *
     * @param driverDO
     * @return
     * @throws ConstraintsViolationException if a driver already exists with the given username, ... .
     */

    public DriverResponseDTO create(Driver driverDO) throws ConstraintsViolationException {
        DriverResponseDTO dto = new DriverResponseDTO();
        User user = new User();
        try {
            user.setAccountType(driverDO.getAccountType());
            user.setFirstName(driverDO.getFirstName());
            user.setLastName(driverDO.getLastName());
            user.setEmail(driverDO.getEmail());
            user.setPassword(encoder.encode(driverDO.getPassword()));
            user.setPhone(driverDO.getPhoneNumber());
            user.setVerified(driverDO.getVerified());
            driverDO.setVehicleSeat(3);
            driverDO.setPassword(encoder.encode(driverDO.getPassword()));
            driverRepository.save(driverDO);
            this.userRepository.save(user);
            dto.setDriver(driverDO);
        } catch (DataIntegrityViolationException e) {
            dto.setMessage("Error while creating " + e.getMessage());
            LOG.warn("Some constraints are thrown due to driver creation", e);
            throw new ConstraintsViolationException(e.getMessage());
        }
        return dto;
    }


    /**
     * Deletes an existing driver by id.
     *
     * @param driverId
     * @throws EntityNotFoundException if no driver with the given id was found.
     */

    @Transactional
    public void delete(Long driverId) throws EntityNotFoundException {
        Driver driverDO = findDriverChecked(driverId);
        driverDO.setDeleted(true);
        driverDO.setIsAvailable(Boolean.FALSE);
    }


//    /**
//     * Update the location for a driver.
//     *
//     * @param driverId
//     * @param longitude
//     * @param latitude
//     * @throws EntityNotFoundException
//     */

    @Transactional
    public void updateLocation(DriverUpdateLocation updateLocation) throws EntityNotFoundException {
        Driver driverDO = this.driverRepository.findByEmail(updateLocation.getDriverEmail()).orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        driverDO.setLatitudeDriverFrom(updateLocation.getLatitudeDriverFrom());
        driverDO.setLongitudeDriverFrom(updateLocation.getLongitudeDriverFrom());
        driverDO.setLatitudeDriverTo(updateLocation.getLatitudeDriverTo());
        driverDO.setLongitudeDriverTo(updateLocation.getLongitudeDriverTo());
        driverDO.setOnlineStatus(OnlineStatus.ONLINE);
        driverDO.setDriverFCMToken(updateLocation.getDriverFCMToken());
        driverDO.setVehicleSeat(updateLocation.getVehicleSeat());
        driverRepository.save(driverDO);
    }


    @Transactional
    public void acceptRequest(String driverEmail) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndStatus(driverEmail, "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("ride started");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("your request has been accepted from " + findDriverByEmail(driverEmail).getFirstName() + " " + findDriverByEmail(driverEmail).getLastName());
            notificationRequestDto.setTitle("accepted request");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(driverEmail);
        driver.setAcceptStatus("Accepted");
        driverRepository.save(driver);
        this.requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(String driverEmail) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndStatus(driverEmail, "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        request.setStatus("matchRejected");

        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("ride rejected");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("your request has been rejected from " + findDriverByEmail(driverEmail).getFirstName() + " " + findDriverByEmail(driverEmail).getLastName());
            notificationRequestDto.setTitle("rejected request");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(driverEmail);
        driver.setAcceptStatus("rejected request");
        driverRepository.save(driver);
        this.requestRepository.save(request);
    }

    @Transactional
    public void startRide(StartRideDto startRideDto) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndStatus(startRideDto.getDriverEmail(), "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        request.setStatus("startRide");
        request.setTimeOfPickUp(LocalDateTime.now());
        request.setPickUpAddress(startRideDto.getPickUpAddress());
        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("ride rejected");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("driver has started the ride ");
            notificationRequestDto.setTitle("Started ride");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(startRideDto.getDriverEmail());
        driver.setAcceptStatus("Started ride");
        driverRepository.save(driver);
        this.requestRepository.save(request);
    }

    @Transactional
    public void endRide(EndDto endDto) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndStatus(endDto.getDriverEmail(), "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        request.setStatus("endRide");
        request.setDropOffTime(LocalDateTime.now());
        request.setDropOffAddress(endDto.getDropOffAddress());
        request.setAmountPaid(endDto.getAmountPaid());
        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("ride rejected");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("driver has ended the ride ");
            notificationRequestDto.setTitle("End ride");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(endDto.getDriverEmail());
        driver.setAcceptStatus("End ride");
        driverRepository.save(driver);
        this.requestRepository.save(request);
    }

    public List<Request> tripHistory(String email) throws EntityNotFoundException {
        String email1 = email;
        String emeil2 = email;
        return this.requestRepository.findByDriverEmailOrUserEmail(email1, emeil2);


    }


    /**
     * Find all drivers by online state.
     *
     * @param onlineStatus
     */

    public List<Driver> find(OnlineStatus onlineStatus) {
        return driverRepository.findByOnlineStatus(onlineStatus);
    }


    private Driver findDriverChecked(Long driverId) throws EntityNotFoundException {
        return driverRepository
                .findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: " + driverId));
    }


    private Driver findDriverByEmail(String driverEmail) throws EntityNotFoundException {
        return driverRepository
                .findByEmail(driverEmail)
                .orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: " + driverEmail));
    }

}
