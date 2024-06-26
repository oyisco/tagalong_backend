package com.tagalong.service;


import com.tagalong.dto.*;
import com.tagalong.exception.ConstraintsViolationException;
import com.tagalong.model.Driver;
import com.tagalong.model.Photo;
import com.tagalong.model.Request;
import com.tagalong.model.repository.DriverRepository;
import com.tagalong.model.repository.PhotoRepository;
import com.tagalong.model.repository.RequestRepository;
import com.tagalong.model.repository.UserRepository;
import com.tagalong.model.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.ManagedList;
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
    private final PhotoRepository photoRepository;


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
        Optional<Driver> driverDO1 = this.driverRepository.findByEmail(updateLocation.getDriverEmail());
        if (driverDO1.isPresent()) {
            Driver driverDO = driverDO1.get();
            driverDO.setLatitudeDriverFrom(updateLocation.getLatitudeDriverFrom());
            driverDO.setLongitudeDriverFrom(updateLocation.getLongitudeDriverFrom());
            driverDO.setLatitudeDriverTo(updateLocation.getLatitudeDriverTo());
            driverDO.setLongitudeDriverTo(updateLocation.getLongitudeDriverTo());
            driverDO.setOnlineStatus(OnlineStatus.ONLINE);
            driverDO.setDriverFCMToken(updateLocation.getDriverFCMToken());
            driverDO.setVehicleSeat(updateLocation.getVehicleSeat());
            driverRepository.save(driverDO);
        }
    }


    @Transactional
    public void goOffLine(DriverUpdateLocation driverUpdateLocation) throws EntityNotFoundException {
        Optional<Driver> driverDO1 = this.driverRepository.findByEmail(driverUpdateLocation.getDriverEmail());
        if (driverDO1.isPresent()) {
            Driver driverDO = driverDO1.get();
            driverDO.setLatitudeDriverFrom(driverUpdateLocation.getLatitudeDriverFrom());
            driverDO.setLongitudeDriverFrom(driverUpdateLocation.getLongitudeDriverFrom());
            driverDO.setLatitudeDriverTo(driverUpdateLocation.getLatitudeDriverTo());
            driverDO.setLongitudeDriverTo(driverUpdateLocation.getLongitudeDriverTo());
            driverDO.setOnlineStatus(OnlineStatus.OFFLINE);
            driverRepository.save(driverDO);
        }
    }


//
//    public  acceptRequest(String driverEmail) throws EntityNotFoundException {
//        Request request = this.requestRepository.findByDriverEmailAndStatus(driverEmail, "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
//        // driverDO.setAcceptStatus("Accepted");
//        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
//        if (user.isPresent()) {
//            User user1 = user.get();
//            user1.setAcceptStatus("ride started");
//            this.userRepository.save(user1);
//            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
//            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
//            notificationRequestDto.setBody("your request has been accepted from " + findDriverByEmail(driverEmail).getFirstName() + " " + findDriverByEmail(driverEmail).getLastName());
//            notificationRequestDto.setTitle("accepted request");
//            this.notificationService.sendPnsToDevice(notificationRequestDto);
//        }
//        Driver driver = findDriverByEmail(driverEmail);
//        driver.setAcceptStatus("Accepted");
//        driverRepository.save(driver);
//        this.requestRepository.save(request);
//    }


    @Transactional
    public void acceptRequest(String driverEmail, String passengerEmail) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndUserEmailAndStatus(driverEmail, passengerEmail, "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        request.setStatus("accepted");
        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("accepted");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("your request has been accepted from " + findDriverByEmail(driverEmail).getFirstName() + " " + findDriverByEmail(driverEmail).getLastName());
            notificationRequestDto.setTitle("accepted request");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(driverEmail);
        driver.setAcceptStatus("accepted");
        driverRepository.save(driver);
        this.requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(String driverEmail, String passengerEmail) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndUserEmailAndStatus(driverEmail, passengerEmail, "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        request.setStatus("rejectedrequest");

        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("rejectedrequest");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("your request has been rejected from " + findDriverByEmail(driverEmail).getFirstName() + " " + findDriverByEmail(driverEmail).getLastName());
            notificationRequestDto.setTitle("rejected request");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(driverEmail);
        driver.setRejectStatus("rejectedrequest");
        driverRepository.save(driver);
        this.requestRepository.save(request);
    }

    @Transactional
    public void startRide(StartRideDto startRideDto) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndUserEmailAndStatus(startRideDto.getDriverEmail(), startRideDto.getPassengerEmail(), "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        request.setStatus("startRide");
        request.setTimeOfPickUp(LocalDateTime.now());
        request.setPickUpAddress(startRideDto.getPickUpAddress());
        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("startRide");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("driver has started the ride ");
            notificationRequestDto.setTitle("Started ride");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(startRideDto.getDriverEmail());
        driver.setStartTripStatus("startRide");
        driverRepository.save(driver);
        this.requestRepository.save(request);
    }

    @Transactional
    public void endRide(EndDto endDto) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndUserEmailAndStatus(endDto.getDriverEmail(), endDto.getPassengerEmail(), "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        request.setStatus("endRide");
        request.setDropOffTime(LocalDateTime.now());
        request.setDropOffAddress(endDto.getDropOffAddress());
        request.setAmountPaid(endDto.getAmountPaid());
        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("endRide");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("driver has ended the ride ");
            notificationRequestDto.setTitle("End ride");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(endDto.getDriverEmail());
        driver.setEndTripStatus("endRide");
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

    public List<MatcherDto2> getMatchPassengerByDriverEmail(String driverEmail) throws EntityNotFoundException {
        List<MatcherDto2> matcherDto2s = new ArrayList<>();
        List<Request> requests = requestRepository.getRequestByDriverEmailAndStatus(driverEmail, "matchFound");
        requests.forEach(request -> {
            User user = this.userRepository.findByEmail(request.getUserEmail()).orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
            Photo photo = this.photoRepository.findById(request.getUserEmail()).orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
            MatcherDto2 matcherDto2 = new MatcherDto2();
            matcherDto2.setUserEmail(request.getUserEmail());
            matcherDto2.setFirstName(user.getFirstName());
            matcherDto2.setLastName(user.getLastName());
            matcherDto2.setImage(photo.getImage());
            matcherDto2.setPhoneNumber(user.getPhone());
            matcherDto2.setStatus(request.getStatus());
            matcherDto2.setLatitudePassengerFrom(request.getLatitudePassengerFrom());
            matcherDto2.setLongitudePassengerFrom(request.getLongitudePassengerFrom());
            matcherDto2.setLatitudePassengerTo(request.getLatitudePassengerTo());
            matcherDto2.setLongitudePassengerTo(request.getLongitudePassengerTo());
            matcherDto2.setTimeOfPickUp(request.getTimeOfPickUp());
            matcherDto2.setAmountPaid(request.getAmountPaid());
            matcherDto2.setDropOffAddress(request.getDropOffAddress());
            matcherDto2.setPickUpAddress(request.getPickUpAddress());
            matcherDto2.setDistance(request.getDistance());
            matcherDto2.setDuration(request.getDuration());
            matcherDto2.setDropOffTime(request.getDropOffTime());
            matcherDto2s.add(matcherDto2);
        });


        return matcherDto2s;

    }


    @Transactional
    public void notAccepted(String driverEmail, String passengerEmail) throws EntityNotFoundException {
        Request request = this.requestRepository.findByDriverEmailAndUserEmailAndStatus(driverEmail, passengerEmail, "matchFound").orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
        // driverDO.setAcceptStatus("Accepted");
        request.setStatus("notaccepted");

        Optional<User> user = this.userRepository.findByEmail(request.getUserEmail());
        if (user.isPresent()) {
            User user1 = user.get();
            user1.setAcceptStatus("notaccepted");
            this.userRepository.save(user1);
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setFcmToken(user1.getPassengerFCMToken());
            notificationRequestDto.setBody("your request has been not been accepted from " + findDriverByEmail(driverEmail).getFirstName() + " " + findDriverByEmail(driverEmail).getLastName());
            notificationRequestDto.setTitle("rejected request");
            this.notificationService.sendPnsToDevice(notificationRequestDto);
        }
        Driver driver = findDriverByEmail(driverEmail);
        driver.setRejectStatus("notaccepted");
        driverRepository.save(driver);
        this.requestRepository.save(request);
    }


    public List<MatcherDto2> getAllAccepted(String driverEmail) throws EntityNotFoundException {
        List<MatcherDto2> matcherDto2s = new ArrayList<>();
        List<Request> requests = requestRepository.getRequestByDriverEmailAndStatuss(driverEmail, "accepted", "startRide");
        requests.forEach(request -> {
            User user = this.userRepository.findByEmail(request.getUserEmail()).orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
            Photo photo = this.photoRepository.findById(request.getUserEmail()).orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
            MatcherDto2 matcherDto2 = new MatcherDto2();
            matcherDto2.setUserEmail(request.getUserEmail());
            matcherDto2.setFirstName(user.getFirstName());
            matcherDto2.setLastName(user.getLastName());
            matcherDto2.setImage(photo.getImage());
            matcherDto2.setPhoneNumber(user.getPhone());
            matcherDto2.setStatus(request.getStatus());
            matcherDto2.setLatitudePassengerFrom(request.getLatitudePassengerFrom());
            matcherDto2.setLongitudePassengerFrom(request.getLongitudePassengerFrom());
            matcherDto2.setLatitudePassengerTo(request.getLatitudePassengerTo());
            matcherDto2.setLongitudePassengerTo(request.getLongitudePassengerTo());
            matcherDto2.setTimeOfPickUp(request.getTimeOfPickUp());
            matcherDto2.setAmountPaid(request.getAmountPaid());
            matcherDto2.setDropOffAddress(request.getDropOffAddress());
            matcherDto2.setPickUpAddress(request.getPickUpAddress());
            matcherDto2.setDistance(request.getDistance());
            matcherDto2.setDuration(request.getDuration());
            matcherDto2.setDropOffTime(request.getDropOffTime());
            matcherDto2s.add(matcherDto2);
        });


        return matcherDto2s;

    }

//    public List<MatcherDto2> getAllStartedTrip(String driverEmail) throws EntityNotFoundException {
//        List<MatcherDto2> matcherDto2s = new ArrayList<>();
//        List<Request> requests = requestRepository.getRequestByDriverEmailAndStatus(driverEmail, "accepted");
//        requests.forEach(request -> {
//            User user = this.userRepository.findByEmail(request.getUserEmail()).orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
//            Photo photo = this.photoRepository.findById(request.getUserEmail()).orElseThrow(() -> new EntityNotFoundException("Could not find entity with id: "));
//            MatcherDto2 matcherDto2 = new MatcherDto2();
//            matcherDto2.setUserEmail(request.getUserEmail());
//            matcherDto2.setFirstName(user.getFirstName());
//            matcherDto2.setLastName(user.getLastName());
//            matcherDto2.setImage(photo.getImage());
//            matcherDto2.setPhoneNumber(user.getPhone());
//            matcherDto2.setStatus(request.getStatus());
//            matcherDto2.setLatitudePassengerFrom(request.getLatitudePassengerFrom());
//            matcherDto2.setLongitudePassengerFrom(request.getLongitudePassengerFrom());
//            matcherDto2.setLatitudePassengerTo(request.getLatitudePassengerTo());
//            matcherDto2.setLongitudePassengerTo(request.getLongitudePassengerTo());
//            matcherDto2.setTimeOfPickUp(request.getTimeOfPickUp());
//            matcherDto2.setAmountPaid(request.getAmountPaid());
//            matcherDto2.setDropOffAddress(request.getDropOffAddress());
//            matcherDto2.setPickUpAddress(request.getPickUpAddress());
//            matcherDto2.setDistance(request.getDistance());
//            matcherDto2.setDuration(request.getDuration());
//            matcherDto2.setDropOffTime(request.getDropOffTime());
//            matcherDto2s.add(matcherDto2);
//        });
//
//
//        return matcherDto2s;
//
//    }
}
