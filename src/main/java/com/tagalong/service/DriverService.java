package com.tagalong.service;


import com.tagalong.dto.OnlineStatus;
import com.tagalong.exception.CarAlreadyInUseException;
import com.tagalong.exception.ConstraintsViolationException;
import com.tagalong.model.Driver;
import com.tagalong.model.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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
            driver.setVehicleSeat("3");
            driver = driverRepository.save(driverDO);
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


    /**
     * Update the location for a driver.
     *
     * @param driverId
     * @param longitude
     * @param latitude
     * @throws EntityNotFoundException
     */

    @Transactional
    public void updateLocation(long driverId, double longitude, double latitude, OnlineStatus onlineStatus) throws EntityNotFoundException {
        Driver driverDO = findDriverChecked(driverId);
        driverDO.setLatitudeDriverFrom(latitude);
        driverDO.setLongitudeDriverFrom(longitude);
        driverDO.setOnlineStatus(onlineStatus);
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
