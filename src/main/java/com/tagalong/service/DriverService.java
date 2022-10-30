package com.tagalong.service;


import com.tagalong.dto.DriverUpdateLocation;
import com.tagalong.dto.OnlineStatus;
import com.tagalong.exception.CarAlreadyInUseException;
import com.tagalong.exception.ConstraintsViolationException;
import com.tagalong.model.Driver;
import com.tagalong.model.repository.DriverRepository;
import com.tagalong.model.repository.UserRepository;
import com.tagalong.model.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.awt.*;
import java.util.List;

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

    public Driver create(Driver driverDO) throws ConstraintsViolationException {
        Driver driver = new Driver();
        try {

            driverDO.setVehicleSeat(3);
            driverDO.setPassword(encoder.encode(driverDO.getPassword()));
            driver = driverRepository.save(driverDO);

            User user = new User();
            user.setFirstName(driverDO.getFirstName());
            user.setLastName(driverDO.getLastName());
            user.setEmail(driverDO.getEmail());
            user.setPassword(encoder.encode(driverDO.getPassword()));
            user.setPhone(driverDO.getPhoneNumber());
            user.setVerified(driverDO.getVerified());
            this.userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            LOG.warn("Some constraints are thrown due to driver creation", e);
            throw new ConstraintsViolationException(e.getMessage());
        }
        return driver;
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
        driverRepository.save(driverDO);
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


}
